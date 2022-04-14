package com.coveritas.heracles

import com.coveritas.heracles.utils.APIException
import com.coveritas.heracles.utils.Meta
import com.google.gson.Gson
import grails.config.Config
import grails.converters.JSON
import grails.core.support.GrailsConfigurationAware
import groovy.json.JsonSlurper
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import kong.unirest.ContentType
import kong.unirest.HeaderNames
import kong.unirest.HttpResponse
import kong.unirest.Unirest
import org.apache.commons.codec.Charsets

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

@CompileStatic
class HttpClientService implements GrailsConfigurationAware {
    static transactional = false
//  private JmxEndpointProperties.Exposure exposure = new JmxEndpointProperties.Exposure()
//  private String domain = "org.springframework.boot"

    static JsonSlurper jsonSlurper = new JsonSlurper()

    private grails.config.Config config
    private String server = null
    private String credential
    private Boolean isDebug
    private Boolean verifySsl


    HttpClientService() {
    }

    @Override
    @CompileDynamic
    void setConfiguration(Config config) {
        if (server == null) {
            server = config.getOrDefault("heracles.api.server", "http://localhost:8080")
            credential = config.getOrDefault("heracles.api.credential", "none")
            isDebug = config.getOrDefault("heracles.api.isDebug", Boolean.TRUE)
            verifySsl = config.apiProperties.getOrDefault("heracles.api.verifySsl", Boolean.FALSE)

            if (!Unirest.config().isRunning()) {
                Unirest.config()
                    .socketTimeout(60000)
                    .connectTimeout(30000)
//        .concurrency(200, 20)
                    .setDefaultHeader(HeaderNames.ACCEPT, "application/json")
//        .followRedirects(true)
                    .enableCookieManagement(false).verifySsl( /*conf.get("heracles.verifySsl", Boolean.class, false )*/ verifySsl)
            }
        }
    }

    /**
     * The method getUrl returns a string with th eURL to the eventing support service based on the task and
     * the base URL from the Lambda environment parameters.
     *
     * @param task contains the task that the eventing support service should accomplish with the call of this URL.
     *
     * @returns the URL of the eventing support service based on the required task.
     */
    private String getUrl(String task) {
        server + "/" + task/*+"?credential="+credential*/
    }


    @SuppressWarnings('GrMethodMayBeStatic')
    private void logRequestAsCurl(String method, Map<String, String> headers, String url, String body) {
//        if (isDebug) {
            StringJoiner hdrJoiner = new StringJoiner(" -H ")
            for (Map.Entry<String, String> e : headers.entrySet()) {
                hdrJoiner.add("'${e.getKey()}: ${e.getValue()}'")
            }
            StringJoiner curlJoiner = new StringJoiner(" ")
            curlJoiner.add("curl -X")
            curlJoiner.add(method)
            curlJoiner.add("-H " + hdrJoiner.toString())
            if (body != null) {
                curlJoiner.add("-d '${body}'")
            }
            curlJoiner.add(url)
            log.info(curlJoiner.toString())
//        }
    }

    /**
     * The method httpGetRequest performs a get request to the given URL
     *
     * @param url contains the url to be called
     *
     * @String with response for the call
     *
     * @throws IOException in case of low level errors with the call (e.g. network or server availability)
     */
    String httpGetRequest(String url) throws IOException {
        Map<String, String> completedHeaders = completedHeaders(null)
        logRequestAsCurl("GET", completedHeaders, url, null)
        HttpResponse<String> response = Unirest.get(url)
            .headers(completedHeaders)
            .asString()
        response.getBody()
    }

    /**
     * The method httpPostRequest performs a post request with the given stringified JSON to the given URL
     *
     * @param url contains the url to be called
     * @param json contains the Stringified JSON to be posted
     *
     * @String with response for the call
     *
     * @throws IOException in case of low level errors with the call (e.g. network or server availability)
     */
    String httpPostRequest(String url, String json) throws IOException, APIException {
        Map<String, String> completedHeaders = completedHeaders(null)
        logRequestAsCurl("POST", completedHeaders, url, json)
        HttpResponse<String> response = Unirest.post(url)
            .headers(completedHeaders)
            .body(json)
            .asString()
        int status = response.getStatus()
        if (status != 200) {
            throw new APIException(response.getStatusText(), status)
        }
        response.getBody()
    }


    String getParamsGeneric(String task, Map params) throws IOException {
        String url = getUrl(task) + paramsFromMap(params)
        String sResponse = httpGetRequest(url)
        if (isDebug) {
            log.debug("Response for get request to \"${url}\" is:\n ${sResponse}")
        }
        sResponse
    }

