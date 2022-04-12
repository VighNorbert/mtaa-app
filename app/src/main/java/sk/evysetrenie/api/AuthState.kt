package sk.evysetrenie.api

import android.content.SharedPreferences
import sk.evysetrenie.api.model.Doctor
import sk.evysetrenie.api.model.User
import sk.evysetrenie.api.model.contracts.responses.LoginResponse

object AuthState {
    private var loggedInUser: User? = null
    private var loggedInDoctor: Doctor? = null
    private var isDoctor: Boolean? = null

    private var accessToken: String? = null


    fun login(loginResponse: LoginResponse) {
        accessToken = loginResponse.access_token
        if (loginResponse.user !== null) {
            loggedInUser = loginResponse.user
            loggedInDoctor = null
            isDoctor = false
        } else {
            loggedInUser = null
            loggedInDoctor = loginResponse.doctor
            isDoctor = true
        }
    }

    fun getLoggedIn() : User? {
        if (isDoctor == false) {
            return loggedInUser
        } else if (isDoctor == true) {
            return User(loggedInDoctor!!.id, loggedInDoctor!!.name, loggedInDoctor!!.surname, loggedInDoctor!!.email, loggedInDoctor!!.phone)
        }
        return null
    }

    fun isLoggedIn() : Boolean {
        return isDoctor != null
    }

    fun logout() {
        loggedInUser = null
        loggedInDoctor = null
        isDoctor = null
        accessToken = null
    }

    fun getAccessToken() : String {
        if (accessToken != null) {
            return accessToken as String
        }
        return ""
    }
}