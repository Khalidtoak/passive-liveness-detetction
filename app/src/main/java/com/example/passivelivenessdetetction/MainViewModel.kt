package com.example.passivelivenessdetetction

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val permissionGranted = mutableStateOf(false)

    fun onPermissionGranted(permissionGranted: Boolean) {
        this.permissionGranted.value = permissionGranted
    }

}