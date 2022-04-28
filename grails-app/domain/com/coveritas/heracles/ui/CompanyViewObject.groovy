package com.coveritas.heracles.ui

class CompanyViewObject extends ViewObject {
    final static String UNKNOWN = 'unknown'
    final static String TRACKING = 'tracking'
    final static String WATCHING = 'watching'
    final static String SURFACING = 'surfacing'
    final static String IGNORING = 'ignoring'
    final static String REMOVING = 'removing'
    final static List<String> LEVELS = [UNKNOWN, TRACKING, WATCHING, SURFACING, IGNORING, REMOVING]

    Company company
    String level

    static mapping = {
        table name: 'ma_company_view'
    }

    static constraints = {
        company nullable: false

        level nullable: false, inList: LEVELS
    }

    @Override
    public String toString() {
        return "$company"+(view == null) ? "" : "@$view'";
    }
}
