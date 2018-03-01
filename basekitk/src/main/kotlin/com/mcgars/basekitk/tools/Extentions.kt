package com.mcgars.basekitk.tools

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.os.Build
import android.support.annotation.*
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.mcgars.basekitk.R
import java.io.File
import java.security.MessageDigest
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * Created by gars on 02.01.2017.
 */

fun Context.toast(@StringRes msg: Int, lenght: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, lenght).show()
}

fun Context.toast(msg: String?, lenght: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, lenght).show()
}

inline fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
    Snackbar.make(this, message, length).apply {
        f(this)
    }.show()
}

inline fun View.snack(message: Int, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
    Snackbar.make(this, message, length).apply {
        f(this)
    }.show()
}

fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG)
        = Snackbar.make(this, message, length).show()

fun View.snack(message: Int, length: Int = Snackbar.LENGTH_LONG)
        = Snackbar.make(this, message, length).show()

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun EditText?.txt(): String {
    return this?.text?.toString()?.trim() ?: ""
}

fun EditText?.toInt(defVal: Int = 0): Int {
    return if (txt().isEmpty()) defVal else txt().toInt()
}

fun EditText?.toFloat(defVal: Float = 0f): Float {
    return if (txt().isEmpty()) defVal else txt().toFloat()
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

inline fun <C> trying2(func: () -> C) = try {
    func()
} catch (e: Exception) {
    e.printStackTrace()
    null
}

fun View?.visible(visible: Boolean = true): Boolean {
    this?.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    return visible
}

fun View?.gone(isGone: Boolean = true): Boolean {
    this?.visibility = if (isGone) View.GONE else View.VISIBLE
    return isGone
}

fun Array<out View?>.visible(visible: Boolean = true): Boolean {
    setVisibleState(if (visible) View.VISIBLE else View.INVISIBLE)
    return visible
}

fun Array<out View?>.gone(isGone: Boolean = true): Boolean {
    setVisibleState(if (isGone) View.GONE else View.VISIBLE)
    return isGone
}

fun Array<out View?>.setVisibleState(state: Int) {
    for (i in indices) {
        get(i)?.visibility = state
    }
}

fun Context?.hideKeyboard(hostView: View?): Boolean? {
    var isHide: Boolean = false
    hostView?.windowToken?.run {
        isHide = (this@hideKeyboard?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(this, InputMethodManager.HIDE_NOT_ALWAYS)
    }
    return isHide
}

fun Activity?.hideKeyboard(): Boolean? {
    return this?.run { hideKeyboard(window.decorView) } ?: false
}

fun Context?.hideKeyboard(): Boolean? {
    if (this is Activity)
        return this.run { hideKeyboard(window.decorView) } ?: false
    return false
}

fun Context?.showKeyboard(etText: EditText?): Boolean? {
    etText?.requestFocus()
    val imm = this?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    return imm?.showSoftInput(etText, InputMethodManager.SHOW_IMPLICIT)
}

fun Context?.toggleKeyboard() {
    (this?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)?.run {
        toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }
}

fun dpToPx(dp: Int): Int {
    return Resources.getSystem().displayMetrics.run {
        Math.round(dp * (xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }
}

fun Context?.isServiceRunning(serviceClass: Class<*>): Boolean {
    return this?.run {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                println("isServiceRunning " + serviceClass.toString() + " true")
                return true
            }
        }
        println("isServiceRunning " + serviceClass.toString() + " false")
        return false
    } ?: false
}

/**
 * auto cast types and find view
 * @param id id view for findViewById
 * @return Необходимую вью
</C> */
fun <C : View?> View.find(id: Int) = findViewById<C?>(id)

fun <C : View?> Activity.find(id: Int) = findViewById<C?>(id)

inline fun ViewGroup.forEach(action: View.() -> Unit) {
    (0 until childCount).forEach { getChildAt(it).action() }
}

/**
 * @param format
 * @return current formated date
 */
fun Calendar.getDate(format: String): String {
    return time.formatToString(format)
}

fun Date.formatToString(format: String) = SimpleDateFormat(format).format(this)

fun getDateFromString(format: String, date: String): Date? {
    val _format = SimpleDateFormat(format)
    try {
        return _format.parse(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return null
}

fun View?.startAnimation(anim: Int, l: Animation.AnimationListener? = null): Animation? {
    return this?.run {
        clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, anim)
        l?.apply { animation.setAnimationListener(l) }
        startAnimation(animation)
        return animation
    }
}

/**
 * @param secconds
 * *
 * @return
 */
fun getDuration(secconds: Int): String {
    val buf = StringBuffer()
    val millis = secconds * 1000
    val hours = millis / (1000 * 60 * 60)
    val minutes = millis % (1000 * 60 * 60) / (1000 * 60)
    val seconds = millis % (1000 * 60 * 60) % (1000 * 60) / 1000

    if (hours > 0) {
        buf.append(String.format("%02d", hours)).append(":")
    }
    buf.append(String.format("%02d", minutes))
            .append(":")
            .append(String.format("%02d", seconds))

    return buf.toString()
}

fun Double.formatCurrency(locale: Locale = Locale("ru", "RU")): String {
    return NumberFormat.getCurrencyInstance(locale).apply {
        isGroupingUsed = true
        minimumFractionDigits = 0
    }.format(this)
}

fun String.match(regexp: String): Boolean {
    return Pattern.compile(regexp).matcher(this).find()
}

fun Context.isInternetAvailable(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return cm.activeNetworkInfo?.isConnectedOrConnecting ?: false
}

/**
 * Device name
 */
fun getDeviceName(): String? {
    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
    if (model.startsWith(manufacturer)) {
        return model.capitalize()
    } else {
        return manufacturer.capitalize() + " " + model
    }
}

/**
 * Upper first letter of string
 */
fun String?.capitalize() = this?.apply {
    return if (Character.isUpperCase(this[0])) this else {
        return Character.toUpperCase(this[0]) + this.substring(1)
    }
}

fun File.getImageOrientation(): Int {
    var rotate = 0
    trying {
        val orientation = ExifInterface(absolutePath).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        rotate = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            else -> 0
        }
    }
    return rotate
}

/**
 * Get id resource from attr
 * @param attr
 * @return
 */
fun Context.getAttributeResourceId(attr: Int): Int {
    return trying2<Int> {
        val typedValue = TypedValue()
        val resIdAttr = intArrayOf(attr)
        val a = obtainStyledAttributes(typedValue.data, resIdAttr)
        val resId = a.getResourceId(0, 0)
        a.recycle()
        return resId
    } ?: 0
}

/**
 * Get simple color resource from attr
 * @param attr
 * @return
 */
fun Context.colorAttr(@AttrRes attr: Int)
        = ContextCompat.getColor(this, getAttributeResourceId(attr))

fun Context.color(@ColorRes color: Int) = ContextCompat.getColor(this, color)

/**
 * Get simple drawable from attr
 * @param contenxt
 * @param attr
 * @return
 */
fun Context.drawable(@DrawableRes drawable: Int)
        = ContextCompat.getDrawable(this, drawable)

fun Context.drawableAttr(@AttrRes attr: Int)
        = ContextCompat.getDrawable(this, getAttributeResourceId(attr))

fun String.md5(): String? {
    return trying2<String> {
        val messageDigest = MessageDigest.getInstance("MD5").run {
            update(toByteArray())
            digest()
        }

        // Create Hex String
        val hexString = StringBuffer()
        for (i in messageDigest.indices) {
            var h = Integer.toHexString(0xFF and messageDigest[i].toInt())
            while (h.length < 2)
                h = "0" + h
            hexString.append(h)
        }
        return hexString.toString()
    }
}

/**
 * Перекрашивание иконок
 */
fun Drawable.setColorAccent(context: Context) {
    setColor(context, R.attr.colorAccent)
}

fun Drawable.setColor(context: Context, @AttrRes colorAttr: Int) {
    val color = context.getAttributeResourceId(colorAttr)
    setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
}

fun ImageView.setIconColor(context: Context, @AttrRes colorAttr: Int) {
    val color = context.getAttributeResourceId(colorAttr)
    drawable?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
}

/**
 * Set color to colorAccent
 * @param context
 * @param icon
 */
fun ImageView.setIconColorAccent(context: Context) {
    setIconColor(context, R.attr.colorAccent)
}

/**
 * Заменяет в исходной строке символы на похожие по написанию символы из другого языка.
 * Например, "Н" ("эн" русская) на "H" ("эйч" английская) и т.п.
 * @param line строка, в которой делается замена символов
 * @param engToRus true для перевода английских букв в русские, false - русских в английские
 * @return строка с заменёнными символами
 */
fun String.replaceSimilarLetters(engToRus: Boolean): String {
    var line = this
    val RUS = arrayOf("а", "в", "е", "к", "м", "н", "о", "п", "р", "с", "т", "у", "х")
    val ENG = arrayOf("a", "b", "e", "k", "m", "h", "o", "n", "p", "c", "t", "y", "x")
    val source = if (engToRus) ENG else RUS
    val destination = if (engToRus) RUS else ENG
    for (n in source.indices)
        line = line.replace(source[n].toRegex(), destination[n])
                .replace(source[n].toUpperCase().toRegex(), destination[n].toUpperCase())
    return line
}

inline fun log(tag: String = "supperloger", text: () -> Any?) {
    val txt = text()?.toString()
    Log.d(tag, "$txt")
}

inline fun String?.isNotEmpty(action: (String) -> Unit): String? {
    return if (!this.isNullOrEmpty()) {
        action(this!!); this
    } else null
}

inline fun String?.ifEmpty(action: () -> Unit): String? {
    return if (this.isNullOrEmpty()) {
        action(); ""
    } else null
}

fun String?.ifNotEmpty() = if (!this.isNullOrEmpty()) this else null

// A method to find height of the status bar
fun Context.getStatusBarHeight(): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}

/**
 * Inflate view
 */
fun <T : View> Context.inflate(layout: Int, parent: ViewGroup? = null) =
        LayoutInflater.from(this).inflate(layout, parent, false) as T

/**
 * Inflate view
 */
fun <T : View> View.inflate(layout: Int, parent: ViewGroup? = null)
        = context.inflate<T>(layout, parent)
