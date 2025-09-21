package com.babyfeed.tracker.ui.screens.dashboard.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.babyfeed.tracker.ui.screens.dashboard.DailyChartData
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.stackedEntryModelOf

@Composable
fun DailyTotalsChart(data: List<DailyChartData>) {
    if (data.isEmpty()) return

    val modelProducer = remember { CartesianChartModelProducer.build() }
    val bottomAxisValueFormatter = CartesianValueFormatter { value, _, _ -> data[value.toInt()].day }


    LaunchedEffect(data) {
        modelProducer.tryRunTransaction {
            val (consumed, wasted) = data.map { it.consumed } to data.map { it.wasted }
            stackedEntryModelOf(consumed, wasted)
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(),
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(valueFormatter = bottomAxisValueFormatter),
        ),
        modelProducer = modelProducer,
    )
}
