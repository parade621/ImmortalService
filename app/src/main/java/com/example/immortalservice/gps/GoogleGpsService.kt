package com.example.immortalservice.gps


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.immortalservice.MainActivity
import com.example.immortalservice.R
import com.example.immortalservice.gps.GpsData.isServiceRunning


class GoogleGpsService : Service() {

    companion object {
        const val NOTIFICATION_ID = 1
        const val channelId = "GPS_Service"
    }

    private lateinit var googleWorker: GoogleWorker

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        try {
            startForeground(NOTIFICATION_ID, createChannel().build())
            isServiceRunning.postValue(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        createFusedProvider()

        return START_STICKY
    }


    private fun createChannel(): NotificationCompat.Builder {

        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            val channel = NotificationChannel(
                channelId,
                "Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
        }

        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.check_on)
            .setContentTitle("GPS Immortal Service")
            .setContentText("Location is Running in the Background")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
    }

    private fun createFusedProvider() {
        googleWorker = GoogleWorker(this)
        googleWorker.startLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            googleWorker.removeLocationUpdates()
            isServiceRunning.postValue(false)
        } catch (e: Exception) {
            //
        }
    }
}