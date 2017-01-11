package com.mcgars.basekitk.tools.permission

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity

import java.util.HashSet

/**
 * Created by Владимир on 09.10.2015.
 */
class BasePermissionController(private val activity: AppCompatActivity) {
    private var allRequest: ((Set<String>?) -> Unit)? = null
    private var onePermission: ((allow: Boolean) -> Unit)? = null

            /**
     * Request all permissions, after user done, system call method
     * Activity.onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
     * @param allRequest
     */
    @SuppressLint("NewApi")
    fun requestPermissions(allRequest: ((Set<String>?) -> Unit)) {
        if (permissions.size == 0 || Build.VERSION.SDK_INT < 23) {
            allRequest.invoke(null)
            return
        }
        this.allRequest = allRequest
        activity.requestPermissions(permissions.toTypedArray(), REQUEST_PERMISSIONS)
    }

    /**
     * Set this method to Activity inside
     * Activity.onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSIONS) {
            val permissionsAllow = HashSet<String>()
            permissions.indices
                    .filter { grantResults[it] == PackageManager.PERMISSION_GRANTED }
                    .mapTo(permissionsAllow) { permissions[it] }
            allRequest?.invoke(permissionsAllow)
            allRequest = null
        } else if (requestCode == REQUEST_PERMISSION) {
            onePermission?.invoke(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            onePermission = null
        }
    }

    /**
     * Check 1 permission
     * @param permission
     * @param onePermission
     */
    fun checkPermission(permission: String, onePermission: ((allow: Boolean) -> Unit)) {
        if (Build.VERSION.SDK_INT >= 23) {
            this.onePermission = onePermission
            if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                activity.requestPermissions(arrayOf(permission), REQUEST_PERMISSION)
                return
            }
        }
        onePermission.invoke(true)

    }

    companion object {
        var REQUEST_PERMISSIONS = 777
        var REQUEST_PERMISSION = 7777
        protected var permissions: MutableSet<String> = HashSet()

        fun addPermission(permission: String) {
            permissions.add(permission)
        }
    }

}