    /**
     * New backend API - always returns 200 with
     *
     *  [result: <Some object with expect value>] or [error: some message].
     *
     * anything other than a 200 is a fabric failure which we let call {} handle
     *
     * @param task
     * @param params
     * @return  value of result:
     */
    Object getParamsExpectResult(String task, Map params) throws IOException, APIException {
        Map<String, Object> temp = new Gson().fromJson(getParamsGeneric(task, params), Map)
        if (temp.error) {
            throw new APIException((String)temp.error)
        }
        else {
            if (! temp.keySet().contains('result')) {
                throw new APIException("Call to ${task} gave no result")
            }
            else {
                temp.result
            }
        }
    }

    Object postParamsExpectResult(String task, Map params) throws IOException, APIException {
        Map<String, Object> temp = new Gson().fromJson(postParamsGeneric(task, params), LinkedHashMap.class)
        if (temp.error) {
            throw new APIException((String)temp.error)
        }
        else {
            if (! temp.keySet().contains('result')) {
                throw new APIException("Call to ${task} gave no result")
            }
            else {
                temp.result
            }
        }
    }

    /**
     *
     * @param task
     * @param params
     * @param clazz
     * @return Instance of clazz with values filled in from result.
     * @throws IOException
     * @throws APIException
     */
    Object getParamsExpectObject(String task, Map params, Class clazz) throws IOException, APIException {
        Meta.fromMap(clazz, getParamsExpectResult(task, params) as Map<String, Object>)
    }

    /**
     *
     * @param task
     * @param params
     * @return result: which should be a map (i.e. not a List since those are the only options)
     * @throws IOException
     * @throws APIException
     */
    Map getParamsExpectMap(String task, Map params) throws IOException, APIException {
        getParamsExpectResult(task, params) as Map<String, Object>
    }


    /**
     * The method postParams performs a call to the eventing support service that returns a list with results of type clazz
     *
     * @param task Task that the call needs to accomplish.
     *
     * @param params map with names and values for call params (will be enriched with credentials and converted into JSON)
     * @param < T >  Type of the objects in the returned list
     * @List of type T with requested values
     * @throws IOException - mostly for low level errors (hopefully temporary)
     * @throws APIException - mostly for API level errors (hopefully never)
     */
    List getParamsExpectList(String task, Map params, Class clazz) throws IOException, APIException {
        typedListFromJson(getParamsExpectResult(task, params) as List<Map>, clazz)
    }

    private static String urlEncode(String s) { URLEncoder.encode(s, StandardCharsets.UTF_8) }

    private static String paramsFromMap(Map<String, Object> params) {
        params ? '?' + params.collect { "${urlEncode(it.key)}=${urlEncode(it.value.toString())}" }.join('&') : ''
    }

    private static <T> List<T> typedListFromJson(List<Map> list, Class<T> clazz) {
        list.collect {
            Meta.fromMap(clazz, it)
        } as List<T>
    }

    /**
     * The method postParams performs a call to the eventing support service that returns a list with results of type clazz
     *
     * @param task Task that the call needs to accomplish.
     *
     * @param params map with names and values for call params (will be enriched with credentials and converted into JSON)
     * @param < T >  Type of the objects in the returned list
     * @List of type T with requested values
     * @throws IOException - mostly for low level errors (hopefully temporary)
     * @throws APIException - mostly for API level errors (hopefully never)
     */
    List postParamsExpectList(String task, Map params, Class clazz) throws IOException, APIException {
        //typedListFromJson(postParamsGeneric(task, params), clazz)
    }

    /**
     * The method postParams performs a call to the eventing support service that returns a list with results of type clazz
     *
     * @param task Task that the call needs to accomplish.
     *
     * @param params map with names and values for call params (will be enriched with credentials and converted into JSON)
     * @param < T >  Type of the objects in the returned list
     * @List of type T with requested values
     * @throws IOException - mostly for low level errors (hopefully temporary)
     * @throws APIException - mostly for API level errors (hopefully never)
     */
    Map postParamsExpectMap(String task, Map params) throws IOException, APIException {
        postParamsExpectResult(task, params) as Map
    }

