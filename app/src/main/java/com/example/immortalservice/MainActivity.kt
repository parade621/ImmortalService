package com.example.immortalservice

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.example.immortalservice.databinding.ActivityMainBinding
import com.example.immortalservice.gps.GpsData
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val isServiceRunning = Observer<Boolean> { isRunning ->
        if (!isRunning) {
            Log.e("tag", "감지!")
            GpsData.startGpsService(this@MainActivity)
            GpsData.isServiceRunning.postValue(true)
        }
    }

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // 권한 목록
    val permissions =
        arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.POST_NOTIFICATIONS
        )

    private var isAllPermissionGrant: Boolean = false

    // 권한 요청 코드
    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionsMap ->
            // 결과 처리. permissionsMap은 권한 이름을 키로, Boolean 값을 값으로 가집니다.

            if (permissions.all { permissionsMap[it] == true }) {
                isAllPermissionGrant = true
            } else {
                val deniedPermissions = permissions.filter { permissionsMap[it] != true }

                if (deniedPermissions.any { shouldShowRequestPermissionRationale(it) }) {
                    // 사용자가 권한 요청을 거절한 경우
                    //showRationaleDialog(deniedPermissions.toTypedArray())
                    //showRationaleDialog(deniedPermissions.toTypedArray())
                } else {
                    // 사용자가 권한 요청을 거절하고 다시 묻지 않음 옵션을 선택한 경우
                    showSettingsDialog()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            mainActivity = this@MainActivity
        }

        GpsData.isServiceRunning.observe(this, isServiceRunning)
        GpsData.startGpsService(this@MainActivity)
        // 권한 요청
        requestPermissionsLauncher.launch(permissions)
    }

    fun stopService() {
        GpsData.stopGpsService(this@MainActivity)
        Toast.makeText(this@MainActivity, "Service Stop", Toast.LENGTH_SHORT).show()
    }


    private fun showRationaleDialog(deniedPermissions: Array<String>) {
        // 권한을 거절했을 때 그 이유를 설명하는 다이얼로그
        AlertDialog.Builder(this)
            .setMessage("이 기능을 사용하기 위해 권한이 필요합니다.")
            .setPositiveButton("다시 요청") { _, _ ->
                requestPermissionsLauncher.launch(deniedPermissions)
            }
            .setNegativeButton("취소", null)
            .show()
    }


    private fun showSettingsDialog() {
        // 설정 화면으로 이동하여 권한을 수동으로 활성화하도록 유도하는 다이얼로그
        AlertDialog.Builder(this)
            .setMessage("권한을 활성화해야 이 기능을 사용할 수 있습니다. 설정 화면으로 이동하시겠습니까?")
            .setPositiveButton("설정으로 이동") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("취소", null)
            .show()
    }
}