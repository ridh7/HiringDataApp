package com.example.hiringdataapp.repository

import com.example.hiringdataapp.model.Item
import com.example.hiringdataapp.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/*
Acts as a repository, abstracting data access (from the API) and
applying business logic (filtering/sorting).
Follow the Repository pattern to decouple data sources from the rest of the app.
 */
class ItemRepository {
    /*
    Provides access to the Retrofit API service for making network calls.
    In larger apps, inject apiService (e.g., via constructor injection with Dagger/Hilt)
    for testability:
    class ItemRepository(private val apiService: ApiService) { ... }
     */
    private val apiService = RetrofitClient.apiService

    /*
    Consider returning a Result<List<Item>> or Response<List<Item>>
    for error handling in production:
    suspend fun getItems(): Result<List<Item>>
     */
    suspend fun getItems(): List<Item> {
        /*
        Uses withContext to run the block on the Dispatchers.IO thread pool.
         */
        return withContext(Dispatchers.IO) {
            /*
            Handle potential exceptions (e.g., network errors) in production:
            using try catch
             */
            val items = apiService.getItems()

            /*
            Filters the items list, keeping only items where name is neither null nor blank.
             */
            val filteredItems = items.filter { !it.name.isNullOrBlank() }

            // Sort by listId then by name
            // For name sorting, extract the number part for proper numerical sorting
            filteredItems.sortedWith(compareBy<Item> { it.listId }
                .thenBy { // Chains a secondary sort using thenBy after compareBy.
                    // Extract number from name (if it contains "Item" followed by a number)
                    /*
                    The "Item (\\d+)" assumption is brittle. Add a fallback or validate the format:
                    val number = matchResult?.groupValues?.get(1)?.toIntOrNull() ?: it.name.hashCode()
                     */
                    val regex = "Item (\\d+)".toRegex() // Matches "Item " followed by one or more digits (\\d+), with the digits in a capture group.
                    val matchResult = regex.find(it.name ?: "") // Applies the regex to it.name, defaulting to "" if name is null.
                    matchResult?.groupValues?.get(1)?.toIntOrNull() ?: Int.MAX_VALUE // Tries to extract and convert the number from "Item N". If it can’t (due to no match, null name, or invalid format), it returns Int.MAX_VALUE.
                })
        }
    }
}