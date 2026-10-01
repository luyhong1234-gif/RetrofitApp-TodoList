package com.example.retrofitapp

// RetrofitClient.kt - Singleton Retrofit instance
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://retrofit-ead16-default-rtdb.asia-southeast1.firebasedatabase.app/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
