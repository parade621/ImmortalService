package com.example.immortalservice.gps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

/**
 * 설명
 *
 * 코드에 있는 [LocationCallback]과 [LocationResult]는 Google Play 서비스의 위치 API와 연관되어 있다.
 * 이 API는 Android 기기에서 위치 정보를 얻기 위해 Google Play 서비스를 사용한다.
 *
 * [FusedLocationProviderClient]는 이 API의 주요 클래스 중 하나로, 앱에 최적화된 위치 업데이트를 제공한다.
 */

class GpsWorker(private val context: Context) {

    private val locationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    private var MIN_INTERVAL_UPDATES: Long = (1000 * 60).toLong() // 1분에 한번씩 업데이트 진행
    private var MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 500

    init {
        GpsData.lat = 0.0
        GpsData.lon = 0.0
    }

    /**
     * 위치 업데이트 시작 함수
     */
    fun startLocationUpdates() {
        // 위치 권한 허용 여부를 확인
        // ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION 두 권한 중 하나라도 부여되지 않았다면 함수를 종료
        if(ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED){
                    return
        }

        // 최근 위치 정보를 가져옴
        // 위치를 성공적으로 가져온 경우, 해당 위치 데이터를 GpsData 객체에 저장
        locationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                GpsData.apply{
                    lat = location.latitude
                    lon = location.longitude
                    accuracy = location.accuracy.toDouble()
                }
            }
        }

        // 위치 업데이트 요청을 위한 옵션들 구성
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, MIN_INTERVAL_UPDATES).apply{
            setMinUpdateDistanceMeters(MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat())
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()

        // 위에서 작성한 설정을 기반으로 위치 업뎃 요청
        locationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback, // 위치 업뎃 콜백 함수(하단 함수 참고)
            Looper.getMainLooper() // 메인 스레드에서 위치 업데이트 처리
            // 포그라운드에서 콜백을 안전하게 처리하기 위해 Looper.getMainLooper() 사용.
        )
    }

    // LocationCallback 객체의 인스턴스 생성
    private val locationCallback: LocationCallback = object : LocationCallback() {
        // 위치 정보 업데이트 시 호출됨
        // locationResult 객체는 최근의 위치 정보를 가짐
        override fun onLocationResult(locationResult: LocationResult) {
            // locationResult.lactions[0]이 최신 위치 객체
            if (locationResult.locations[0] != null) {
                GpsData.apply{
                    lat = locationResult.locations[0].latitude
                    lon = locationResult.locations[0].longitude
                    lastUpdatedTime = System.currentTimeMillis()
                }
            }
        }
    }
    fun removeLocationUpdates() {
        // 위치 업데이트 중지 처리 로직
        locationProviderClient.removeLocationUpdates(locationCallback)
    }
}