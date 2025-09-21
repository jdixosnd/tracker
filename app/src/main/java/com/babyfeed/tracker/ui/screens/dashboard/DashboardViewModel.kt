package com.babyfeed.tracker.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babyfeed.tracker.data.local.Child
import com.babyfeed.tracker.data.repository.ChildRepository
import com.babyfeed.tracker.data.repository.MilkFeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val childRepository: ChildRepository,
    private val milkFeedRepository: MilkFeedRepository,
    private val medicationRepository: com.babyfeed.tracker.data.repository.MedicationRepository
) : ViewModel() {

    private val _activeChild = MutableStateFlow<Child?>(null)
    val activeChild: StateFlow<Child?> = _activeChild.asStateFlow()

    init {
        viewModelScope.launch {
            childRepository.getAllChildren().collect { children ->
                if (_activeChild.value == null || children.none { it.id == _activeChild.value?.id }) {
                    _activeChild.value = children.firstOrNull()
                }
            }
        }
    }

    fun setActiveChild(child: Child) {
        _activeChild.value = child
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val dashboardUiState: StateFlow<DashboardUiState> = activeChild.flatMapLatest { child ->
        if (child == null) {
            flowOf(DashboardUiState())
        } else {
            val now = Calendar.getInstance()
            val todayStart = (now.clone() as Calendar).apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }
            val yesterdayStart = (todayStart.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
            val sevenDaysAgoStart = (todayStart.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -7) }
            val last24hStart = (now.clone() as Calendar).apply { add(Calendar.HOUR_OF_DAY, -24) }

            val allFeedsFlow = milkFeedRepository.getFeedsForChild(child.id)
            val lastDoseFlow = medicationRepository.getLastDoseForChild(child.id)
            val activeMedicationsFlow = medicationRepository.getActiveMedications(child.id)

            combine(allFeedsFlow, lastDoseFlow, activeMedicationsFlow) { allFeeds, lastDose, activeMedications ->
                val todayFeeds = allFeeds.filter { it.timestamp >= todayStart.timeInMillis }
                val yesterdayFeeds = allFeeds.filter { it.timestamp >= yesterdayStart.timeInMillis && it.timestamp < todayStart.timeInMillis }
                val last7DaysFeeds = allFeeds.filter { it.timestamp >= sevenDaysAgoStart.timeInMillis }
                val last24hFeeds = allFeeds.filter { it.timestamp >= last24hStart.timeInMillis }

                val todayTotal = todayFeeds.sumOf { it.amountConsumed }
                val yesterdayTotal = yesterdayFeeds.sumOf { it.amountConsumed }
                val totalConsumedLast7Days = last7DaysFeeds.sumOf { it.amountConsumed }
                val dailyAverageLast7Days = if (last7DaysFeeds.isNotEmpty()) totalConsumedLast7Days / 7 else 0
                val last24hTotal = last24hFeeds.sumOf { it.amountConsumed }
                val avgFeedsPerDayLast7Days = if (last7DaysFeeds.isNotEmpty()) last7DaysFeeds.size / 7 else 0
                val wastedLast7Days = last7DaysFeeds.sumOf { it.amountOffered - it.amountConsumed }
                val mostRecentFeedTimestamp = allFeeds.maxOfOrNull { it.timestamp }

                val dailyTotalsChartData = (0..4).map { i ->
                    val day = (todayStart.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -i) }
                    val dayStart = (day.clone() as Calendar).apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }.timeInMillis
                    val dayEnd = (day.clone() as Calendar).apply { set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59) }.timeInMillis

                    val feedsForDay = allFeeds.filter { it.timestamp in dayStart..dayEnd }

                    val consumed = feedsForDay.sumOf { it.amountConsumed }.toFloat()
                    val wasted = feedsForDay.sumOf { it.amountOffered - it.amountConsumed }.toFloat()

                    val dayOfWeek = SimpleDateFormat("EEE", Locale.getDefault()).format(day.time)

                    DailyChartData(day = dayOfWeek, consumed = consumed, wasted = wasted)
                }.reversed()

                val consumptionTrendChartData = (0..6).map { i ->
                    val day = (todayStart.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -i) }
                    val dayStart = (day.clone() as Calendar).apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }.timeInMillis
                    val dayEnd = (day.clone() as Calendar).apply { set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59) }.timeInMillis

                    val feedsForDay = allFeeds.filter { it.timestamp in dayStart..dayEnd }

                    val amount = feedsForDay.sumOf { it.amountConsumed }.toFloat()

                    val dayOfMonth = SimpleDateFormat("d", Locale.getDefault()).format(day.time)

                    LineChartData(day = dayOfMonth, amount = amount)
                }.reversed()

                val feedsByDay = allFeeds.groupBy {
                    Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
                }

                val lastDoseGiven = lastDose?.let {
                    val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(it.timestamp))
                    "${it.name} at $time"
                }

                DashboardUiState(
                    todayTotal = todayTotal,
                    yesterdayTotal = yesterdayTotal,
                    dailyAverageLast7Days = dailyAverageLast7Days,
                    last24hTotal = last24hTotal,
                    avgFeedsPerDayLast7Days = avgFeedsPerDayLast7Days,
                    wastedLast7Days = wastedLast7Days,
                    mostRecentFeedTimestamp = mostRecentFeedTimestamp,
                    dailyTotalsChartData = dailyTotalsChartData,
                    consumptionTrendChartData = consumptionTrendChartData,
                    feedsByDay = feedsByDay,
                    lastDoseGiven = lastDoseGiven
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )
}

data class DashboardUiState(
    val todayTotal: Int = 0,
    val yesterdayTotal: Int = 0,
    val dailyAverageLast7Days: Int = 0,
    val last24hTotal: Int = 0,
    val avgFeedsPerDayLast7Days: Int = 0,
    val wastedLast7Days: Int = 0,
    val mostRecentFeedTimestamp: Long? = null,
    val dailyTotalsChartData: List<DailyChartData> = emptyList(),
    val consumptionTrendChartData: List<LineChartData> = emptyList(),
    val feedsByDay: Map<LocalDate, List<com.babyfeed.tracker.data.local.MilkFeed>> = emptyMap(),
    val lastDoseGiven: String? = null,
    val nextMedication: String? = null
)

data class DailyChartData(
    val day: String,
    val consumed: Float,
    val wasted: Float
)

data class LineChartData(
    val day: String,
    val amount: Float
)
