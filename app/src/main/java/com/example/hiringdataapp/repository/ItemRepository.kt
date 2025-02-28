package com.example.hiringdataapp.repository

import com.example.hiringdataapp.model.Item
import com.example.hiringdataapp.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ItemRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun getItems(): List<Item> {
        return withContext(Dispatchers.IO) {
            val items = apiService.getItems()

            // Filter out items with null or blank names
            val filteredItems = items.filter { !it.name.isNullOrBlank() }

            // Sort by listId then by name
            // For name sorting, extract the number part for proper numerical sorting
            filteredItems.sortedWith(compareBy<Item> { it.listId }
                .thenBy {
                    // Extract number from name (if it contains "Item" followed by a number)
                    val regex = "Item (\\d+)".toRegex()
                    val matchResult = regex.find(it.name ?: "")
                    matchResult?.groupValues?.get(1)?.toIntOrNull() ?: Int.MAX_VALUE
                })
        }
    }
}