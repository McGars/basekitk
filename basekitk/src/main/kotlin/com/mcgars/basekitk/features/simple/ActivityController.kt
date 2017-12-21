package com.mcgars.basekitk.features.simple

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem

/**
 *
 */
interface ActivityHolder {
    fun setActivity(activity: Activity)
}

/**
 * Created by Владимир on 12.10.2015.
 * Заменяет лайфсайкл активити
 * Так как мы не можем унаследовать все активити от базового активити
 * то используеться этот [ActivityController]
 */
open class ActivityController : ActivityHolder {

    override fun setActivity(activity: Activity) {
        // ignore
    }

    open fun onResume() {

    }

    open fun onDestroy() {

    }

    open fun onBackPressed(): Boolean {
        return false
    }

    open fun onPause() {

    }

    open fun onCreate(savedInstanceState: Bundle?) {

    }

    open fun onOptionsItemSelected(item: MenuItem): Boolean {
        return false
    }

    open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

    }

    open fun onStart() {}

    open fun onStop() {}

}
