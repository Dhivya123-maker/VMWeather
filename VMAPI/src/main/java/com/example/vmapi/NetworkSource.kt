package com.vmapi

import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

abstract class NetworkSource {

    data class Resource<out T>(val status: Status, val response: T?, val message: String?) {

        enum class Status { SUCCESS, ERROR, LOADING }

        companion object {
            fun <T> success(data: T): Resource<T> {
                return Resource(Status.SUCCESS, data, null)
            }

            fun <T> error(message: String, data: T? = null): Resource<T> {
                return Resource(Status.ERROR, data, message)
            }

            fun <T> loading(data: T? = null): Resource<T> {
                return Resource(Status.LOADING, data, null)
            }
        }
    }

    fun getRetrofit(baseUrl: String): WeatherService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
            .create(WeatherService::class.java)
    }

}