package com.example.hiringdataapp.network

import com.example.hiringdataapp.model.Item
import retrofit2.http.GET

//Use interfaces for Retrofit services to keep the contract separate from implementation details
interface ApiService {
    @GET("hiring.json")
    suspend fun getItems(): List<Item>
}

