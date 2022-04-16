package sk.evysetrenie.api.interfaces

import sk.evysetrenie.api.model.contracts.responses.ApiError
import sk.evysetrenie.api.model.contracts.responses.DoctorsDetailResponse

interface DoctorsDetailReader {

    fun showError(error: ApiError)

    fun runOnUiThread(action: Runnable?)

    fun dataReceived(doctor: DoctorsDetailResponse)
}