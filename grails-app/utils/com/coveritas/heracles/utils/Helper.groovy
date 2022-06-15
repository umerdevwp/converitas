package com.coveritas.heracles.utils

import com.coveritas.heracles.ui.User
import org.apache.http.HttpStatus

import javax.servlet.http.HttpSession

class Helper {
    static User userFromSession(HttpSession session) {
        Long userID = session['userID'] as Long
        (userID==null)?null:User.get(userID)
    }

    static User apiUserFromSession(HttpSession session) {
        Long userID = session['userID'] as Long
        if (userID==null) {
            throw new SessionTimedOutException("User not logged in")
        }
        User.get(userID)
    }
}