    private String postParamsGeneric(String task, Map params) throws IOException, APIException {
        Map<String, String> postParams = params == null ? new HashMap<String, String>() : new HashMap<String, String>(params as Map<? extends String, ? extends String>)
//    postParams.put("credential", credential)
//    postParams.put("rq", ""+System.currentTimeMillis())
//    String json = JsonStream.serialize(JSON_CFG_OMIT_DEFAULTS, postParams)
        String json = postParams as JSON
        String url = getUrl(task)
        String sResponse = httpPostRequest(url, json)
        if (isDebug) {
            log.debug("Response for request to \"${url}\" with JSON \"${json}\" is:\n${sResponse}")
            //System.out.println("Response for request to \""+url+"\" with JSON \""+json+"\" is:\n"+ sResponse)
        }
        sResponse
    }



    /**
     * The method httpGetRequest performs a get request to the given URL
     *
     * @param url contains the url to be called
     *
     * @String with response for the call
     *
     * @throws IOException in case of low level errors with the call (e.g. network or server availability)
     */
    String httpGetRequestAll(String url, Map<String, String> headers) throws IOException {
        logRequestAsCurl("GET", headers, url, null)
        HttpResponse<String> response = Unirest.get(url)
//        .header("Content-Type", "application/json")
            .header("Accept-Encoding", "gzip").headers(headers)
            .asString()
        response.getBody()
    }

    /**
     * The method httpOptionsRequest performs an OPTIONS request to the given URL
     *
     * @param url contains the url to be called
     * @param headers contains the map with header values, if "Content-Type" is not set it will be set as "application/json"
     *                and if "Accept-Encoding" is not set it will be set as "gzip"
     *
     * @String with response for the call
     *
     * @throws IOException in case of low level errors with the call (e.g. network or server availability)
     */
    String httpOptionsRequest(String url, Map<String, String> headers) throws IOException {
        Map<String, String> completedHeaders = completedHeaders(null)
        logRequestAsCurl("OPTIONS", completedHeaders, url, null)
        HttpResponse<String> response = Unirest.options(url)
            .headers(completedHeaders)
            .asString()
        response.getBody()
    }

    /**
     * The method httpHeadRequest performs an HEAD request to the given URL
     *
     * @param url contains the url to be called
     * @param headers contains the map with header values, if "Content-Type" is not set it will be set as "application/json"
     *                and if "Accept-Encoding" is not set it will be set as "gzip"
     *
     * @http status code
     *
     * @throws IOException in case of low level errors with the call (e.g. network or server availability)
     */
    int httpHeadRequest(String url, Map<String, String> headers) throws IOException {
        Map<String, String> completedHeaders = completedHeaders(null)
        logRequestAsCurl("OPTIONS", completedHeaders, url, null)
        HttpResponse<String> response = Unirest.options(url)
            .headers(completedHeaders)
            .asString()
        response.getStatus()
    }

    /**
     * The method httpPutRequest performs a post request with the given stringified JSON to the given URL
     *
     * @param url contains the url to be called
     * @param body contains the String to be posted as body
     * @param headers contains the map with header values, if "Content-Type" is not set it will be set as "application/json"
     *                and if "Accept-Encoding" is not set it will be set as "gzip"
     *
     *
     * @String with response for the call
     *
     * @throws IOException in case of low level errors with the call (e.g. network or server availability)
     */
    String httpPutRequest(String url, String body, Map<String, String> headers) {
        Map<String, String> completedHeaders = completedHeaders(headers)
        logRequestAsCurl("PUT", completedHeaders, url, body)
        HttpResponse<String> response = Unirest.put(url)
            .headers(completedHeaders)
            .body(body)
            .asString()
        response.getBody()
    }
    /**
     * The method completeHeaders returns a new Map with the same header that is in the params, except that
     * if "Content-Type" is not set it will be set in the returned header map as "application/json"
     * and if "Accept-Encoding" is not set it will be set in the returned header map as "gzip".
     *
     * @param headers contains the map with header values, if "Content-Type" is not set it will be set as "application/json"
     *                and if "Accept-Encoding" is not set it will be set as "gzip"
     *
     * @a new Map with the same header that is in the params, except that
     *         if "Content-Type" is not set it will be set in the returned header map as "application/json"
     *         and if "Accept-Encoding" is not set it will be set in the returned header map as "gzip".
     */
    private Map<String, String> completedHeaders(Map<String, String> headers) {
        Map<String, String> newHeaders = headers == null ? new HashMap<String, String>() : new HashMap<>(headers)
        newHeaders.putIfAbsent(HeaderNames.CONTENT_TYPE, "application/json")
        newHeaders.putIfAbsent(HeaderNames.ACCEPT_ENCODING, "gzip")
        newHeaders
    }
}
