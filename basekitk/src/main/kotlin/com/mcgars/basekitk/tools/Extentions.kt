package com.mcgars.basekitk.tools

import android.app.Activity
import android.content.Context
import android.support.annotation.IdRes
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast

/**
 * Created by gars on 02.01.2017.
 */

fun Context.toast(@IdRes msg: Int, lenght: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, lenght).show()
}

fun Context.toast(msg: String, lenght: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, lenght).show()
}

inline fun trying(func: () -> Unit): Boolean {
    try {
        func()
        return true
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

inline fun <C> trying2(func: () -> Unit): C? {
    try {
        return func() as C
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

inline fun View?.visible(visible: Boolean) {
    this?.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

inline fun View?.gone(isGone: Boolean) {
    this?.visibility = if (isGone) View.GONE else View.VISIBLE
}

inline fun Array<View?>.visible(visible: Boolean) {
    setVisibleState(if (visible) View.VISIBLE else View.INVISIBLE)
}

inline fun Array<View?>.gone(isGone: Boolean) {
    setVisibleState(if (isGone) View.GONE else View.VISIBLE)
}

fun Array<View?>.setVisibleState(state: Int) {
    for (i in indices) {
        get(i)?.visibility = state
    }
}

inline fun Context?.hideKeyboard(hostView: View?): Boolean? {
    var isHide: Boolean = false
    hostView?.windowToken?.run {
        isHide = (this@hideKeyboard?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(hostView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
    return isHide
}

inline fun Context?.showKeyboard(etText: EditText?): Boolean? {
    etText?.requestFocus()
    val imm = this?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    return imm?.showSoftInput(etText, InputMethodManager.SHOW_IMPLICIT)
}

inline fun toggleKeyboard(context: Context) {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

/**
 * auto cast types and find view
 * @param id id view for findViewById
 * @return Необходимую вью
</C> */
inline fun <C : View> View.find(id: Int) = findViewById(id) as C

inline fun <C : View> Activity.find(id: Int) = findViewById(id) as C