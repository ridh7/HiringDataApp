package com.example.hiringdataapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hiringdataapp.model.Item
import com.example.hiringdataapp.repository.ItemRepository
import kotlinx.coroutines.launch

class ItemViewModel : ViewModel() {
    private val repository = ItemRepository()

    private val _items = MutableLiveData<Map<Int, List<Item>>>()
    val items: LiveData<Map<Int, List<Item>>> = _items

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        fetchItems()
    }

    fun fetchItems() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val fetchedItems = repository.getItems()
                // Group items by listId
                _items.value = fetchedItems.groupBy { it.listId }
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.message ?: "Unknown error occurred"
            }
        }
    }
}