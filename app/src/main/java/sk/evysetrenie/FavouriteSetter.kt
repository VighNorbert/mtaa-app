package sk.evysetrenie

import sk.evysetrenie.api.model.contracts.responses.ApiError

interface FavouriteSetter {
    fun showError(error: ApiError)
}