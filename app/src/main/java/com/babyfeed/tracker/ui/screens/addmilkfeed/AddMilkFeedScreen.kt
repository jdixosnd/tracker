package com.babyfeed.tracker.ui.screens.addmilkfeed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.babyfeed.tracker.data.local.MilkFeed
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMilkFeedScreen(
    childId: Int, // Needs to be passed in
    viewModel: AddMilkFeedViewModel = hiltViewModel(),
    onFeedAdded: () -> Unit
) {
    var amountOffered by remember { mutableStateOf("") }
    var amountConsumed by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val amountWasted = (amountOffered.toIntOrNull() ?: 0) - (amountConsumed.toIntOrNull() ?: 0)

    // TODO: Add Date and Time pickers

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add Milk Feed") })
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
                value = amountOffered,
                onValueChange = { amountOffered = it.filter { c -> c.isDigit() } },
                label = { Text("Amount Offered (ml)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = amountConsumed,
                onValueChange = { amountConsumed = it.filter { c -> c.isDigit() } },
                label = { Text("Amount Consumed (ml)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = if (amountWasted >= 0) amountWasted.toString() else "0",
                onValueChange = {},
                readOnly = true,
                label = { Text("Amount Wasted (ml)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    val feed = MilkFeed(
                        childId = childId,
                        timestamp = Calendar.getInstance().timeInMillis,
                        amountOffered = amountOffered.toIntOrNull() ?: 0,
                        amountConsumed = amountConsumed.toIntOrNull() ?: 0,
                        notes = notes.takeIf { it.isNotBlank() }
                    )
                    viewModel.addMilkFeed(feed)
                    onFeedAdded()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = amountOffered.isNotBlank() && amountConsumed.isNotBlank()
            ) {
                Text("Save Feed")
            }
        }
    }
}
