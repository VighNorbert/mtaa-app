package sk.evysetrenie.api.interfaces

import sk.evysetrenie.api.model.Specialisation
import sk.evysetrenie.api.model.contracts.responses.ApiError

interface SpecialisationReader {
    fun getAllSpecialisationSuccess(specialisations: Array<Specialisation>)

    fun showError(error: ApiError)
}