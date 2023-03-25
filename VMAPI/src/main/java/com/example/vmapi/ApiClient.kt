package com.vmapi

import com.example.vmapi.Constants.APPID
//import com.example.vmapi.Constants.LATITUDE
//import com.example.vmapi.Constants.LONGITUDE
import com.example.vmapi.Constants.NAME


class ApiClient(
    private val baseUrl: String,
    private val name: String,
    private val lat: Double,
    private val lon: Double,
    private val appId: String,

) : NetworkSource() {

    suspend fun getWeatherResponse(): Resource<WeatherResponse?> {
        validateRequest()

        val response = getRetrofit(baseUrl).getWeather(NAME, lat,lon, APPID)

        return if (response.isSuccessful) {
            Resource.success(response.body())
        } else {
            Resource.error(response.message())
        }
    }

    private fun validateRequest(): Resource<WeatherResponse?> {
        var resource: Resource<WeatherResponse?> = Resource.loading()
        if (baseUrl.isEmpty()) {
            resource = Resource.error("URL should not be empty")
        } else if (appId.isEmpty()) {
            resource = Resource.error("APP Id should not be empty")
        }
        return resource
    }


    fun updateWeatherIcon(condition: Int): String {
        if (condition in 0..300) {
            return "thunderstorm"
        } else if (condition in 300..500) {
            return "lightrain"
        } else if (condition in 500..600) {
            return "shower"
        } else if (condition in 600..700) {
            return "heavysnow"
        } else if (condition in 701..771) {
            return "fog"
        } else if (condition in 772..800) {
            return "overcast"
        } else if (condition == 800) {
            return "sunny"
        } else if (condition in 801..804) {
            return "cloudy"
        } else if (condition in 900..902) {
            return "thunderstorm"
        }
        if (condition == 903) {
            return "lightsnow"
        }
        if (condition == 904) {
            return "sunny"
        }
        return if (condition in 905..1000) {
            "heavythunderstorm"
        } else "cloudy"
    }
}