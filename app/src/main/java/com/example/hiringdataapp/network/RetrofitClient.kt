package com.example.hiringdataapp.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/*Using a singleton (object) ensures there’s only one Retrofit instance,
conserving resources and avoiding redundant setup.
For larger apps, inject apiService via Dagger/Hilt instead of a singleton
to improve testability.
 */
object RetrofitClient {

    /*
    Hardcoding BASE_URL limits flexibility.
    Use BuildConfig or a configuration class for dev/staging/prod URLs:
    private const val BASE_URL = if (BuildConfig.DEBUG) "https://dev.example.com/"
    else "https://prod.example.com/"
     */
    private const val BASE_URL = "https://fetch-hiring.s3.amazonaws.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        /*Sets the logging level to BODY, which logs full request/response bodies
        (including headers and payloads).
        Level.BODY is verbose, ideal for development but potentially overwhelming
        in production due to log size and security risks (e.g., leaking sensitive data).

         */
        level = HttpLoggingInterceptor.Level.BODY
    }

    /*Consider additional customizations (e.g., timeouts, retry policies) for production apps:
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
     */
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /*
    Creates an implementation of the ApiService interface using the Retrofit instance.
    Provides a public accessor to the API service for making network requests.
     */
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}