package com.x_permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.x_permissions.callbacks.ExplainReasonCallback

class PermissionController() {

    private lateinit var permissionsList: Array<String>
    private var activity: FragmentActivity? = null
    private var fragment: Fragment? = null
    private val invisibleFragment = InvisibleFragment()
    var explainReasonCallback: ExplainReasonCallback? = null

    constructor(activity: FragmentActivity) : this() {
        this.activity = activity
    }

    constructor(fragment: Fragment) : this() {
        this.fragment = fragment
    }

    private val fragmentManager: FragmentManager
        get() {
            return (fragment?.childFragmentManager ?: activity?.supportFragmentManager)!!
        }

    companion object{
        fun isGranted(context: Context, permission: String): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun permissions(permissionsList:Array<String>): PermissionController {
        this.permissionsList = permissionsList
       return this@PermissionController
    }

    fun start(){
        invisibleFragment.setPermissions(permissionsList)
        invisibleFragment.setExplainCallback(explainReasonCallback)
        invisibleFragment.setController(this@PermissionController)
        if(activity != null){
            fragmentManager.beginTransaction()
                .add(invisibleFragment, "InvisibleFragment")
                .commitNowAllowingStateLoss()
        }else if(fragment != null) {
            fragmentManager.beginTransaction()
                .add(invisibleFragment, "InvisibleFragment")
                .commitNowAllowingStateLoss()
        }
    }

    fun onExplainRequestReason(callback: ExplainReasonCallback?): PermissionController {
        explainReasonCallback = callback
        return this@PermissionController
    }


}



















/* fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val notGrantedPermissions = mutableListOf<String>()

            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    notGrantedPermissions.add(permission)
                }
            }

            if (notGrantedPermissions.isEmpty()) {
                // all granted
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        } else {
            // all granted
        }
    }*/