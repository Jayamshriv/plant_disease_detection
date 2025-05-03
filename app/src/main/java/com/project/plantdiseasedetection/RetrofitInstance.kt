package com.project.plantdiseasedetection

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object RetrofitInstance {
    private var baseUrl = "http://192.168.0.106:5000/"

    private val gson = GsonBuilder()
        .setLenient()
        .serializeNulls()
        .create()

    private var retrofit: Retrofit? = null

    fun updateBaseUrl(newUrl: String) {
        baseUrl = newUrl
        retrofit = null  // Force recreation of Retrofit instance
    }

    private fun getRetrofit(context: android.content.Context): Retrofit {
        if (retrofit == null) {
            // Get saved URL from SharedPreferences
            baseUrl = ServerConfig.getServerUrl(context)

            val client = OkHttpClient.Builder().build()
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit!!
    }

    // Method to get API service
    fun getApi(context: android.content.Context): PlantApiService {
        return getRetrofit(context).create(PlantApiService::class.java)
    }
}