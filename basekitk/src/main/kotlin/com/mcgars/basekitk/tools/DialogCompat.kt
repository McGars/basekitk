package com.mcgars.basekitk.tools

import android.app.Activity
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import com.mcgars.basekitk.R


/**
 * Created by Владимир on 09.06.2015.
 */
class DialogCompat(activity: Activity) {
    val dialog: AlertDialog.Builder = AlertDialog.Builder(activity)
    private val okButton = R.string.ok
    private val noneButton = android.R.string.cancel

    fun setTitle(title: Int): DialogCompat {
        dialog.setTitle(title)
        return this
    }

    fun setTitle(title: String): DialogCompat {
        dialog.setTitle(title)
        return this
    }

    fun show(title: String, text: String): AlertDialog {
        dialog.setTitle(title)
                .setMessage(text)
        return dialog.show()
    }

    fun show(title: Int, text: Int): AlertDialog {
        return dialog.setTitle(title)
              .setMessage(text)
              .show()
    }

    fun show(text: String): AlertDialog {
        return dialog.setMessage(text).show()
    }

    fun show(text: Int): AlertDialog {
        return dialog.setMessage(text).show()
    }

    fun setPositiveListener(): DialogCompat {
        setPositiveListener(okButton, null)
        return this
    }

    fun setPositiveListener(clickListener: DialogInterface.OnClickListener?): DialogCompat {
        setPositiveListener(okButton, clickListener)
        return this
    }

    fun setPositiveListener(button: Int, clickListener: DialogInterface.OnClickListener?): DialogCompat {
        dialog.setPositiveButton(button, clickListener)
        return this
    }

    fun setNegativeListener(): DialogCompat {
        return setNegativeListener(noneButton, null)
    }

    fun setNegativeListener(clickListener: DialogInterface.OnClickListener?): DialogCompat {
        return setNegativeListener(noneButton, clickListener)
    }

    fun setNegativeListener(button: Int, clickListener: DialogInterface.OnClickListener?): DialogCompat {
        dialog.setNegativeButton(button, clickListener)
        return this
    }
}
