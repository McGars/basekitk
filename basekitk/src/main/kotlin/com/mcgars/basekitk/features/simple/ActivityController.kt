package com.mcgars.basekitk.features.simple

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.mcgars.basekitk.features.simple.BaseKitActivity

/**
 * Created by Владимир on 12.10.2015.
 * Заменяет лайфсайкл активити
 * Так как мы не можем унаследовать все активити от базового активити
 * то используеться этот [ActivityController]
 */
class ActivityController<T : BaseKitActivity<ActivityController<T>>>(activity: T? = null) {
    var activity: Activity?= activity

    fun onResume() {

    }

    fun onDestroy() {

    }

    fun onBackPressed(): Boolean {
        return false
    }

    fun onPause() {

    }

    fun onCreate(savedInstanceState: Bundle?) {

    }

    fun onOptionsItemSelected(item: MenuItem): Boolean {
        return false
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

    }

    fun onStart() {}

    fun onStop() {}

}
