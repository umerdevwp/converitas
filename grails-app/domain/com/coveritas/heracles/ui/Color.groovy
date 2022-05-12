package com.coveritas.heracles.ui

class Color {
    Long id
    String name
    String code

    static mapping = {
        table name: 'ma_color'
    }

    static constraints = {
        id generator : 'increment'
        name blank: false, unique:true
        code blank: false
    }

    @Override
    String toString() {
        return "$name ($code)"
    }
}
