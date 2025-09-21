package com.babyfeed.tracker.ui.screens.medication

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationLogScreen(
    viewModel: MedicationViewModel = hiltViewModel()
) {
    val doseHistory by viewModel.doseHistory.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Medication Log") })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            items(doseHistory) { dose ->
                val date = Date(dose.timestamp)
                val dateText = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(date)
                ListItem(
                    headlineContent = { Text("${dose.name} (${dose.dosage})") },
                    supportingContent = { Text(dateText) }
                )
                Divider()
            }
        }
    }
}
