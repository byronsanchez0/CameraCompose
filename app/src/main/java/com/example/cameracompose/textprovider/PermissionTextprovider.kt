package com.example.cameracompose.textprovider

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}