package sk.evysetrenie.api.interfaces

import android.graphics.Bitmap
import sk.evysetrenie.api.model.contracts.responses.ApiError

interface AvatarReader {

    fun showError(error: ApiError)

    fun runOnUiThread(action: Runnable?)

    fun avatarReceived(bmp: Bitmap?)
}