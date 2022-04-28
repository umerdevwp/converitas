package com.coveritas.heracles.ui

class View {
    String uuid                 // View ID in backend

    String name
    String description
    Project project
    Set<Company> companies
    static hasMany = [companyViewObjects:CompanyViewObject, viewObjects:ViewObject]

    def onLoad(){
        companyViewObjects.each { CompanyViewObject cvo -> companies << cvo.company}
    }

    static mapping = {
        table name: 'ma_view'
    }

    static constraints = {
        id generator : 'increment'
        uuid nullable: false, blank: false, unique: true
        name nullable: false, unique: ['project']
    }

    static transients = ['companies']

    @Override
    String toString() { name }
}
