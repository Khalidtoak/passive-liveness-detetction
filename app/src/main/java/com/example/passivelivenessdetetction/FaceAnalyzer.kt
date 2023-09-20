package com.example.passivelivenessdetetction

import android.annotation.SuppressLint
import android.graphics.PointF
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceLandmark
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class FaceAnalyzer @Inject constructor(private val faceDetector: FaceDetector) {

    private var liveCount = 0
    private var notLiveCount = 0

    private var previousFace: Face? = null

    @SuppressLint("UnsafeOptInUsageError")
    fun analyzeFace(imageProxy: ImageProxy, callback: (Boolean) -> Unit) {
        imageProxy.image?.let { image ->
            InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
        }.also { inputImage ->
            inputImage?.let {
                faceDetector.process(inputImage)
                    .addOnSuccessListener { faces ->
                        if (faces.isNotEmpty()) {
                            val currentFace = faces[0]

                            if (previousFace != null) {
                                val hasMoved = detectMovement(previousFace!!, currentFace)
                                Log.d("Face Analyzer", "movement detected $hasMoved")
                                callback(hasMoved)
                            }

                            previousFace = currentFace
                        } else {
                            callback(false)
                        }
                    }
                    .addOnFailureListener {
                        // Handle error, for example:
                        Log.d("Face Analyzer", "failed $it")
                        callback(false)
                    }.addOnCompleteListener {
                        imageProxy.close()
                    }
            }
        }
    }

    private fun detectMovement(previousFace: Face, currentFace: Face): Boolean {
        // if its the same face and it has been detected as live more than 10 times, then its live and vice versa
        if (previousFace.trackingId != currentFace.trackingId) {
            // reset if face changes
            liveCount = 0
            notLiveCount = 0
        }
        // if its the same face and it has been detected as live more than 10 times, then its live and vice versa
        if (previousFace.trackingId == currentFace.trackingId && liveCount > 10) {
            return true
        }
        if (previousFace.trackingId == currentFace.trackingId && notLiveCount > 10) {
            return false
        }
        val previousNoseBase = previousFace.getLandmark(FaceLandmark.NOSE_BASE)?.position
        val currentNoseBase = currentFace.getLandmark(FaceLandmark.NOSE_BASE)?.position

        val previousLeftEarBase = previousFace.getLandmark(FaceLandmark.LEFT_EAR)?.position
        val currentLeftEarBase = currentFace.getLandmark(FaceLandmark.LEFT_EAR)?.position

        val previousRightEarBase = previousFace.getLandmark(FaceLandmark.RIGHT_EAR)?.position
        val currentRightEarBase = currentFace.getLandmark(FaceLandmark.RIGHT_EAR)?.position

        val previousLeftCheekBase = previousFace.getLandmark(FaceLandmark.LEFT_CHEEK)?.position
        val currentLeftCheekBase = currentFace.getLandmark(FaceLandmark.LEFT_CHEEK)?.position

        val previousRightCheekBase = previousFace.getLandmark(FaceLandmark.RIGHT_CHEEK)?.position
        val currentRightCheekBase = currentFace.getLandmark(FaceLandmark.RIGHT_CHEEK)?.position


        val previousLeftEyeBase = previousFace.getLandmark(FaceLandmark.LEFT_EYE)?.position
        val currentLeftEyeBase = currentFace.getLandmark(FaceLandmark.LEFT_EYE)?.position

        val previousRightEyeBase = previousFace.getLandmark(FaceLandmark.RIGHT_EYE)?.position
        val currentRightEyeBase = currentFace.getLandmark(FaceLandmark.RIGHT_EYE)?.position

        val previousRightMouthBase = previousFace.getLandmark(FaceLandmark.MOUTH_RIGHT)?.position
        val currentRightMouthBase = currentFace.getLandmark(FaceLandmark.MOUTH_RIGHT)?.position

        val previousLeftMouthBase = previousFace.getLandmark(FaceLandmark.MOUTH_LEFT)?.position
        val currentLeftMouthBase = currentFace.getLandmark(FaceLandmark.MOUTH_LEFT)?.position

        val previousBottomMouthBase = previousFace.getLandmark(FaceLandmark.MOUTH_BOTTOM)?.position
        val currentBottomMouthBase = currentFace.getLandmark(FaceLandmark.MOUTH_BOTTOM)?.position

        if (previousNoseBase != null && currentNoseBase != null && previousLeftEarBase != null && currentLeftEarBase != null && previousRightEarBase != null &&
            currentRightEarBase != null && previousLeftCheekBase != null && currentLeftCheekBase != null && previousLeftEyeBase != null && currentLeftEyeBase != null
            && previousRightEyeBase != null && currentRightEyeBase != null && previousRightMouthBase != null && previousRightCheekBase != null && currentRightCheekBase != null
            && currentRightMouthBase != null && previousLeftMouthBase != null && currentLeftMouthBase != null && previousBottomMouthBase != null && currentBottomMouthBase != null
        ) {
            // small threshold to make sure they are natural movements and not just someone moving the image around,
            // will require UI to tell user to remain as still as possible though
            val threshold = 2f..3f
            // landmarks
            val deltaNoseX = abs(previousNoseBase.x - currentNoseBase.x)
            val deltaNoseY = abs(previousNoseBase.y - currentNoseBase.y)

            val deltaLeftEarX = abs(previousLeftEarBase.x - currentLeftEarBase.x)
            val deltaLeftEarY = abs(previousLeftEarBase.y - currentLeftEarBase.y)

            val deltaRightEarX = abs(previousRightEarBase.x - currentRightEarBase.x)
            val deltaRightEarY = abs(previousRightEarBase.y - currentRightEarBase.y)

            val deltaLeftCheekX = abs(previousLeftCheekBase.x - currentLeftCheekBase.x)
            val deltaLeftCheekY = abs(previousLeftCheekBase.y - currentLeftCheekBase.y)

            val deltaRightCheekBaseX = abs(previousRightCheekBase.x - currentRightCheekBase.x)
            val deltaRightCheekBaseY = abs(previousRightCheekBase.y - currentRightCheekBase.y)

            val deltaLeftEyeBaseX = abs(previousLeftEyeBase.x - currentLeftEyeBase.x)
            val deltaLeftEyeBaseY = abs(previousLeftEyeBase.y - currentLeftEyeBase.y)

            val deltaRightEyeBaseX = abs(previousRightEyeBase.x - currentRightEyeBase.x)
            val deltaRightEyeBaseY = abs(previousRightEyeBase.y - currentRightEyeBase.y)

            val deltaRightMouthBaseX = abs(previousRightMouthBase.x - currentRightMouthBase.x)
            val deltaRightMouthBaseY = abs(previousRightMouthBase.y - currentRightMouthBase.y)

            val deltaLeftMouthBaseX = abs(previousLeftMouthBase.x - currentLeftMouthBase.x)
            val deltaLeftMouthBaseY = abs(previousLeftMouthBase.y - currentLeftMouthBase.y)

            val deltaBottomMouthBaseX = abs(previousBottomMouthBase.x - currentBottomMouthBase.x)
            val deltaBottomMouthBaseY = abs(previousNoseBase.y - currentNoseBase.y)

            if (threshold.contains(deltaNoseX) || threshold.contains(deltaNoseY)
                || threshold.contains(deltaLeftEarX) || threshold.contains(deltaLeftEarY)
                || threshold.contains(deltaRightEarX) || threshold.contains(deltaRightEarY)
                || threshold.contains(deltaLeftCheekX) || threshold.contains(deltaLeftCheekY)
                || threshold.contains(deltaLeftEyeBaseX) || threshold.contains(deltaRightEyeBaseY)
                || threshold.contains(deltaRightEyeBaseY) || threshold.contains(deltaRightEyeBaseX)
                || threshold.contains(deltaRightCheekBaseX) || threshold.contains(deltaRightCheekBaseY)
                || threshold.contains(deltaRightMouthBaseY) || threshold.contains(deltaRightMouthBaseX)
                || threshold.contains(deltaLeftEyeBaseY) || threshold.contains(deltaLeftMouthBaseX)
                || threshold.contains(deltaLeftMouthBaseY) || threshold.contains(deltaBottomMouthBaseX)
                || threshold.contains(deltaBottomMouthBaseY)) {
                liveCount++
                return true // Movement detected
            }
        }
        notLiveCount++
        return false
    }
}