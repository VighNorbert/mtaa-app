package sk.evysetrenie

import android.content.ContentResolver
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract

interface ProfileEditor {
    var base64string: String?
    var loadingDialog: LoadingDialog
    fun getContentResolver() : ContentResolver

    fun <I, O> registerForActivityResult(
        contract: ActivityResultContract<I, O>,
        callback: ActivityResultCallback<O>
    ): ActivityResultLauncher<I>

    fun runOnUiThread(action: Runnable?)
}