package com.example.hiringdataapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hiringdataapp.model.Item
import com.example.hiringdataapp.viewmodel.ItemViewModel
import kotlin.math.exp

class MainActivity : ComponentActivity() {

    private val viewModel: ItemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: ItemViewModel) {
    val items by viewModel.items.observeAsState(emptyMap())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState(null)

    var selectedSortOption by remember { mutableStateOf("id") }
    var expanded by remember { mutableStateOf(false) }
    val sortOptions = listOf("id", "name")

    val sortedItems = remember(items, selectedSortOption) {
        items.entries.sortedBy { it.key }.associate { (listId, groupItems) ->
            listId to when(selectedSortOption) {
                "id" -> groupItems.sortedBy { it.id }
                "name" -> groupItems.sortedBy { it.name }
                else -> groupItems
            }
        }
    }

    val context = LocalContext.current
    LaunchedEffect(error) { error?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() } }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                TextField(
                    value = "Sort by $selectedSortOption",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    sortOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text("Sort by: $option") },
                            onClick = {
                                selectedSortOption = option
                                expanded = false
                            }
                        )
                    }
                }
            }
            Box {
                PullRefresh(
                    isRefreshing = isLoading,
                    onRefresh = { viewModel.fetchItems() }
                ) {
                    if (isLoading && items.isEmpty()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            // Flatten groups into the single LazyColumn
                            sortedItems.forEach { (listId, groupItems) ->
                                // Add group header as an item
                                item {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(4.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "List ID: $listId",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(MaterialTheme.colorScheme.primary)
                                                .padding(8.dp)
                                        )
                                    }
                                }
                                // Add items for this group
                                items(groupItems) { item ->
                                    ItemRow(item)
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemRow(item: Item) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "ID: ${item.id}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Name: ${item.name ?: "N/A"}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


// Simplified Pull-to-Refresh (you can refine this with a library like Accompanist)
@Composable
fun PullRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    Box {
        content()
        if (isRefreshing) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            )
        }
        LaunchedEffect(Unit) {
            if (!isRefreshing) onRefresh() // Initial fetch handled by ViewModel init
        }
    }
}