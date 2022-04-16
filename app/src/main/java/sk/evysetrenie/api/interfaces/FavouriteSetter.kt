package sk.evysetrenie.api.interfaces

import sk.evysetrenie.api.model.contracts.responses.ApiError

interface FavouriteSetter {
    fun showError(error: ApiError)
}