package sk.evysetrenie.api

import android.os.Environment
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import sk.evysetrenie.LoginActivity
import sk.evysetrenie.RegisterDoctorActivity
import sk.evysetrenie.RegisterPatientActivity
import sk.evysetrenie.api.model.contracts.responses.ErrorResponse
import sk.evysetrenie.api.model.contracts.requests.LoginRequest
import sk.evysetrenie.api.model.contracts.requests.RegisterDoctorRequest
import sk.evysetrenie.api.model.contracts.requests.RegisterRequest
import sk.evysetrenie.api.model.contracts.responses.ApiError
import sk.evysetrenie.api.model.contracts.responses.RegisterResponse
import java.io.File
import java.lang.Exception

class AuthService {

    fun authenticate(loginRequest: LoginRequest, activity: LoginActivity) {
        val body = Json.encodeToString(loginRequest)

        val request = Request.Builder()
            .url("https://api.norb.sk/login")
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
        val body = Json.encodeToString(registerRequest)

        val request = Request.Builder()
            .url("https://api.norb.sk/register")
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
        val body = Json.encodeToString(registerDoctorRequest)

        println(body.replace(",\"",",\n\""))
        println(body.length)

        val request = Request.Builder()
            .url("https://api.norb.sk/register-doctor")
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

}