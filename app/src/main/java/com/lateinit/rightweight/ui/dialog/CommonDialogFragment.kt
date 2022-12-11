package com.lateinit.rightweight.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.lateinit.rightweight.R

class CommonDialogFragment(private val callback: (String?) -> Unit) : DialogFragment() {

    var messageId: Int = R.string.logout_message

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(messageId)
                .setPositiveButton(R.string.submit) { _, _ ->
                    callback(this.tag)
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun show(
        manager: FragmentManager,
        tag: String?,
        @StringRes messageId: Int
    ) {
        this.messageId = messageId
        show(manager, tag)
    }

    companion object {
        const val BACKUP_USER_INFO_TAG = "BACKUP"
        const val LOGOUT_DIALOG_TAG = "LOGOUT"
        const val WITHDRAW_DIALOG_TAG = "WITHDRAW"
        const val RESET_DIALOG_TAG = "RESET"
        const val SELECTED_ROUTINE_REMOVE_DIALOG_TAG = "SELECTED_ROUTINE_REMOVE"
        const val ROUTINE_REMOVE_DIALOG_TAG = "ROUTINE_REMOVE"
        const val END_EXERCISE_DIALOG_TAG = "END_EXERCISE"
    }
}