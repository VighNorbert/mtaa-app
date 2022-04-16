package sk.evysetrenie.api

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.*
import okio.IOException
import sk.evysetrenie.BaseActivity
import sk.evysetrenie.api.interfaces.SpecialisationReader
import sk.evysetrenie.api.model.Specialisation
import sk.evysetrenie.api.model.contracts.responses.ErrorResponse
import sk.evysetrenie.api.model.contracts.responses.ApiError
import java.lang.Exception

class SpecialisationService {

    private var specialisations: Array<Specialisation>? = null

    fun getAll(reader: SpecialisationReader? = null, activity: BaseActivity? = null) {
        if (specialisations == null) {
            val request = Request.Builder()
                .url(Constants.API_URL + "specialisations")
                .method("GET", null)
                .addHeader("accept", "application/json")
                .build()

            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    activity?.runOnUiThread { reader?.showError(ApiError(400)) }
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            try {
                                val error = Json.decodeFromString<ErrorResponse>(response.body!!.string())
                                activity?.runOnUiThread { reader?.showError(error.error) }
                            } catch (e: Exception) {
                                activity?.runOnUiThread { reader?.showError(ApiError(response.code)) }
                            }
                        } else {
                            specialisations = Json.decodeFromString<Array<Specialisation>>(response.body!!.string())
                            activity?.runOnUiThread {
                                reader?.getAllSpecialisationSuccess(specialisations!!)
                            }
                        }
                    }
                }
            })
        } else {
            activity?.runOnUiThread {
                reader?.getAllSpecialisationSuccess(specialisations!!)
            }
        }
    }

    fun getByTitle(title: String) : Specialisation? {
        if (specialisations == null)
            getAll()

        return specialisations?.find { s: Specialisation -> s.title == title }
    }


}