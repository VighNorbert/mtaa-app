package sk.evysetrenie.api

import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import sk.evysetrenie.LoginActivity
import sk.evysetrenie.MyProfileActivity
import sk.evysetrenie.RegisterDoctorActivity
import sk.evysetrenie.RegisterPatientActivity
import sk.evysetrenie.api.model.contracts.responses.ErrorResponse
import sk.evysetrenie.api.model.contracts.requests.LoginRequest
import sk.evysetrenie.api.model.contracts.requests.RegisterDoctorRequest
import sk.evysetrenie.api.model.contracts.requests.RegisterRequest
import sk.evysetrenie.api.model.contracts.responses.ApiError
import sk.evysetrenie.api.model.contracts.responses.RegisterResponse
import java.lang.Exception
import java.security.MessageDigest

class AuthService {

    private fun hash(password: String, algorithm: String = "SHA_256"): String {
        return MessageDigest
            .getInstance("SHA-256")
            .digest(password.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }

    fun authenticate(loginRequest: LoginRequest, activity: LoginActivity) {
        loginRequest.password = hash(loginRequest.password)
        val body = Json.encodeToString(loginRequest)

        val request = Request.Builder()
            .url(Constants.API_URL + "login")
            .method("POST", body.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .addHeader("accept", "application/json")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                activity.runOnUiThread { activity.showError(ApiError(400)) }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        try {
                            val error = Json.decodeFromString<ErrorResponse>(response.body!!.string())
                            activity.runOnUiThread { activity.showError(error.error) }
                        } catch (e: Exception) {
                            activity.runOnUiThread { activity.showError(ApiError(response.code)) }
                        }
                    } else {
                        AuthState.login(Json.decodeFromString(response.body!!.string()))
                        activity.runOnUiThread { activity.successfulLogin() }

                    }
                }
            }
        })
    }

    fun register(registerRequest: RegisterRequest, activity: RegisterPatientActivity) {
        registerRequest.password = hash(registerRequest.password)
        val body = Json.encodeToString(registerRequest)

        val request = Request.Builder()
            .url(Constants.API_URL + "register")
            .method("POST", body.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .addHeader("accept", "application/json")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                activity.runOnUiThread { activity.showError(ApiError(400)) }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        try {
                            val error = Json.decodeFromString<ErrorResponse>(response.body!!.string())
                            activity.runOnUiThread { activity.showError(error.error) }
                        } catch (e: Exception) {
                            activity.runOnUiThread { activity.showError(ApiError(response.code)) }
                        }
                    } else {
                        val rr = Json.decodeFromString<RegisterResponse>(response.body!!.string())
                        activity.runOnUiThread { activity.successfulRegistration(rr.id) }
                    }
                }
            }
        })
    }

    fun registerDoctor(registerDoctorRequest: RegisterDoctorRequest, activity: RegisterDoctorActivity) {
        registerDoctorRequest.password = hash(registerDoctorRequest.password!!)
        val body = Json.encodeToString(registerDoctorRequest)

        println(body.replace(",\"",",\n\""))
        println(body.length)

        val request = Request.Builder()
            .url(Constants.API_URL + "register-doctor")
            .method("POST", body.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .addHeader("accept", "application/json")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                activity.runOnUiThread { activity.showError(ApiError(400)) }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        try {
                            val error = Json.decodeFromString<ErrorResponse>(response.body!!.string())
                            activity.runOnUiThread { activity.showError(error.error) }
                        } catch (e: Exception) {
                            activity.runOnUiThread { activity.showError(ApiError(response.code)) }
                        }
                    } else {
                        Json.decodeFromString<RegisterResponse>(response.body!!.string())
                        activity.runOnUiThread { activity.successfulRegistration() }
                    }
                }
            }
        })
    }

    fun editProfile(registerDoctorRequest: RegisterDoctorRequest, activity: MyProfileActivity) {
        if (registerDoctorRequest.password !== null)
            registerDoctorRequest.password = hash(registerDoctorRequest.password!!)
        val body = Json.encodeToString(registerDoctorRequest)

        println(body.replace(",\"",",\n\""))
        println(body.length)

        val request = Request.Builder()
            .url(Constants.API_URL + "profile")
            .method("PUT", body.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .addHeader("accept", "application/json")
            .addHeader("x-auth-token", AuthState.getAccessToken())
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                activity.runOnUiThread { activity.showError(ApiError(400)) }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        if (response.code == 401) {
                            activity.logout()
                        } else {
                            try {
                                val error = Json.decodeFromString<ErrorResponse>(response.body!!.string())
                                activity.runOnUiThread { activity.showError(error.error) }
                            } catch (e: Exception) {
                                activity.runOnUiThread { activity.showError(ApiError(response.code)) }
                            }
                        }
                    } else {
                        activity.runOnUiThread { activity.successfullyChanged() }
                    }
                }
            }
        })
    }

}