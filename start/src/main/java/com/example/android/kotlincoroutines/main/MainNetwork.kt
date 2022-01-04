package com.example.android.kotlincoroutines.main

import com.example.android.kotlincoroutines.util.SkipNetworkInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private val service: MainNetwork by lazy {
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(SkipNetworkInterceptor())
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    retrofit.create(MainNetwork::class.java)
}

fun getNetworkService() = service

/**
 * Основной сетевой интерфейс, который подарит нам новый приветственный заголовок.
 * Для поддержки suspend функции требуется Retrofit версия 2.6.0 или более поздняя.
 *
 * Здесь мы возвращаемся String, но вы также можете вернуть сложный тип с поддержкой json.
 * Если вы все еще хотите предоставить доступ к полной версии Retrofit Result,
 * вы можете вернуть Result<String>вместо String.
 */
interface MainNetwork {
    @GET("next_title.json")
    suspend fun fetchNextTitle(): String
}


