package sk.evysetrenie.api

import android.content.SharedPreferences
import sk.evysetrenie.api.model.Doctor
import sk.evysetrenie.api.model.Specialisation
import sk.evysetrenie.api.model.User
import sk.evysetrenie.api.model.contracts.responses.LoginResponse

object AuthState {
    private var loggedInUser: User? = null
    private var loggedInDoctor: Doctor? = null
    private var isDoctor: Boolean? = null

    private var accessToken: String? = null

    var sharedPreferences: SharedPreferences? = null

    fun login(loginResponse: LoginResponse) {
        setSharedPreferences(loginResponse)
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

    fun getLoggedInDoctor() : Doctor {
        return loggedInDoctor!!
    }

    fun isDoctor(): Boolean? {
        return isDoctor
    }

    fun isLoggedIn() : Boolean {
        if (isDoctor == null) {
            checkSharedPreferences()
        }

        return isDoctor != null
    }

    private fun setSharedPreferences(loginResponse: LoginResponse) {
        val editor = sharedPreferences!!.edit()

        editor.putString("accessToken", loginResponse.access_token)

        if (loginResponse.user !== null) {
            editor.putBoolean("isDoctor", false)
            val user = loginResponse.user
            editor.putInt("id", user.id!!)
            editor.putString("name", user.name)
            editor.putString("surname", user.surname)
            editor.putString("email", user.email)
            editor.putString("phone", user.phone)
        } else {
            editor.putBoolean("isDoctor", true)
            val doctor = loginResponse.doctor!!
            editor.putInt("id", doctor.id!!)
            editor.putString("name", doctor.name)
            editor.putString("surname", doctor.surname)
            editor.putString("title", doctor.title)
            editor.putString("email", doctor.email)
            editor.putString("phone", doctor.phone)
            editor.putInt("specialisation.id", doctor.specialisation.id)
            editor.putString("specialisation.title", doctor.specialisation.title)
            editor.putInt("appointments_length", doctor.appointments_length)
            editor.putString("address", doctor.address)
            editor.putString("city", doctor.city)
            editor.putString("description", doctor.description)
        }
        editor.apply()
    }

    private fun checkSharedPreferences() {
       if (sharedPreferences == null)
           return
       if (sharedPreferences!!.contains("accessToken")) {
           accessToken = sharedPreferences!!.getString("accessToken", null)
           isDoctor = sharedPreferences!!.getBoolean("isDoctor", false)
           if (isDoctor!!) {
               loggedInUser = null
               loggedInDoctor = Doctor(
                   sharedPreferences!!.getInt("id", 0),
                   sharedPreferences!!.getString("name", "")!!,
                   sharedPreferences!!.getString("surname", "")!!,
                   sharedPreferences!!.getString("title", "")!!,
                   sharedPreferences!!.getString("email", "")!!,
                   sharedPreferences!!.getString("phone", "")!!,
                   Specialisation(sharedPreferences!!.getInt("specialisation.id", 0), sharedPreferences!!.getString("specialisation.title", "")!!),
                   sharedPreferences!!.getInt("appointments_length", 0),
                   sharedPreferences!!.getString("address", "")!!,
                   sharedPreferences!!.getString("city", "")!!,
                   sharedPreferences!!.getString("description", "")!!,
                   null
               )
           } else {
               loggedInUser = User(
                   sharedPreferences!!.getInt("id", 0),
                   sharedPreferences!!.getString("name", "")!!,
                   sharedPreferences!!.getString("surname", "")!!,
                   sharedPreferences!!.getString("email", "")!!,
                   sharedPreferences!!.getString("phone", "")!!
               )
               loggedInDoctor = null
           }
       }
    }

    fun logout() {
        if (sharedPreferences !== null) {
            sharedPreferences!!.edit().clear().apply()
        }
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