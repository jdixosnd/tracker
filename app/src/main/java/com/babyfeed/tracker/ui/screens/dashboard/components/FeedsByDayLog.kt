package com.babyfeed.tracker.ui.screens.dashboard.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.babyfeed.tracker.data.local.MilkFeed
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun FeedsByDayLog(feedsByDay: Map<LocalDate, List<MilkFeed>>) {
    val sortedDays = feedsByDay.keys.sortedDescending()
    var expandedStates by remember { mutableStateOf(mapOf<LocalDate, Boolean>()) }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sortedDays.forEach { date ->
            val feeds = feedsByDay[date] ?: emptyList()
            val isExpanded = expandedStates[date] ?: false

            item {
                DayHeader(
                    date = date,
                    feeds = feeds,
                    isExpanded = isExpanded,
                    onClick = { expandedStates = expandedStates + (date to !isExpanded) }
                )
                Divider()
            }

            if (isExpanded) {
                items(feeds) { feed ->
                    FeedItem(feed)
                }
            }
        }
    }
}

@Composable
fun DayHeader(
    date: LocalDate,
    feeds: List<MilkFeed>,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    val totalConsumed = feeds.sumOf { it.amountConsumed }
    val totalWasted = feeds.sumOf { it.amountOffered - it.amountConsumed }
    val dateText = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
    val summaryText = "${feeds.size} Feeds • $totalConsumed ml consumed • $totalWasted ml wasted"

    ListItem(
        headlineContent = { Text(text = dateText, fontWeight = FontWeight.Bold) },
        supportingContent = { Text(text = summaryText) },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
fun FeedItem(feed: MilkFeed) {
    val timeText = java.time.Instant.ofEpochMilli(feed.timestamp)
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalTime()
        .format(DateTimeFormatter.ofPattern("hh:mm a"))

    val wasted = feed.amountOffered - feed.amountConsumed
    val detailsText = "Offered ${feed.amountOffered}ml, ${wasted}ml left"

    ListItem(
        headlineContent = { Text(text = "$timeText - Consumed ${feed.amountConsumed}ml", fontWeight = FontWeight.SemiBold) },
        supportingContent = { Text(text = detailsText, style = MaterialTheme.typography.bodySmall) }
    )
}
