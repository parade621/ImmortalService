package com.example.immortalservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.immortalservice.gps.GpsData

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GpsData.startGpsService(this@MainActivity)
    }
}