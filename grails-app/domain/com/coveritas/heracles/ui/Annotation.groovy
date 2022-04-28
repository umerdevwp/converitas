package com.coveritas.heracles.ui

class Annotation extends ViewObject {
    String annotationType  // could be description, note, constraint?
    String title
    String textContent
    static hasOne = [annotatedVO:ViewObject]

    static mapping = {
        table name: 'ma_annotation'
    }

    static constraints = {
    }

    @Override
    String toString() {
        return "$title" ((annotatedVO==null)?"":" note for '${annotatedVO}'");
    }
}
