package com.x_permission

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.x_permission.databinding.ActivityMainBinding
import com.x_permissions.callbacks.ExplainReasonCallback
import com.x_permissions.PermissionController
import com.x_permissions.callbacks.ReasonDialogCallback

class SecondActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this@SecondActivity, R.layout.activity_main)

        binding.btnPermission.setOnClickListener {

            PermissionController(
                this@SecondActivity)
                .onExplainRequestReason(object : ExplainReasonCallback {
                    override fun onExplainReason(
                        deniedList: List<String?>,
                        listiner: ReasonDialogCallback) {
                        listiner.showReasonDialog()
                    }
                }).permissions(arrayOf( Manifest.permission.CAMERA,Manifest.permission.READ_CONTACTS,Manifest.permission.CALL_PHONE))
                .start()
        }
    }



}