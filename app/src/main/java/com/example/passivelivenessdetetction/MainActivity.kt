@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.passivelivenessdetetction

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.camera.core.Preview as camPreview
import com.example.passivelivenessdetetction.ui.theme.PassiveLivenessDetetctionTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private fun openAppSettings() {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        ).also(::startActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PassiveLivenessDetetctionTheme {
                // A surface container using the 'background' color from the theme
                val mainViewModel = viewModel<MainViewModel>()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val cameraPermissionResultLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = { isGranted ->
                            mainViewModel.onPermissionGranted(isGranted)
                        }
                    )

                    if (!mainViewModel.permissionGranted.value) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(onClick = {
                                val isNotPermanentyDeclined =
                                    shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                                if (isNotPermanentyDeclined) {
                                    openAppSettings()
                                } else {
                                    cameraPermissionResultLauncher.launch(
                                        Manifest.permission.CAMERA
                                    )
                                }
                            }) {
                                Text(text = "Open Camera")
                            }
                        }
                    } else {
                        CameraPreview(
                            cameraSelector = mainViewModel.camLensFacing.value,
                            lifeDetectionString = mainViewModel.lifeDetected.value,
                            onSwitchCameraClicked = {
                                mainViewModel.switchCamera()
                            },
                            onFrameRecieved = {
                                mainViewModel.onFrameRecieved(it)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CameraPreview(
    onFrameRecieved: (ImageProxy) -> Unit, cameraSelector: CameraSelector, lifeDetectionString: String,
    onSwitchCameraClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
    ) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val cameraProviderFuture = remember(context) { ProcessCameraProvider.getInstance(context) }
        val cameraProvider = remember(cameraProviderFuture) { cameraProviderFuture.get() }
        val executor = remember(context) { ContextCompat.getMainExecutor(context) }
        var camera: Camera? by remember { mutableStateOf(null) }
        var preview by remember { mutableStateOf<androidx.camera.core.Preview?>(null) }

        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)

                cameraProviderFuture.addListener(
                    {
                        val imageAnalysis: ImageAnalysis =
                            ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                                .also {
                                    it.setAnalyzer(executor) { imageProxy ->
                                        onFrameRecieved(imageProxy)
                                    }
                                }
                        try {
                            cameraProvider.unbindAll()
                            camera = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )
                        } catch (e: Exception) {
                        }
                    }, executor
                )
                camPreview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }.also { preview = it }
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = lifeDetectionString,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            color = Color.White,
            fontSize = 30.sp
        )
        IconButton(
            onClick = onSwitchCameraClicked,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_switch_camera_white),
                contentDescription = ""
            )
        }
    }
}