package com.babyfeed.tracker.ui.screens.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.babyfeed.tracker.data.local.Child
import com.babyfeed.tracker.ui.screens.dashboard.components.ConsumptionTrendChart
import com.babyfeed.tracker.ui.screens.dashboard.components.DailyTotalsChart
import com.babyfeed.tracker.ui.screens.dashboard.components.FeedsByDayLog
import com.babyfeed.tracker.ui.screens.dashboard.components.MetricCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onManageChildren: () -> Unit,
    onAddMilkFeed: (Int) -> Unit,
    onNavigateToMedication: () -> Unit
) {
    val uiState by viewModel.dashboardUiState.collectAsState()
    val activeChild by viewModel.activeChild.collectAsState()
    val allChildren by viewModel.children.collectAsState(initial = emptyList())
    var showChildSwitcher by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.clickable { showChildSwitcher = true }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(activeChild?.name ?: "Dashboard")
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Switch Child")
                            DropdownMenu(
                                expanded = showChildSwitcher,
                                onDismissRequest = { showChildSwitcher = false }
                            ) {
                                allChildren.forEach { child ->
                                    DropdownMenuItem(
                                        text = { Text(child.name) },
                                        onClick = {
                                            viewModel.setActiveChild(child)
                                            showChildSwitcher = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                actions = {
                    TextButton(onClick = onManageChildren) {
                        Text("Manage")
                    }
                }
            )
        },
        floatingActionButton = {
            activeChild?.let {
                FloatingActionButton(onClick = { onAddMilkFeed(it.id) }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Milk Feed")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top-level metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricCard(title = "Today", value = "${uiState.todayTotal} ml", modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(16.dp))
                MetricCard(title = "Yesterday", value = "${uiState.yesterdayTotal} ml", modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(16.dp))
                MetricCard(title = "7-Day Avg", value = "${uiState.dailyAverageLast7Days} ml", modifier = Modifier.weight(1f))
            }

            // Deeper Insights
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Deeper Insights", style = MaterialTheme.typography.titleLarge)
                    Divider()
                    InsightRow(label = "Last 24h Total", value = "${uiState.last24hTotal} ml")
                    InsightRow(label = "Avg. Feeds per Day", value = uiState.avgFeedsPerDayLast7Days.toString())
                    InsightRow(label = "Wasted (last 7 days)", value = "${uiState.wastedLast7Days} ml")
                    InsightRow(label = "Most Recent Feed", value = uiState.mostRecentFeedTimestamp?.let {
                        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(it))
                    } ?: "N/A")
                }
            }

            // Medication Insights
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Medication", style = MaterialTheme.typography.titleLarge)
                    Divider()
                    InsightRow(label = "Next Due", value = uiState.nextMedication ?: "None scheduled")
                    InsightRow(label = "Last Given", value = uiState.lastDoseGiven ?: "No doses logged")
                }
            }

            // Charts
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Daily Totals", style = MaterialTheme.typography.titleLarge)
                    DailyTotalsChart(data = uiState.dailyTotalsChartData)
                }
            }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Consumption Trend (Last 7 Days)", style = MaterialTheme.typography.titleLarge)
                    ConsumptionTrendChart(data = uiState.consumptionTrendChartData)
                }
            }

            // Feeds By Day Log
            Text("Feed Log", style = MaterialTheme.typography.titleLarge)
            FeedsByDayLog(feedsByDay = uiState.feedsByDay)

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNavigateToMedication) {
                Text("Medication")
            }
        }
    }
}

@Composable
fun InsightRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}
