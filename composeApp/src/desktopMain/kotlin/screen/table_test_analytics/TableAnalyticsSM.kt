package screen.table_test_analytics

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import screen.table_test_analytics.TableAnalyticsSM.State
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TableAnalyticsSM : StateScreenModel<State>(State.Init) {
    sealed interface State {
        data object Init : State
        data object Loading : State
        data object LoadedNoData : State
        data class LoadedData(
            val barData: Map<String, Any>,
            val lineData: Map<String, Any>,
        ) : State
    }

    fun loadGraphData() = screenModelScope.launch(Dispatchers.IO) {
        mutableState.value = State.Loading
        val raw = database.databaseQueries.getAll().executeAsList()
        if (raw.isEmpty()) {
            mutableState.value = State.LoadedNoData
            return@launch
        }

        val barXAxis = mutableListOf<String>()
        val lineXAxis = mutableListOf<String>()
        val barGroup = mutableListOf<String>()
        val lineGroup = mutableListOf<String>()
        val lineData = mutableListOf<Float>()
        val barData = buildList {
            raw.forEach {
                val lcTime = it.submitAt.toFormattedTime()
                barXAxis.add(lcTime)
                barXAxis.add(lcTime)
                barXAxis.add(lcTime)
                barGroup.add("Làm đúng")
                barGroup.add("Tổng cộng")
                barGroup.add("Thời gian")

                this.add(it.numCorrect)
                this.add(it.numTotal)
                this.add(it.consumedTime)

                lineXAxis.add(lcTime)
                lineXAxis.add(lcTime)
                lineGroup.add("Độ chính xác")
                lineGroup.add("Tốc độ")
                lineData.add(it.correctRate / 100f)
                lineData.add(it.avgSpeed)
            }
        }

        val barGraphData = mapOf(
            "Lịch sử" to barXAxis,
            "Chỉ số" to barGroup,
            "Giá trị" to barData,
        )

        val lineGraphData = mapOf(
            "Lịch sử" to lineXAxis,
            "Chỉ số" to lineGroup,
            "Giá trị" to lineData,
        )

        mutableState.value = State.LoadedData(
            barData = barGraphData,
            lineData = lineGraphData
        )
    }

    private fun Long.toFormattedTime(): String {
        val zoneId = ZoneId.systemDefault()
        val df = DateTimeFormatter.ofPattern("HH:mm, dd-MM-yyyy")
        return Instant.ofEpochSecond(this).atZone(zoneId).toLocalDateTime().format(df)
    }
}