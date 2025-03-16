package com.example.hiringdataapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hiringdataapp.model.Item
import com.example.hiringdataapp.repository.ItemRepository
import kotlinx.coroutines.launch

// Extend ViewModel for lifecycle-aware data management in Android apps.
class ItemViewModel : ViewModel() {
    /*
    In larger apps, inject the repository (e.g., via Dagger/Hilt) for testability and flexibility:
    class ItemViewModel(private val repository: ItemRepository) : ViewModel()
     */
    private val repository = ItemRepository()

    /*
    Declares a private MutableLiveData to hold a map of items grouped by listId.
    The underscore prefix (_items) signals it’s a backing property for a public LiveData.
     */
    private val _items = MutableLiveData<Map<Int, List<Item>>>()
    /*
    Always expose LiveData publicly while keeping MutableLiveData
    private (the “backing property” pattern).
     */
    val items: LiveData<Map<Int, List<Item>>> = _items

    /*
    Defines a private MutableLiveData and public LiveData for tracking loading state.
    Notifies the UI when data is being fetched (e.g., to show/hide a progress bar).
     */
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    /*
    Defines a private MutableLiveData and public LiveData for error messages.
     */
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    /*
    An initializer block that calls fetchItems() when the ViewModel is created.
    Be cautious with init for heavy operations; consider exposing fetchItems()
    as a manual trigger if data isn’t always required immediately.
     */
    init {
        fetchItems()
    }

    fun fetchItems() {
        // Launches a coroutine in the viewModelScope.
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val fetchedItems = repository.getItems()
                /*
                Groups the fetched items by listId and updates _items.
                groupBy { it.listId } creates a Map<Int, List<Item>>, ideal
                for multi-section UIs.
                 */
                _items.value = fetchedItems.groupBy { it.listId }
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.message ?: "Unknown error occurred"
            }
        }
    }
}