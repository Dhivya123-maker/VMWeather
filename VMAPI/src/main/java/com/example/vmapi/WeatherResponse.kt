package com.vmapi


import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("weather")
    val weather : List<Weather>,
    @SerializedName ("main")
    val main: Main,
    @SerializedName ("wind")
    val wind: Wind,
    @SerializedName("dt")
    val dt: Long,
    @SerializedName("sys")
    val sys: Sys,
    @SerializedName("name")
    val name: String,



) {
    data class Weather(
        @SerializedName("description")
        val description: String,
        @SerializedName("icon")
        val icon: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("main")
        val main: String
    )

    data class Main(
        @SerializedName("temp")
        val temp: Double,
        @SerializedName("feels_like")
        val feelsLike: Double,
        @SerializedName("temp_min")
        val temp_min: Double,
        @SerializedName("temp_max")
        val temp_max: Double,
        @SerializedName("humidity")
        val humidity: Int,

    )
    data class Wind(
        @SerializedName("speed")
        val speed: Double,

        )
    data class Sys(
        @SerializedName("country")
        val country: String,
        @SerializedName("sunrise")
        val sunrise: Long,
        @SerializedName("sunset")
        val sunset: Long,

        )


}