package sk.evysetrenie.api

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import sk.evysetrenie.DoctorsActivity
import sk.evysetrenie.api.model.contracts.requests.DoctorsRequest
import sk.evysetrenie.api.model.contracts.responses.DoctorsResponse
import sk.evysetrenie.api.model.contracts.responses.ErrorResponse


class DoctorsService {

    fun getCollection(doctorsRequest: DoctorsRequest, activity: DoctorsActivity) {
        val weburl = "https://api.norb.sk/doctors"
        val urlBuilder = weburl.toHttpUrl().newBuilder()
        if (doctorsRequest.name != null) { urlBuilder.addQueryParameter("name", doctorsRequest.name) }
        if (doctorsRequest.specialisation != null) { urlBuilder.addQueryParameter("specialisation", doctorsRequest.specialisation.toString()) }
        if (doctorsRequest.city != null) { urlBuilder.addQueryParameter("city", doctorsRequest.city) }
        urlBuilder.addQueryParameter("only_favourites", doctorsRequest.only_favourites.toString())
            .addQueryParameter("page", doctorsRequest.page.toString())
            .addQueryParameter("per_page", doctorsRequest.per_page.toString())
        val url = urlBuilder.build()
        val request = Request.Builder()
            .url(url)
            .addHeader("accept", "application/json")
            .addHeader("x-auth-token", AuthState.getAccessToken())
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
//                activity.runOnUiThread { activity.showError(ApiError(400)) }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        try {
                            val error = Json.decodeFromString<ErrorResponse>(response.body!!.string())
//                            activity.runOnUiThread { activity.showError(error.error) }
                        } catch (e: Exception) {
//                            activity.runOnUiThread { activity.showError(ApiError(response.code)) }
                        }
                    } else {
                        val res = Json.decodeFromString<List<DoctorsResponse>>(response.body!!.string())
                        activity.runOnUiThread { activity.dataReceived(res) }
                    }
                }
            }
        })
    }

    fun addToFavourites(doctor_id: Int) {
        println("Adding $doctor_id")
        val body = "{}".toRequestBody()
        val weburl = "https://api.norb.sk/doctor/$doctor_id/favourite"
        val request = Request.Builder()
            .url(weburl)
            .method("POST", body)
            .addHeader("accept", "*/*")
            .addHeader("x-auth-token", AuthState.getAccessToken())
            .addHeader("Content-Type", "application/json")
            .build()
        sendFavouritesRequest(request)
    }

    fun removeFromFavourites(doctor_id: Int) {
        val weburl = "https://api.norb.sk/doctor/$doctor_id/favourite"
        val request = Request.Builder()
            .url(weburl)
            .method("DELETE", null)
            .addHeader("Content-Length", "0")
            .addHeader("accept", "*/*")
            .addHeader("x-auth-token", AuthState.getAccessToken())
            .build()
        sendFavouritesRequest(request)
    }

    fun sendFavouritesRequest(request: Request) {
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
//                activity.runOnUiThread { activity.showError(ApiError(400)) }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        try {
                            val error = Json.decodeFromString<ErrorResponse>(response.body!!.string())
//                            activity.runOnUiThread { activity.showError(error.error) }
                        } catch (e: Exception) {
//                            activity.runOnUiThread { activity.showError(ApiError(response.code)) }
                        }
                    } else {
//                        activity.runOnUiThread { activity.dataReceived(res) }
                    }
                }
            }
        })
    }

}