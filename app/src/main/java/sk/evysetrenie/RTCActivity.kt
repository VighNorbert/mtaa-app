package sk.evysetrenie

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.webrtc.*
import sk.evysetrenie.webrtc.*

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class RTCActivity : BaseActivity() {

    companion object {
        private const val CAMERA_AUDIO_PERMISSION_REQUEST_CODE = 1
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        private const val AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO
    }

    private lateinit var rtcClient: RTCClient
    private lateinit var signallingClient: SignalingClient

    private lateinit var switchCameraButton: ImageView
    private lateinit var audioOutputButton: ImageView
    private lateinit var videoButton: ImageView
    private lateinit var micButton: ImageView
    private lateinit var endCallButton: ImageView
    private lateinit var remoteViewLoading: ProgressBar
    private lateinit var remoteView: SurfaceViewRenderer
    private lateinit var localView: SurfaceViewRenderer

    private val audioManager by lazy { RTCAudioManager.create(this) }

    val tag = "WebRTC"

    private var meetingID : String = "test-call"

    private var isJoin = false

    private var audioEnabled = true

    private var videoEnabled = true

    private var inSpeakerMode = true

    private val sdpObserver = object : AppSdpObserver() {
        override fun onCreateSuccess(p0: SessionDescription?) {
            super.onCreateSuccess(p0)
//            signallingClient.send(p0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webrtc)

        if (intent.hasExtra("meetingID"))
            meetingID = intent.getStringExtra("meetingID")!!
        if (intent.hasExtra("isJoin"))
            isJoin = intent.getBooleanExtra("isJoin",false)

        checkCameraAndAudioPermission()
        audioManager.selectAudioDevice(RTCAudioManager.AudioDevice.SPEAKER_PHONE)

        switchCameraButton = findViewById(R.id.switch_camera_button)
        audioOutputButton = findViewById(R.id.audio_output_button)
        videoButton = findViewById(R.id.video_button)
        micButton = findViewById(R.id.mic_button)
        endCallButton = findViewById(R.id.end_call_button)
        remoteViewLoading = findViewById(R.id.remote_view_loading)
        remoteView = findViewById(R.id.remote_view)
        localView = findViewById(R.id.local_view)

        switchCameraButton.setOnClickListener {
            rtcClient.switchCamera()
        }

        audioOutputButton.setOnClickListener {
            if (inSpeakerMode) {
                inSpeakerMode = false
                audioOutputButton.setImageResource(R.drawable.ic_baseline_hearing_24)
                audioManager.setDefaultAudioDevice(RTCAudioManager.AudioDevice.EARPIECE)
            } else {
                inSpeakerMode = true
                audioOutputButton.setImageResource(R.drawable.ic_baseline_speaker_up_24)
                audioManager.setDefaultAudioDevice(RTCAudioManager.AudioDevice.SPEAKER_PHONE)
            }
        }
        videoButton.setOnClickListener {
            if (videoEnabled) {
                videoEnabled = false
                println("-> VP: $videoEnabled")
                videoButton.setImageResource(R.drawable.ic_baseline_videocam_off_24)
                videoButton.backgroundTintList = ColorStateList.valueOf(this.getColor(R.color.white))
                videoButton.imageTintList = ColorStateList.valueOf(this.getColor(androidx.cardview.R.color.cardview_dark_background))
            } else {
                videoEnabled = true
                println("-> VP: $videoEnabled")
                videoButton.setImageResource(R.drawable.ic_baseline_videocam_24)
                videoButton.backgroundTintList = ColorStateList.valueOf(this.getColor(androidx.cardview.R.color.cardview_dark_background))
                videoButton.imageTintList = ColorStateList.valueOf(this.getColor(R.color.white))
            }
            rtcClient.enableVideo(videoEnabled)
        }
        micButton.setOnClickListener {
            if (audioEnabled) {
                audioEnabled = false
                println("-> MUTE: $audioEnabled")
                micButton.setImageResource(R.drawable.ic_baseline_mic_off_24)
                micButton.backgroundTintList = ColorStateList.valueOf(this.getColor(R.color.white))
                micButton.imageTintList = ColorStateList.valueOf(this.getColor(androidx.cardview.R.color.cardview_dark_background))
            } else {
                audioEnabled = true
                println("-> MUTE: $audioEnabled")
                micButton.setImageResource(R.drawable.ic_baseline_mic_24)
                micButton.backgroundTintList = ColorStateList.valueOf(this.getColor(androidx.cardview.R.color.cardview_dark_background))
                micButton.imageTintList = ColorStateList.valueOf(this.getColor(R.color.white))
            }
            rtcClient.enableAudio(audioEnabled)
        }
        endCallButton.setOnClickListener {
            rtcClient.endCall(meetingID)
            remoteView.isGone = false
            Constants.isCallEnded = true
            finish()
            startActivity(Intent(this@RTCActivity, MainActivity::class.java))
        }
    }

    private fun checkCameraAndAudioPermission() {
        if ((ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION)
                    != PackageManager.PERMISSION_GRANTED) &&
            (ContextCompat.checkSelfPermission(this, AUDIO_PERMISSION)
                    != PackageManager.PERMISSION_GRANTED)) {
            requestCameraAndAudioPermission()
        } else {
            onCameraAndAudioPermissionGranted()
        }
    }

    private fun onCameraAndAudioPermissionGranted() {
        rtcClient = RTCClient(
                application,
                object : PeerConnectionObserver() {
                    override fun onIceCandidate(p0: IceCandidate?) {
                        super.onIceCandidate(p0)
                        signallingClient.sendIceCandidate(p0, isJoin)
                        rtcClient.addIceCandidate(p0)
                    }

                    override fun onAddStream(p0: MediaStream?) {
                        super.onAddStream(p0)
                        Log.e(tag, "onAddStream: $p0")
                        p0?.videoTracks?.get(0)?.addSink(remoteView)
                    }

                    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
                        Log.e(tag, "onIceConnectionChange: $p0")
                    }

                    override fun onIceConnectionReceivingChange(p0: Boolean) {
                        Log.e(tag, "onIceConnectionReceivingChange: $p0")
                    }

                    override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                        Log.e(tag, "onConnectionChange: $newState")
                    }

                    override fun onDataChannel(p0: DataChannel?) {
                        Log.e(tag, "onDataChannel: $p0")
                    }

                    override fun onStandardizedIceConnectionChange(newState: PeerConnection.IceConnectionState?) {
                        Log.e(tag, "onStandardizedIceConnectionChange: $newState")
                    }

                    override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {
                        Log.e(tag, "onAddTrack: $p0 \n $p1")
                    }

                    override fun onTrack(transceiver: RtpTransceiver?) {
                        Log.e(tag, "onTrack: $transceiver" )
                    }
                }
        )

        remoteView = findViewById(R.id.remote_view)
        localView = findViewById(R.id.local_view)
        rtcClient.initSurfaceView(remoteView)
        rtcClient.initSurfaceView(localView)
        rtcClient.startLocalVideoCapture(localView)
        signallingClient = SignalingClient(meetingID,createSignallingClientListener())
        if (!isJoin)
            rtcClient.call(sdpObserver,meetingID)
    }

    private fun createSignallingClientListener() = object : SignalingClientListener {
        override fun onConnectionEstablished() {
            endCallButton.isClickable = true
            Constants.isCallEnded = false
        }

        override fun onOfferReceived(description: SessionDescription) {
            rtcClient.onRemoteSessionReceived(description)
            Constants.isIntiatedNow = false
            rtcClient.answer(sdpObserver,meetingID)
            remoteViewLoading.isGone = true
        }

        override fun onAnswerReceived(description: SessionDescription) {
            rtcClient.onRemoteSessionReceived(description)
            Constants.isIntiatedNow = false
            remoteViewLoading.isGone = true
        }

        override fun onIceCandidateReceived(iceCandidate: IceCandidate) {
            rtcClient.addIceCandidate(iceCandidate)
        }

        override fun onCallEnded() {
            if (!Constants.isCallEnded) {
                Constants.isCallEnded = true
                rtcClient.endCall(meetingID)
                finish()
                startActivity(Intent(this@RTCActivity, MainActivity::class.java))
            }
        }
    }

    private fun requestCameraAndAudioPermission(dialogShown: Boolean = false) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA_PERMISSION) &&
            ActivityCompat.shouldShowRequestPermissionRationale(this, AUDIO_PERMISSION) &&
            !dialogShown) {
            showPermissionRationaleDialog()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(CAMERA_PERMISSION, AUDIO_PERMISSION), CAMERA_AUDIO_PERMISSION_REQUEST_CODE)
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
                .setTitle("Camera And Audio Permission Required")
                .setMessage("This app need the camera and audio to function")
                .setPositiveButton("Grant") { dialog, _ ->
                    dialog.dismiss()
                    requestCameraAndAudioPermission(true)
                }
                .setNegativeButton("Deny") { dialog, _ ->
                    dialog.dismiss()
                    onCameraPermissionDenied()
                }
                .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_AUDIO_PERMISSION_REQUEST_CODE && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            onCameraAndAudioPermissionGranted()
        } else {
            onCameraPermissionDenied()
        }
    }

    private fun onCameraPermissionDenied() {
        Toast.makeText(this, "Camera and Audio Permission Denied", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        signallingClient.destroy()
        super.onDestroy()
    }
}