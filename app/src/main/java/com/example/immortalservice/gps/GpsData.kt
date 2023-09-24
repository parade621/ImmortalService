package com.example.immortalservice.gps

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData

object  GpsData {
    val isServiceRunning = MutableLiveData<Boolean>(false)

    var lat = 0.0
    var lon = 0.0
    var accuracy = 0.0
    var lastUpdatedTime: Long = 0



    fun startGpsService(context: Context) {
        if(!isServiceRun(context)) {
            val intent = Intent(context, GoogleGpsService::class.java)
            ContextCompat.startForegroundService(context, intent)
            Toast.makeText(context, "Service Start", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopGpsService(context: Context) {
        if(isServiceRun(context)) {
            val intent = Intent(context, GoogleGpsService::class.java)
            context.stopService(intent)
        }

    }

    private fun isServiceRun(context: Context): Boolean {
        if (isServiceRunning.value == true) {
            return true
        }
        return false
    }

}