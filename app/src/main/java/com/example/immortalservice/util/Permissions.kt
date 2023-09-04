package com.example.immortalservice.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat


/**
 * 콜백 인터페이스 정의
 *
 * 권한 요청의 결과(허용/거부)에 따라 호출될 메서드들을 포함하는 인터페이스를 정의하였습니다.
 */
interface PermissionCallback {
    fun onPermissionGranted()
    fun onPermissionDenied()
}

/**
 * 권한 요청 시, 호출하는 클래스입니다.
 * 이런 식으로 작성하면 유지보수가 한결 수월해서 필자는 이런식으로 권한을 관리하는 클래스를 작성하는 것을
 * 선호합니다.
 *
 * [context]: 인자로 Permission검사를 진행하는 액티비티의 context를 받습니다.
 *
 * [callback]: 권한 관련 작업을 수행하는 클래스에 위에서 정의한 콜백 인터페이스를 추가하였습니다.
 * 이 콜백은 권한 요청 결과를 액티비티나 프래그먼트에 전달하는 역할을 합니다.
 *
 * [permissions]: 여기서는 이렇게 내부에 검사를 진행할 권한들을 작성하였지만,
 * 보통 실제 프로젝트에서는 인자로 검사하려는 권한들의 리스트를 넘겨 받기도 합니다.
 */
class PermissionsAsk(val context: Context, private val callback: PermissionCallback) {

    val PERMISSION_REQUEST_CODE = 100
    val permissions =
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS
        )

    init {
        checkPermissions(permissions)
    }

    // 단수 권한 체크 함수
    private fun checkPermission(): Boolean {
        val result =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED
    }

    // 복수의 권한 체크 함수
    fun checkPermissions(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    // 권한 요청 결과 처리 함수
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callback.onPermissionGranted()
                } else {
                    callback.onPermissionDenied()
                }
            }
        }
    }

}