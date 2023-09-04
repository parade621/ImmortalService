package com.example.immortalservice.gps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*


class GoogleWorker(private val context: Context) {

    private val googleLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    private var MIN_INTERVAL_UPDATES: Long = (1000 * 60).toLong() // 1분에 한번씩 업데이트 진행
    private var MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 500

    init {
        GpsData.lat = 0.0
        GpsData.lon = 0.0
    }

    fun startLocationUpdates() {
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

        googleLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                GpsData.lat = location.latitude
                GpsData.lon = location.longitude
                GpsData.accuracy = location.accuracy.toDouble()
            }
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, MIN_INTERVAL_UPDATES).apply{
            setMinUpdateDistanceMeters(MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat())
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()

        googleLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (locationResult.locations[0] != null) {
                GpsData.lat = locationResult.locations[0].latitude
                GpsData.lon = locationResult.locations[0].longitude
                GpsData.lastUpdatedTime = System.currentTimeMillis()
            }
        }
    }
    fun removeLocationUpdates() {
        googleLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}