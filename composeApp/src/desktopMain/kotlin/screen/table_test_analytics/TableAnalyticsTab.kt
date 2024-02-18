package screen.table_test_analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.jetbrains.letsPlot.geom.geomBar
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.intern.StatKind
import org.jetbrains.letsPlot.intern.layer.StatOptions
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.pos.positionDodge
import org.jetbrains.letsPlot.skia.compose.PlotPanel

object TableAnalyticsTab : Tab {
    private fun readResolve(): Any = TableAnalyticsTab

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Filled.Insights)
            return remember {
                TabOptions(
                    index = 1u,
                    title = "Analytics",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val model = rememberScreenModel { TableAnalyticsSM() }
        val state by model.state.collectAsState()
        LaunchedEffect(Unit) {
            model.loadGraphData()
        }
        TableAnalyticsTabContent(state)
    }
}

@Composable
fun TableAnalyticsTabContent(
    state: TableAnalyticsSM.State
) {
    when (state) {
        is TableAnalyticsSM.State.Init -> {}
        is TableAnalyticsSM.State.Loading -> TableAnalyticsTabContentLoading()
        is TableAnalyticsSM.State.LoadedData -> TableAnalyticsTabContentGraph(state)
        is TableAnalyticsSM.State.LoadedNoData -> TableAnalyticsTabContentLoadedEmpty()
    }
}

@Composable
fun TableAnalyticsTabContentLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.fillMaxHeight(0.3f).aspectRatio(1f)
        )
    }
}

@Composable
fun TableAnalyticsTabContentLoadedEmpty() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text("No data available")
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TableAnalyticsTabContentGraph(
    data: TableAnalyticsSM.State.LoadedData
) {
    Column {
        PlotPanel(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f),
            figure = letsPlot(
                data = data.barData
            ) {
                x = "Lịch sử"
            } + geomBar(
                stat = StatOptions(StatKind.IDENTITY),
                position = positionDodge(0.3),
            ) {
                y = "Giá trị"
                fill = "Chỉ số"
            },
            computationMessagesHandler = {}
        )
        PlotPanel(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f),
            figure = letsPlot(
                data = data.lineData
            ) {
                x = "Lịch sử"
            } + geomLine {
                y = "Giá trị"
                color = "Chỉ số"
            } + geomPoint {
                y = "Giá trị"
                color = "Chỉ số"
            },
            computationMessagesHandler = {}
        )
    }
}