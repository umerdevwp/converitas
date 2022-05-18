package com.coveritas.heracles.ui

class Color {
    Long id
    String name
    String code

    static mapping = {
        table name: 'ma_color'
        id generator : 'sequence', params:[sequence:'seq_id_color_pk']
    }

    static constraints = {
        name blank: false, unique:true
        code blank: false
    }

    @Override
    String toString() {
        return "$name ($code)"
    }
}
