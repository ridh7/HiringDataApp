package com.example.hiringdataapp.network

import com.example.hiringdataapp.model.Item
import retrofit2.http.GET

interface ApiService {
    @GET("hiring.json")
    suspend fun getItems(): List<Item>
}

