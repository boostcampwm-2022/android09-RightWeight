package com.lateinit.rightweight.ui.home.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.lateinit.rightweight.R

class CommonDialogFragment : DialogFragment() {

    internal lateinit var listener: NoticeDialogListener
    var messageId: Int = R.string.logout_message

    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as NoticeDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(
                (context.toString() +
                        " must implement NoticeDialogListener")
            )
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(messageId)
                .setPositiveButton(R.string.submit) { _, _ ->
                    listener.onDialogPositiveClick(this)
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
            // Create the AlertDialog object and return it
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
        const val LOGOUT_DIALOG_TAG = "LOGOUT"
        const val WITHDRAW_DIALOG_TAG = "WITHDRAW"
    }
}