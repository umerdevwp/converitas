package com.coveritas.heracles.ui

class Annotation extends ViewObject {
    String annotationType  // could be description, note, constraint?
    String title
    String textContent
    byte[] binContent
    long ts
    User user
    static hasOne = [annotatedVO:ViewObject]
    String time = null


    String getTime() {
        if (time==null){
            time = ApiService.format.format(new Date(ts as long))
        }
        time
    }

    static mapping = {
        table name: 'ma_annotation'
    }

    static constraints = {
        textContent     nullable:true
        annotationType  nullable:true
        binContent      nullable:true
        ts              nullable:true
    }

    static transients = ['organization', 'time']
    private Organization organization = null
    Organization getOrganization() {
        if (organization==null) {
            organization = user.organization
        }
        organization
    }

    @Override
    String toString() {
        return "$title" /*((annotatedVO==null)?"":" note for '${annotatedVO}'")*/
    }
}
