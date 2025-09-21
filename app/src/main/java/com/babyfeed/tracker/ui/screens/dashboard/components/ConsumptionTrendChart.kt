package com.babyfeed.tracker.ui.screens.dashboard.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.babyfeed.tracker.ui.screens.dashboard.LineChartData
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.entryModelOf

@Composable
fun ConsumptionTrendChart(data: List<LineChartData>) {
    if (data.isEmpty()) return

    val modelProducer = remember { CartesianChartModelProducer.build() }
    val bottomAxisValueFormatter = CartesianValueFormatter { value, _, _ -> data[value.toInt()].day }

    LaunchedEffect(data) {
        modelProducer.tryRunTransaction {
            entryModelOf(data.map { it.amount })
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(valueFormatter = bottomAxisValueFormatter),
        ),
        modelProducer = modelProducer,
    )
}
