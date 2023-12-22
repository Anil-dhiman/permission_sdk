package com.x_permissions.callbacks

interface ExplainReasonCallback {
    fun onExplainReason(deniedList: List<String?>, showDialogListiner: ReasonDialogCallback)
}