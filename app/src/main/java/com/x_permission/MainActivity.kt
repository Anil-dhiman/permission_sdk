package com.x_permission

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.databinding.DataBindingUtil
import com.x_permission.databinding.ActivityMainBinding
import com.x_permissions.PermissionManager

class MainActivity : ComponentActivity() {

    lateinit var binding: ActivityMainBinding

    val permissionManager : PermissionManager=PermissionManager(
        this@MainActivity,
        arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_CONTACTS
        ),
        123
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)


        permissionManager.setPermissionCallback(object : PermissionManager.PermissionCallback {
            override fun onPermissionsGranted() {
                Log.e(">>>che","Permissions granted")
            }

            override fun onPermissionsDenied() {
                Log.e(">>>che","Permissions denied")
            }
        })

        binding.btnPermission.setOnClickListener {
            permissionManager.checkAndRequestPermissions()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionManager?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


}

