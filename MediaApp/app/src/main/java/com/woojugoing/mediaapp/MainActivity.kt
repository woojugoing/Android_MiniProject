package com.woojugoing.mediaapp

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.media.MediaRecorder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.woojugoing.mediaapp.databinding.ActivityMainBinding
import java.io.IOException

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_RECORD_AUDIO_CODE = 200
    }

    private enum class State { RELEASE, RECORDING, PLAY }

    private lateinit var activityMainBinding: ActivityMainBinding
    private var recorder: MediaRecorder ?= null
    private var fileName: String = ""
    private var state: State = State.RELEASE


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        fileName = "${externalCacheDir?.absolutePath}/audio_record_text.3qp"

        activityMainBinding.recordButton.setOnClickListener {
            when(state) {
                State.RELEASE ->  record()
                State.RECORDING -> onRecord(true)
                State.PLAY -> {}
            }
        }
    }

    private fun record() {
        when {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED -> { onRecord(true) }
            ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECORD_AUDIO) -> { showPermissionRationalDialog() }
            else -> ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_CODE)
        }
    }

    private fun onRecord(start: Boolean) = if(start) startRecord() else stopRecord()

    private fun startRecord() {
        state = State.RECORDING
        recorder = MediaRecorder().apply {          // 순서가 바뀌면 오류남
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e("APP", "prepare() failed $e")
            }

            start()
        }

        activityMainBinding.run {
            recordButton.run {
                setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_baseline_stop_24))
                imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, R.color.black))
            }

            playButton.run {
                isEnabled = false
                alpha = 0.3f
            }
        }
    }

    private fun stopRecord() {
        state = State.RELEASE
        recorder?.apply {
            stop()
            release()
        }

        recorder = null

        activityMainBinding.run {
            recordButton.run {
                setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_baseline_record_24))
                imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, R.color.red))
            }

            playButton.run {
                isEnabled = true
                alpha = 1.0f
            }
        }
    }


    private fun showPermissionRationalDialog() {
        AlertDialog.Builder(this)
            .setMessage("녹음 권한을 켜야 합니다.")
            .setPositiveButton("권한 허용") { _, _ -> ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_CODE) }
            .setNegativeButton("취소") { dialogInterface, _ ->  dialogInterface.cancel() }
            .show()
    }

    private fun showPermissionSettingDialog() {
        AlertDialog.Builder(this)
            .setMessage("녹음 권한을 켜야 합니다. 설정 화면에서 직접 설정 하세요.")
            .setPositiveButton("권한 변경하기") { _, _ -> navigateToAppSetting() }
            .setNegativeButton("취소") { dialogInterface, _ ->  dialogInterface.cancel() }
            .show()
    }

    private fun navigateToAppSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val audioRecordPermissionGranted = requestCode == REQUEST_RECORD_AUDIO_CODE && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
        if(audioRecordPermissionGranted)  {
            onRecord(true)
        } else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, android.Manifest.permission.RECORD_AUDIO)) {
                showPermissionRationalDialog()
            } else {
                showPermissionSettingDialog()
            }
        }
    }
}