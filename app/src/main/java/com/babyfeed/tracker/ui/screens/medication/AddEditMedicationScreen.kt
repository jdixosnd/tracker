package com.babyfeed.tracker.ui.screens.medication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.babyfeed.tracker.data.local.Medication
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMedicationScreen(
    viewModel: MedicationViewModel = hiltViewModel(),
    childId: Int, // Will be passed from navigation
    medicationId: Int?, // Null for new medication
    onMedicationSaved: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var scheduleType by remember { mutableStateOf("AS_NEEDED") }
    var scheduleDetails by remember { mutableStateOf<String?>(null) }
    var startDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var instructions by remember { mutableStateOf("") }

    if (medicationId != null) {
        val medication by viewModel.getMedication(medicationId).collectAsState(initial = null)
        LaunchedEffect(medication) {
            medication?.let {
                name = it.name
                dosage = it.dosage
                scheduleType = it.scheduleType
                scheduleDetails = it.scheduleDetails
                startDate = it.startDate
                endDate = it.endDate
                instructions = it.instructions ?: ""
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (medicationId == null) "Add Medication" else "Edit Medication") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Medicine Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = dosage,
                onValueChange = { dosage = it },
                label = { Text("Dosage (e.g., 5 ml, 1 tablet)") },
                modifier = Modifier.fillMaxWidth()
            )

            ScheduleSelector(
                scheduleType = scheduleType,
                onScheduleTypeChange = { scheduleType = it },
                scheduleDetails = scheduleDetails,
                onScheduleDetailsChange = { scheduleDetails = it }
            )

            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it },
                label = { Text("Instructions (e.g., with food)") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DatePickerButton(
                    label = "Start Date",
                    selectedDate = startDate,
                    onDateSelected = { startDate = it },
                    modifier = Modifier.weight(1f)
                )
                DatePickerButton(
                    label = "End Date (Optional)",
                    selectedDate = endDate,
                    onDateSelected = { endDate = it },
                    modifier = Modifier.weight(1f)
                )
            }

            Button(
                onClick = {
                    val medication = Medication(
                        id = medicationId ?: 0,
                        childId = childId,
                        name = name,
                        dosage = dosage,
                        scheduleType = scheduleType,
                        scheduleDetails = scheduleDetails,
                        startDate = startDate,
                        endDate = endDate,
                        instructions = instructions
                    )
                    viewModel.addOrUpdateMedication(medication)
                    onMedicationSaved()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Medication")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleSelector(
    scheduleType: String,
    onScheduleTypeChange: (String) -> Unit,
    scheduleDetails: String?,
    onScheduleDetailsChange: (String?) -> Unit
) {
    val scheduleTypes = listOf("AS_NEEDED", "EVERY_X_HOURS", "X_TIMES_A_DAY")
    var expanded by remember { mutableStateOf(false) }

    Column {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = scheduleType.replace("_", " "),
                onValueChange = {},
                readOnly = true,
                label = { Text("Schedule") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                scheduleTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.replace("_", " ")) },
                        onClick = {
                            onScheduleTypeChange(type)
                            expanded = false
                        }
                    )
                }
            }
        }

        if (scheduleType == "EVERY_X_HOURS") {
            OutlinedTextField(
                value = scheduleDetails ?: "",
                onValueChange = { onScheduleDetailsChange(it) },
                label = { Text("Every X Hours") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
        }
        // TODO: Add UI for X_TIMES_A_DAY
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerButton(
    label: String,
    selectedDate: Long?,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Button(
        onClick = { showDatePicker = true },
        modifier = modifier
    ) {
        Text(
            text = selectedDate?.let {
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
            } ?: label
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                Button(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
