package com.x_permissions

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionManager(
    private val activity: Activity,
    private val permissions: Array<String>,
    private val requestCode: Int,
    private val rationaleMessage: String = "Permissions are required for this feature."
) {

    interface PermissionCallback {
        fun onPermissionsGranted()
        fun onPermissionsDenied()
    }

    private var permissionCallback: PermissionCallback? = null

    fun setPermissionCallback(callback: PermissionCallback) {
        this.permissionCallback = callback
    }

    fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val notGrantedPermissions = mutableListOf<String>()

            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    notGrantedPermissions.add(permission)
                }
            }

            if (notGrantedPermissions.isEmpty()) {
                permissionCallback?.onPermissionsGranted()
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    notGrantedPermissions.toTypedArray(),
                    requestCode
                )
            }
        } else {
            permissionCallback?.onPermissionsGranted()
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == this.requestCode) {
            val deniedPermissions = mutableListOf<String>()

            for (i in permissions.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permissions[i])
                }
            }

            if (deniedPermissions.isEmpty()) {
                // All permissions are granted
                permissionCallback?.onPermissionsGranted()
            } else {
                // Permissions are denied
                handleDeniedPermissions(deniedPermissions)
            }
        }
    }

    private fun handleDeniedPermissions(deniedPermissions: List<String>) {
        val showRationale = deniedPermissions.any {
            ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
        }

        if (showRationale) {
            permissionCallback?.onPermissionsDenied()
        } else {
            checkAndRequestPermissions()
        }
    }
}