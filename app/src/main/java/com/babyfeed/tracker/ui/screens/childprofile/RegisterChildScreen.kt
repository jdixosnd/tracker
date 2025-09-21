package com.babyfeed.tracker.ui.screens.childprofile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.babyfeed.tracker.data.local.Child
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterChildScreen(
    viewModel: ChildProfileViewModel = hiltViewModel(),
    childId: Int,
    onProfileCreated: () -> Unit
) {
    val isEditMode = childId != -1

    var name by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf<Long?>(null) }
    var gender by remember { mutableStateOf<String?>(null) }
    val showDatePicker = remember { mutableStateOf(false) }

    if (isEditMode) {
        val child by viewModel.getChild(childId).collectAsState(initial = null)
        LaunchedEffect(child) {
            child?.let {
                name = it.name
                dob = it.dob
                gender = it.gender
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (isEditMode) "Edit Child Profile" else "Register Your Child") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Child's Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = { showDatePicker.value = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = dob?.let {
                        SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(it))
                    } ?: "Select Date of Birth"
                )
            }

            if (showDatePicker.value) {
                val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dob)
                DatePickerDialog(
                    onDismissRequest = { showDatePicker.value = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                dob = datePickerState.selectedDateMillis
                                showDatePicker.value = false
                            }
                        ) { Text("OK") }
                    },
                    dismissButton = {
                        Button(onClick = { showDatePicker.value = false }) { Text("Cancel") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = { gender = "Male" }, enabled = gender != "Male") { Text("Male") }
                Button(onClick = { gender = "Female" }, enabled = gender != "Female") { Text("Female") }
                Button(onClick = { gender = null }, enabled = gender != null) { Text("Prefer not to say") }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val finalDob = dob
                    if (name.isNotBlank() && finalDob != null) {
                        val child = Child(
                            id = if (isEditMode) childId else 0,
                            name = name,
                            dob = finalDob,
                            gender = gender,
                            profilePhotoUri = null // Photo picker not implemented yet
                        )
                        if (isEditMode) {
                            viewModel.updateChild(child)
                        } else {
                            viewModel.addChild(child)
                        }
                        onProfileCreated()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && dob != null
            ) {
                Text("Save Profile")
            }
        }
    }
}
