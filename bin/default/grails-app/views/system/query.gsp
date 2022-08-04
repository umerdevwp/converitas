<%--
  Created by IntelliJ IDEA.
  User: alanl
  Date: 2/15/22
  Time: 10:13 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Business Metagraph Query Results</title>
    <asset:stylesheet src="vis-timeline-graph2d.min.css"/>
    <asset:stylesheet src="vis-network.min.css"/>
    <script src="https://d3js.org/d3.v3.min.js"></script>
    <script src="/assets/d3.layout.cloud.js"></script>
    <script src="/assets/wordcloud.js"></script>
    <script src="/assets/timeConverter.js"></script>
    <script src="/assets/tempcolor.js"></script>

    <style>

    .vis-item .vis-item-content {
        line-height: 14px;
        font-weight: normal;
        font-size: 12px;
        padding: 1px;
    }

    .article {
        font-size: 14pt;
        text-decoration: none;
        color: #4e555b;
    }

    .articletitle {
        font-size: 14pt;
        text-decoration: none;
        color: black;
    }
    </style>
</head>

<body>
<script type="module">
    import "/assets/vis-timeline-graph2d.min.js";
    import "/assets/vis-network.min.js";

    // Timeline ---------------------------------------------------------------------------------------------------------
    const items = new vis.DataSet();

    // Setting from/to options counts as a range change which triggers a reload which ... so
    // Use a click to indicate we are no longer initializing .. sigh
    var initializing = true;

    const container = document.getElementById('time_line');
    const options = {
        editable: true,
        height: 150,
        maxHeight: 300,
        start: ${from},
        end: ${to}
    };

    const timeline = new vis.Timeline(container, items, options);

    timeline.on('rangechanged', function(e) {
        if (! initializing) {
            var url = '/system/query?query=${raw(query)}';
            window.location =  encodeURI(url + "&from=" + e.start.getTime() + "&to=" +  e.end.getTime());
        }
    });

    timeline.on('doubleClick', function(props) {
        return true;
    });

    timeline.on('click', function(props) {
        initializing = false;
        return true;
    });

    timeline.on('mouseOver', function(props) {
        initializing = false;
        return true;
    });

    var from = ${from}, to = ${to};

    window.search = function() {
        const range = timeline.getWindow();
        const from = range.start.getTime(), to = range.end.getTime();
        window.location = encodeURI('/system/query?query=' + $('#q').val() + '&from=' + from + '&to=' + to);
    }



</script>
<content tag="navTitle">Business Metagraph Query Results</content>

<div id="timeLine" role="navigation">
    <div id="time_line"></div>
</div>

<div style="margin:5px">
    <h2>Query (Results in above Time Range): ${URLDecoder.decode(query, 'UTF-8')}</h2>
    <table>
        <tr><th style="width:10em">Published</th><th>Score</th><th>Source</th><th>Headline</th><th>Associations</th></tr>
        <g:each in="${articles.sort {it.score}.reverse()}">
            <tr>
                <td>${new Date((Long)it.contentTs).toString()[0..10]}</td>
                <td>${it.score.round(1)}</td>
                <td><a href="${it.source}">link</a></td>
                <td>${it.title}</td>
                <td>
                 <g:each in="${it.companies}" var="co">
                    <a href="/organization/info/${co[1]}?ts=${3600*1000+(Long)it.contentTs}">${co[0]}</a>
                </g:each>
                </td>
            </tr>
        </g:each>

    </table>
</div>
</body>
</html>