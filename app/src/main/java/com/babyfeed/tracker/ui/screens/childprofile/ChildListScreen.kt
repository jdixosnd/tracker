package com.babyfeed.tracker.ui.screens.childprofile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildListScreen(
    viewModel: ChildProfileViewModel = hiltViewModel(),
    onAddChild: () -> Unit,
    onEditChild: (Int) -> Unit
) {
    val children by viewModel.children.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Your Children") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddChild) {
                Icon(Icons.Default.Add, contentDescription = "Add Child")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(children) { child ->
                ListItem(
                    headlineContent = { Text(child.name) },
                    supportingContent = {
                        val formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(child.dob))
                        Text("DOB: $formattedDate")
                    },
                    trailingContent = {
                        Row {
                            IconButton(onClick = { onEditChild(child.id) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Child")
                            }
                            IconButton(onClick = { viewModel.deleteChild(child) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Child")
                            }
                        }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Divider()
            }
        }
    }
}
