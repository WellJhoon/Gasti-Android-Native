package com.jhonmedina.gasty.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// =========================================================================
// 🚀 CLIENTE HTTP SINGLETON (Equivalente a axios.create({ baseURL: ... }))
// =========================================================================
object RetrofitClient {

    // 💡 NOTA PARA EMULADOR ANDROID:
    // En el emulador Android, 'localhost' o '127.0.0.1' apunta AL PROPIO TELÉFONO.
    // Para acceder al localhost de tu computadora de desarrollo,
    // Google Android reserva la IP especial: 10.0.2.2
    private const val BASE_URL = "http://10.0.2.2:4000/api/v1/"

    // Almacén en memoria del token JWT (se actualiza al hacer login)
    private var authToken: String? = null

    fun setAuthToken(token: String?) {
        authToken = token
    }

    fun getAuthToken(): String? = authToken

    // Interceptor para agregar 'Authorization: Bearer <token>' automáticamente
    // (Igual a un axios.interceptors.request.use(config => ...))
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        authToken?.let { token ->
            requestBuilder.header("Authorization", "Bearer $token")
        }

        chain.proceed(requestBuilder.build())
    }

    // Interceptor de Logs para ver las peticiones y respuestas en Logcat
    // (Igual a la pestaña "Network" de las DevTools del navegador)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: GastyApiService by lazy {
        retrofit.create(GastyApiService::class.java)
    }
}
