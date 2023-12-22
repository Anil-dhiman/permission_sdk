package com.x_permissions

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.textview.MaterialTextView
import com.x_permissions.callbacks.ExplainReasonCallback
import com.x_permissions.callbacks.ReasonDialogCallback


class InvisibleFragment : Fragment() {

    private var explainReasonCallback: ExplainReasonCallback? = null
    private var permissionController: PermissionController? = null
    private var permissionsList: Array<String>? = null
    private var grantPermissionsList: ArrayList<String> = ArrayList<String>()
    private var deniedList: ArrayList<String> = ArrayList<String>()
    private var permanentDeniedList: ArrayList<String> = ArrayList<String>()
    private var showReasonList: ArrayList<String> = ArrayList<String>()
    private var currentIPermissionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestPermissions()
    }

    fun setPermissions(permissionsList: Array<String>) {
        this.permissionsList = permissionsList
    }

    fun setController(permissionsList: PermissionController) {
        this.permissionController = permissionsList
    }

    fun setExplainCallback(explainReasonCallback: ExplainReasonCallback?) {
        this.explainReasonCallback = explainReasonCallback
    }

    fun checkAndRequestPermissions() {
        requestPermissionLauncher.launch(getArrayList().toTypedArray())
    }

    private fun getArrayList(): ArrayList<String> {
        val list = ArrayList<String>()
        permissionsList!!.forEachIndexed { index, model ->
            list.add(model)
        }
        return list
    }

    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantResults ->
            Log.e(">>check", grantResults.toString())
            clearList()
            for ((permission, granted) in grantResults) {
                if (granted) {
                    grantPermissionsList.add(permission)
                } else {
                    val shouldShowRationale = shouldShowRequestPermissionRationale(permission)
                    if (shouldShowRationale) {
                        showReasonList.add(permission)
                        deniedList.add(permission)
                        // So there's no need to remove the current permission from permanentDeniedPermissions because it won't be there.
                    } else {
                        permanentDeniedList.add(permission)
                    }
                }
            }

            val deniedAndPemanentDenied = ArrayList<String>()
            deniedAndPemanentDenied.addAll(deniedList)
            deniedAndPemanentDenied.addAll(permanentDeniedList)

            for (permission in deniedAndPemanentDenied) {
                if (PermissionController.isGranted(requireContext(), permission)) {
                    grantPermissionsList.add(permission)
                    deniedList.remove(permission)
                    permanentDeniedList.remove(permission)
                    showReasonList.remove(permission)
                }
            }

            if (showReasonList.isNotEmpty()) {
                explainReasonCallback?.onExplainReason(deniedList, showDialogListiner)
            }else if (permanentDeniedList.isNotEmpty()){
                moveToSetting()
            }

        }

    private fun clearList() {
        grantPermissionsList.clear()
        showReasonList.clear()
        deniedList.clear()
        permanentDeniedList.clear()
    }

    var showDialogListiner = object : ReasonDialogCallback {
        override fun showReasonDialog() {
            showExplainDialog()
        }
    }

    fun showExplainDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_explain)

        val cancel = dialog.findViewById<MaterialTextView>(R.id.tvCancel) as TextView
        val ok = dialog.findViewById<MaterialTextView>(R.id.tvOk) as TextView

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        ok.setOnClickListener {
            checkAndRequestPermissions()
            dialog.dismiss()
        }

        dialog.show()
    }

    fun moveToSetting() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_explain)

        val cancel = dialog.findViewById<MaterialTextView>(R.id.tvCancel) as TextView
        val ok = dialog.findViewById<MaterialTextView>(R.id.tvOk) as TextView
        val subtitle = dialog.findViewById<MaterialTextView>(R.id.tvSubtitle) as TextView
        subtitle.setText("You have permanently denied the permissions to use the functionality you have to allow the permissions from the setting")
        ok.setText("Go to Settings")

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        ok.setOnClickListener {
            forwardToSettings()
            dialog.dismiss()
        }

        dialog.show()
    }

    fun forwardToSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        forwardToSettingsLauncher.launch(intent)
    }

    private val forwardToSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            checkAndRequestPermissions()
        }

}