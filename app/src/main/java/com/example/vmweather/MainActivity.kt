package com.example.vmweather

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.vmapi.Constants.APPID
import com.example.vmapi.Constants.LATITUDE
import com.example.vmapi.Constants.LONGITUDE
import com.example.vmapi.Constants.NAME
import com.example.vmapi.Constants.URL
import com.example.vmweather.databinding.ActivityMainBinding
import com.vmapi.ApiClient
import com.vmapi.NetworkSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var progress:Int?=0
   lateinit var apiClient:ApiClient


   var city:String = ""
    var latitude:Double = 0.0
    var longitude:Double = 0.0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        updateUI(true)

        val bundle = intent.extras
        apiClient = ApiClient(URL, NAME, LATITUDE, LONGITUDE, APPID)

        if(bundle!=null){

            startService()
          city = bundle.getString("city").toString()
         latitude = bundle.getDouble("latitude")
         longitude = bundle.getDouble("longitude")

            apiClient = ApiClient(URL, city, latitude, longitude, APPID)

    }else{
            startService()

        }


            val imageView: ImageView = findViewById(R.id.wind_image)
            Glide.with(this).load(R.drawable.wind_turbine).into(imageView)



            binding.menu.setOnClickListener {
                val popupMenu = PopupMenu(this, binding.menu)
                popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.city ->
                            startActivity(Intent(this@MainActivity, AddCityActivity::class.java))

                    }
                    true
                })
                popupMenu.show()
            }

            CoroutineScope(Dispatchers.IO).launch {

                val response = apiClient.getWeatherResponse()

                when (response.status) {
                    NetworkSource.Resource.Status.SUCCESS -> {
                        updateUI(false)
                        println(response.response)


                    }
                    NetworkSource.Resource.Status.ERROR -> {
                        println(response.message)
                    }
                    else -> {}
                }
            }


        }
        @SuppressLint("ResourceAsColor")
        private fun updateUI(isLoading: Boolean) {
            runOnUiThread {
                if (isLoading) {
                    binding.progress.visibility = View.VISIBLE
                    binding.rLayout.visibility = View.GONE

                } else {
                    binding.rLayout.visibility = View.VISIBLE

                    updateUIData()
                    binding.progress.visibility = View.GONE

                }


            }
        }

        private fun updateUIData() {
            CoroutineScope(Dispatchers.IO).launch {

                val name = apiClient.getWeatherResponse().response!!.name

                val temp = apiClient.getWeatherResponse().response!!.main.temp - 273.15
                val tempRoundedValue = Math.rint(temp).toInt().toString() + "°"

                val weatherName = apiClient.getWeatherResponse().response!!.weather.get(0).main

                val updatedAt = apiClient.getWeatherResponse().response!!.dt
                val updatedAtText =
                    "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                        Date(updatedAt * 1000)
                    )

                val humidity = apiClient.getWeatherResponse().response!!.main.humidity.toString()

                val feelsLike = apiClient.getWeatherResponse().response!!.main.feelsLike - 273.15
                val humidityRoundValue = Math.rint(feelsLike).toInt().toString() + "°"


                val windSpeed =
                    apiClient.getWeatherResponse().response!!.wind.speed.toString() + " km/hr"

                val sunrise = apiClient.getWeatherResponse().response!!.sys.sunrise
                val sunriseTime = Time(sunrise)
                val sunriseFormat = SimpleDateFormat("hh:mm a")
                val sunriseText = sunriseFormat.format(sunriseTime)

                val sunset = apiClient.getWeatherResponse().response!!.sys.sunset
                val sunsetTime = Time(sunset)
                val sunsetFormat = SimpleDateFormat("hh:mm a")
                val sunsetText = sunsetFormat.format(sunsetTime)

                val tempMax = apiClient.getWeatherResponse().response!!.main.temp_max - 273.15
                val tempMaxRoundeValue = Math.rint(tempMax).toInt().toString() + "°"
                val tempMin = apiClient.getWeatherResponse().response!!.main.temp_min - 273.15
                val tempMinRoundeValue = Math.rint(tempMin).toInt().toString() + "°"

                val tempResult = tempMaxRoundeValue + " / " + tempMinRoundeValue

                val weatherCondition = apiClient.getWeatherResponse().response!!.weather.get(0).id


                runOnUiThread {


                    binding.name.text = name
                    binding.temp.text = tempRoundedValue
                    binding.weatherType.text = weatherName
                    binding.updatedAt.text = updatedAtText
                    binding.humidity.text = humidity
                    binding.feelsLike.text = humidityRoundValue
                    binding.windSpeed.text = windSpeed
                    binding.sunrise.text = sunriseText
                    binding.sunset.text = sunsetText
                    binding.minMax.text = tempResult

                    val resourceID = resources.getIdentifier(
                        apiClient.updateWeatherIcon(weatherCondition),
                        "drawable",
                        packageName
                    )
                    binding.weatherIcon.setImageResource(resourceID)


                    progress = Integer.parseInt(binding.humidity.text.toString())
                    if (progress!! <= 90) {
                        binding.progressBar.progress = progress as Int
                        binding.humidity.text = humidity + "%"
                    }
                    if (progress!! >= 10) {
                        binding.progressBar.progress = progress as Int
                        binding.humidity.text = humidity + "%"
                    }
                }
            }

        }


        fun startService() {
            val bundle1 = intent.extras
            if(bundle1!=null){

                city = bundle1.getString("city").toString()
                latitude = bundle1.getDouble("latitude")
                longitude = bundle1.getDouble("longitude")

                val bundle = Bundle()
                bundle.putString("city",city)
                bundle.putDouble("latitude", latitude)
                bundle.putDouble("longitude",longitude)

                val intent = Intent(this, MyService::class.java)
                intent.putExtras(bundle)
                ContextCompat.startForegroundService(this, intent)

                println(bundle.toString())
            }else{
                val serviceIntent = Intent(this, MyService::class.java)
                ContextCompat.startForegroundService(this, serviceIntent)
            }

        }



    }

