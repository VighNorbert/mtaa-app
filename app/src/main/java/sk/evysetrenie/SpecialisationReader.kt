package sk.evysetrenie

import sk.evysetrenie.api.model.Specialisation
import sk.evysetrenie.api.model.contracts.responses.ApiError

abstract class SpecialisationReader : BaseActivity() {
    abstract fun getAllSpecialisationSuccess(specialisations: Array<Specialisation>)

    abstract fun showError(error: ApiError)
}