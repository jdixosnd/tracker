package com.babyfeed.tracker.ui.screens.medication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.babyfeed.tracker.data.local.Medication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationDashboardScreen(
    viewModel: MedicationViewModel = hiltViewModel(),
    onAddMedication: (Int) -> Unit,
    onNavigateToLog: () -> Unit
) {
    val childId by viewModel.childId.collectAsState(initial = -1)
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Active", "History")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medication") },
                actions = {
                    TextButton(onClick = onNavigateToLog) {
                        Text("Log")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { if (childId != -1) onAddMedication(childId) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Medication")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
            when (tabIndex) {
                0 -> ActiveMedicationsTab(viewModel)
                1 -> ArchivedMedicationsTab(viewModel)
            }
        }
    }
}

@Composable
fun ActiveMedicationsTab(viewModel: MedicationViewModel) {
    val activeMedications by viewModel.activeMedications.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(activeMedications) { medication ->
            ActiveMedicationItem(
                medication = medication,
                onLogDose = { viewModel.logDose(medication.id) },
                onArchive = { viewModel.archiveMedication(medication) }
            )
        }
    }
}

@Composable
fun ArchivedMedicationsTab(viewModel: MedicationViewModel) {
    val archivedMedications by viewModel.archivedMedications.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(archivedMedications) { medication ->
            ListItem(
                headlineContent = { Text(medication.name) },
                supportingContent = { Text("Dosage: ${medication.dosage}") }
            )
        }
    }
}

@Composable
fun ActiveMedicationItem(
    medication: Medication,
    onLogDose: () -> Unit,
    onArchive: () -> Unit
) {
    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = medication.name, style = MaterialTheme.typography.titleLarge)
            Text(text = "Dosage: ${medication.dosage}", style = MaterialTheme.typography.bodyMedium)
            // TODO: Display frequency and last dose
            Row(
                modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onLogDose) {
                    Text("Log Dose")
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(onClick = onArchive) {
                    Text("Archive")
                }
            }
        }
    }
}
