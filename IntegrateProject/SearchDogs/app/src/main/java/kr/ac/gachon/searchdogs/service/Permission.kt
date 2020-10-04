/**
 * 스마트폰의 Permission을 제어
 *
 * @since: 2019.10.01
 * @author: 류일웅
 */
package kr.ac.gachon.searchdogs.service

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Permission {

    private val neededPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    /**
     * ##################################################
     * 권한 요청 기능
     *
     * @since: 2019.10.01
     * @author: 류일웅
     * @param: activity
     * @return:
     * ##################################################
     */
    fun requestPermission(activity: Activity) {
        ActivityCompat.requestPermissions(activity, neededPermissions,0)
    }

    /**
     * ##################################################
     * 권한 수락여부 확인 기능
     *
     * @since: 2019.10.01
     * @author: 류일웅
     * @param: context
     * @return: boolean
     * ##################################################
     */
    fun hasNoPermissions(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
    }

}
