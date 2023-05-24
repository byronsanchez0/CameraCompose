package com.example.cameracompose.textprovider

class LocationPermissionText : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you permanently declined location permission. " +
                    "You can go to the app setting to grant it."
        } else {
            "This app needs access to your location so that you can be able " +
                    "to see where the photo was taken on the map."
        }
    }
}