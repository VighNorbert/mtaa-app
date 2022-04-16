package sk.evysetrenie.api

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.scale
import sk.evysetrenie.ProfileEditor
import java.io.ByteArrayOutputStream
import java.lang.Exception
import kotlin.math.min

class ImageManager(
    private val activity: ProfileEditor,
    private val avatarImageView: ImageView,
    private val noAvatarTextView: TextView
) {

    // https://www.android--code.com/2020/06/android-kotlin-bitmap-crop-square.html
    private fun Bitmap.toSquare(): Bitmap {
        // get the small side of bitmap
        val side = min(width,height)

        // calculate the x and y offset
        val xOffset = (width - side) /2
        val yOffset = (height - side)/2

        // create a square bitmap
        // a square is closed, two dimensional shape with 4 equal sides
        return Bitmap.createBitmap(
            this, // source bitmap
            xOffset, // x coordinate of the first pixel in source
            yOffset, // y coordinate of the first pixel in source
            side, // width
            side // height
        ).scale(400, 400)
    }

    private fun Bitmap.toBase64String(): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
        val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    // https://guides.codepath.com/android/Accessing-the-Camera-and-Stored-Media#accessing-stored-media
    val resultLauncher = activity.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        activity.loadingDialog.open()
        Thread {
            println("OK")
            if (uri !== null) {
                println("URI OK")
                try {
                    var bitmap = if (Build.VERSION.SDK_INT > 27) {
                        // on newer versions of Android, use the new decodeBitmap method
                        val source: ImageDecoder.Source =
                            ImageDecoder.createSource(activity.getContentResolver(), uri)
                        ImageDecoder.decodeBitmap(source)
                    } else {
                        // support older versions of Android by using getBitmap
                        MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri)
                    }

                    bitmap = bitmap.toSquare()
                    activity.runOnUiThread {
                        avatarImageView.setImageBitmap(bitmap)
                        avatarImageView.visibility = View.VISIBLE
                        noAvatarTextView.visibility = View.GONE
                    }

                    activity.base64string = bitmap.toBase64String()

                } catch (err: Exception) {
                    err.printStackTrace()
                }
            }
            activity.runOnUiThread { activity.loadingDialog.dismiss() }
        }.start()
    }

}