package com.coveritas.heracles.ui

class UserEvent {
    User user
    Long ts // = System.currentTimeMillis()
    String event

    public final static String E_LOGIN = 'login'
    public final static String E_LOGOUT = 'logout'
    public final static String E_FAILED = 'failed'
    public final static String E_PASSWORDCHANGE = 'passwordchange'

    final static List<String> STATES = [E_LOGIN, E_LOGOUT, E_FAILED, E_PASSWORDCHANGE]

    static constraints = {
        user nullable: true  // maybe empty on failed attempts for unknow nusers
        ts DefaultValue: "now()"
        event nullable: false, inList: STATES
    }

    static mapping = {
        table name: 'ma_user_event'
        id generator : 'sequence', params:[sequence:'seq_id_user_event_pk']
    }

    static transients = ['organization']
    private Organization organization = null
    Organization getOrganization() {
        if (organization==null) {
            organization = user.organization
        }
        organization
    }
}
