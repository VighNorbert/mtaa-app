package sk.evysetrenie.api

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.*
import okio.IOException
import sk.evysetrenie.AppointmentsActivity
import sk.evysetrenie.api.model.contracts.responses.ApiError
import sk.evysetrenie.api.model.contracts.responses.AppointmentResponse
import sk.evysetrenie.api.model.contracts.responses.ErrorResponse
import java.util.*

class AppointmentsService {

    fun getCollection(date: Date?, activity: AppointmentsActivity) {
        val request = Request.Builder()
            .url(Constants.API_URL + "appointments")
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
                        try {
                            val error = Json.decodeFromString<ErrorResponse>(response.body!!.string())
                            activity.runOnUiThread { activity.showError(error.error) }
                        } catch (e: Exception) {
                            activity.runOnUiThread { activity.showError(ApiError(response.code)) }
                        }
                    } else {
                        val res = Json.decodeFromString<List<AppointmentResponse>>(response.body!!.string())
                        activity.runOnUiThread { activity.dataReceived(res) }
                    }
                }
            }
        })
    }
}