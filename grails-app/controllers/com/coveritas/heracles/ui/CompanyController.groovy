package com.coveritas.heracles.ui

import com.coveritas.heracles.HttpClientService
import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class CompanyController {
    HttpClientService httpClientService
    CompanyService companyService
    ApiService apiService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond companyService.list(params), model:[companyCount: companyService.count()]
    }

    def show(Long id) {
        respond companyService.get(id)
    }

    def info(String uuid) {
        Company company = apiService.getCompanyFromAPI(uuid)

        redirect  url:"/company/show/${company.id}"
    }

    def create() {
        Company company = new Company(params)
        respond company
    }

    def save(Company company) {
        if (company == null) {
            notFound()
            return
        }

        try {
            Map<String, Object> result = httpClientService.getParamsExpectMap('company/resolve', [name:company.canonicalName, iso:company.countryIso], false)
            Map<String, Object> c = result.company as Map<String, Object>
            apiService.getCompanyFromAPI(c.uuid as String)
        } catch (ValidationException e) {
            respond company.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'company.label', default: 'Company'), company.id])
                redirect company
            }
            '*' { respond company, [status: CREATED] }
        }
    }

    def edit(Long id) {
        Company company = companyService.get(id)
        if (!company.overrideBackend) {
            company = apiService.getCompanyFromAPI(company.uuid)
        }
        respond company
    }

    def update(Company company) {
        if (company == null) {
            notFound()
            return
        }

        try {
            companyService.save(company)
        } catch (ValidationException e) {
            respond company.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'company.label', default: 'Company'), company.id])
                redirect company
            }
            '*'{ respond company, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        Company company = Company.get(id)
        company.deleted = true
        companyService.save(company)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'company.label', default: 'Company'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'company.label', default: 'Company'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
