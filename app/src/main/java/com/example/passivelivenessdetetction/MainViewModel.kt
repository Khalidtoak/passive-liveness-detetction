package com.example.passivelivenessdetetction

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val faceAnalyzer: FaceAnalyzer) : ViewModel() {
    val permissionGranted = mutableStateOf(false)
    val lifeDetected = mutableStateOf(false)
    val camLensFacing = mutableIntStateOf(CameraSelector.LENS_FACING_FRONT)
    fun onPermissionGranted(permissionGranted: Boolean) {
        this.permissionGranted.value = permissionGranted
    }

    fun onFrameRecieved(frame: ImageProxy) {
        faceAnalyzer.analyzeFace(frame) {
            lifeDetected.value = it
        }
    }

}