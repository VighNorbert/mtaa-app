package sk.evysetrenie.api

import android.graphics.BitmapFactory
import android.widget.Toast
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import sk.evysetrenie.BaseActivity
import sk.evysetrenie.DoctorsActivity
import sk.evysetrenie.DoctorsDetailActivity
import sk.evysetrenie.api.interfaces.AvatarReader
import sk.evysetrenie.api.interfaces.DoctorsDetailReader
import sk.evysetrenie.api.interfaces.FavouriteSetter
import sk.evysetrenie.api.model.contracts.requests.AppointmentRequest
import sk.evysetrenie.api.model.contracts.requests.DoctorsRequest
import sk.evysetrenie.api.model.contracts.responses.*


class DoctorsService {

    fun getCollection(doctorsRequest: DoctorsRequest, activity: DoctorsActivity) {
        val weburl = Constants.API_URL + "doctors"
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
                        val res = Json.decodeFromString<List<DoctorsResponse>>(response.body!!.string())
                        activity.runOnUiThread { activity.dataReceived(res) }
                    }
                }
            }
        })
    }

    fun getDetail(doctor_id: Int, activity: DoctorsDetailReader) {
        val request = Request.Builder()
            .url(Constants.API_URL + "doctor/$doctor_id")
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
                            activity.logout(true)
                        } else {
                            try {
                                val error = Json.decodeFromString<ErrorResponse>(response.body!!.string())
                                activity.runOnUiThread { activity.showError(error.error) }
                            } catch (e: Exception) {
                                activity.runOnUiThread { activity.showError(ApiError(response.code)) }
                            }
                        }
                    } else {
                        val res = Json.decodeFromString<DoctorsDetailResponse>(response.body!!.string())
                        activity.runOnUiThread { activity.dataReceived(res) }
                    }
                }
            }
        })
    }

    fun getAvatar(doctor_id: Int, activity: AvatarReader) {
        val request = Request.Builder()
            .url(Constants.API_URL + "doctor/$doctor_id/avatar")
            .addHeader("accept", "image/*")
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
                            activity.logout(true)
                        } else if (response.code == 404) {
                            activity.runOnUiThread { activity.avatarReceived(null) }
                        }
                        else {
                            try {
                                val error = Json.decodeFromString<ErrorResponse>(response.body!!.string())
                                activity.runOnUiThread { activity.showError(error.error) }
                            } catch (e: Exception) {
                                activity.runOnUiThread { activity.showError(ApiError(response.code)) }
                            }
                        }
                    } else {
                        val bmp = BitmapFactory.decodeStream(response.body!!.byteStream())
                        activity.runOnUiThread { activity.avatarReceived(bmp) }
                    }
                }
            }
        })
    }

    fun addToFavourites(doctor_id: Int, setter: FavouriteSetter, activity: BaseActivity) {
        val body = "{}".toRequestBody()
        val weburl = Constants.API_URL + "doctor/$doctor_id/favourite"
        val request = Request.Builder()
            .url(weburl)
            .method("POST", body)
            .addHeader("accept", "*/*")
            .addHeader("x-auth-token", AuthState.getAccessToken())
            .addHeader("Content-Type", "application/json")
            .build()
        sendFavouritesRequest(request, setter, activity, true)
    }

    fun removeFromFavourites(doctor_id: Int, setter: FavouriteSetter, activity: BaseActivity) {
        val weburl = Constants.API_URL + "doctor/$doctor_id/favourite"
        val request = Request.Builder()
            .url(weburl)
            .method("DELETE", null)
            .addHeader("Content-Length", "0")
            .addHeader("accept", "*/*")
            .addHeader("x-auth-token", AuthState.getAccessToken())
            .build()
        sendFavouritesRequest(request, setter, activity, false)
    }

    fun sendFavouritesRequest(request: Request, setter: FavouriteSetter, activity: BaseActivity, add: Boolean) {
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                activity.runOnUiThread { setter.showError(ApiError(400)) }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        if (response.code == 401) {
                            activity.logout()
                        } else {
                            try {
                                val error = Json.decodeFromString<ErrorResponse>(response.body!!.string())
                                activity.runOnUiThread { setter.showError(error.error) }
                            } catch (e: Exception) {
                                activity.runOnUiThread { setter.showError(ApiError(response.code)) }
                            }
                        }
                    } else {
                        activity.runOnUiThread {
                            if (add) {
                                Toast.makeText(activity.applicationContext, "Lekár bol pridaný", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                Toast.makeText(activity.applicationContext, "Lekár bol odobraný", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        })
    }

    fun getDates(doctor_id: Int, month: Int, year: Int, activity: DoctorsDetailActivity) {
        val weburl = Constants.API_URL + "doctor/${doctor_id}/appointment/dates"
        val urlBuilder = weburl.toHttpUrl().newBuilder()
        urlBuilder.addQueryParameter("month", month.toString())
        urlBuilder.addQueryParameter("year", year.toString())
        val url = urlBuilder.build()
        val request = Request.Builder()
            .url(url)
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
                        val res = Json.decodeFromString<List<Int>>(response.body!!.string())
                        activity.runOnUiThread { activity.datesReceived(res, month, year) }
                    }
                }
            }
        })
    }

    fun getTimes(doctor_id: Int, day: Int, month: Int, year: Int, activity: DoctorsDetailActivity) {
        val weburl = Constants.API_URL + "doctor/${doctor_id}/appointment/times"
        val urlBuilder = weburl.toHttpUrl().newBuilder()
        urlBuilder.addQueryParameter("day", day.toString())
        urlBuilder.addQueryParameter("month", month.toString())
        urlBuilder.addQueryParameter("year", year.toString())
        val url = urlBuilder.build()
        val request = Request.Builder()
            .url(url)
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
                        val res = Json.decodeFromString<List<AppointmentTimesResponse>>(response.body!!.string())
                        activity.runOnUiThread { activity.timesReceived(res) }
                    }
                }
            }
        })
    }

    fun makeAppointment(doctorId: Int, appointmentId: Int, requestBody: AppointmentRequest, activity: DoctorsDetailActivity) {
        val body = Json.encodeToString(requestBody)

        val request = Request.Builder()
            .url(Constants.API_URL + "doctor/${doctorId}/appointment/${appointmentId}")
            .method("POST", body.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .addHeader("accept", "*/*")
            .addHeader("x-auth-token", AuthState.getAccessToken())
            .addHeader("Content-Type", "application/json")
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
                            val error =
                                Json.decodeFromString<ErrorResponse>(response.body!!.string())
                            activity.runOnUiThread { activity.showError(error.error) }
                        } catch (e: Exception) {
                            activity.runOnUiThread { activity.showError(ApiError(response.code)) }
                        }
                    } else {
                        activity.runOnUiThread { activity.appointmentSuccess() }
                    }
                }
            }
        })
    }

}