package com.example.immortalservice.gps


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.immortalservice.MainActivity
import com.example.immortalservice.R
import com.example.immortalservice.gps.GpsData.isServiceRunning


class GpsService : Service() {

    companion object {
        const val NOTIFICATION_ID = 1
        const val channelId = "GPS_Service"
    }

    // gpsWorker 인스턴스를 저장하는 변수
    private lateinit var gpsWorker: GpsWorker

    // 서비스 바인드 시, 호출되는 메서드지만, 바인드 하지 않음으로 null을 반환하도록 함.
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    // 서비스 시작 시, 호출되는 메서드
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        try {
            // 포그라운드 서비스로 시작, 실행 중임을 알리는 노티 표시
            startForeground(NOTIFICATION_ID, createChannel().build())
            isServiceRunning.postValue(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 위치 정보 제공자 생성
        createFusedProvider()

        return START_STICKY
    }


    // 노티 채널을 생성하는 메서드
    private fun createChannel(): NotificationCompat.Builder {

        // Android Oreo (API 26) 이상에서는 알림 채널을 생성해야 하기 때문에 작성
        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            val channel = NotificationChannel(
                channelId,
                "Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        // 알림을 탭하면 MainActivity로 이동하는 인텐트 생성
        val intent = Intent(this, MainActivity::class.java).apply {
        }

        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // 알림을 구성하고 반환
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.check_on)
            .setContentTitle("GPS Immortal Service")
            .setContentText("Location is Running in the Background")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
    }

    // GpsWorker 인스턴스를 생성하고 위치 업데이트를 시작하는 메서드
    private fun createFusedProvider() {
        gpsWorker = GpsWorker(this)
        gpsWorker.startLocationUpdates()
    }

    // 서비스가 완전 종료될 때 호출되는 메서드
    override fun onDestroy() {
        super.onDestroy()

        // 위치 업데이트를 중지하고 서비스 실행 상태를 업데이트
        gpsWorker.removeLocationUpdates()
        isServiceRunning.postValue(false)

    }
}