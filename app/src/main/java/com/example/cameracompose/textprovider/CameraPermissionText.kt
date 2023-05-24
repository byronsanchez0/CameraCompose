package com.example.cameracompose.textprovider

class CameraPermissionText : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you permanently declined camera permission. " +
                    "You can go to the app setting to grant it." + "\n**If you already allow to use the camera, please click outside of this dialogue**"
        } else {
            "This app needs access to your camera so that you can be allow " +
                    "to take photos."
        }
    }
}