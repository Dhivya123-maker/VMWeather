package com.example.vmweather

import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import com.example.vmapi.Constants.APPID
import com.example.vmapi.Constants.NAME
import com.example.vmapi.Constants.LATITUDE
import com.example.vmapi.Constants.LONGITUDE
import com.example.vmapi.Constants.URL
import androidx.annotation.Nullable
import com.example.vmapi.Constants.CHANNEL_ID
import com.vmapi.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class MyService : Service() {

    lateinit var apiClient:ApiClient

    var city:String = ""
    var latitude:Double = 0.0
    var longitude:Double = 0.0

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, FLAG_IMMUTABLE
        )

                val bundle = intent.extras

                if(bundle!=null){
                    city = bundle.getString("city").toString()
                    latitude = bundle.getDouble("latitude")
                    longitude = bundle.getDouble("longitude")

                    apiClient = ApiClient(URL, city, latitude, longitude, APPID)

                }else{
                    apiClient = ApiClient(URL, NAME, LATITUDE, LONGITUDE, APPID)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    val name = apiClient.getWeatherResponse().response!!.name

                    val temp = apiClient.getWeatherResponse().response!!.main.temp - 273.15
                    val tempRoundedValue = Math.rint(temp).toInt().toString() + "°"

                    val weatherName = apiClient.getWeatherResponse().response!!.weather.get(0).main

                    val notification =tempRoundedValue+" in "+name+ "\n" +"Feels like "+ weatherName

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val notification = Notification.Builder(this@MyService, CHANNEL_ID)
                            .setContentTitle("Today's Weather")
                            .setContentText(notification)
                            .setSmallIcon(R.drawable.cloudy)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .build()
                        startForeground(1, notification)
                        stopForeground(false)

                    } else {
                        stopForeground(true)
                    }
                }



        return START_NOT_STICKY


    }

    override fun onDestroy() {

    }



    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}


