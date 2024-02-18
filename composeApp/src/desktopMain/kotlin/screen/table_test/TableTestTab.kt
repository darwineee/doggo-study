package screen.table_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Filter9
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import widget.InputText
import java.util.*
import kotlin.random.Random
import kotlin.random.nextUInt

object TableTestTab : Tab {
    private fun readResolve(): Any = TableTestTab

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Filled.Filter9)
            return remember {
                TabOptions(
                    index = 0u,
                    title = "Test",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val model = rememberScreenModel { TableTestSM() }
        TableTestTabContent(model)
    }
}

@Composable
fun TableTestTabContent(
    model: TableTestSM
) {
    var numOfCalc by remember { mutableStateOf("") }
    val listCalc = remember { mutableStateListOf<CalcItem>() }
    var shownResult by remember { mutableStateOf(false) }
    val state by model.state.collectAsState()

    val onGenerateNewTest: () -> Unit = {
        val newList: MutableList<CalcItem> = mutableListOf()
        repeat(numOfCalc.toInt()) {
            val num1 = Random.nextUInt(2u..9u)
            val num2 = Random.nextUInt(2u..9u)
            newList.add(
                CalcItem(
                    id = UUID.randomUUID().toString(),
                    num1 = num1,
                    num2 = num2,
                )
            )
        }
        listCalc.clear()
        listCalc.addAll(newList)
        shownResult = false
        model.startCountTestTime()
    }

    val onShowResult: () -> Unit = {
        if (shownResult.not()) {
            listCalc.replaceAll { it.copy(showResult = true) }
            val numCorrect = listCalc.count { it.isValid }
            model.submitData(
                numCorrect = numCorrect,
                numTotal = listCalc.size,
            )
            shownResult = true
        }
    }

    when (state) {
        is TableTestSM.State.SubmittedData -> model.loadResult()
        is TableTestSM.State.LoadedResult -> {
            val data = state as TableTestSM.State.LoadedResult
            Dialog(
                onDismissRequest = { model.clearState() },
                properties = DialogProperties()
            ) {
                Column(
                    modifier = Modifier
                        .background(color = Color.White)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = buildAnnotatedString {
                            append(
                                if (data.correctRate > 80) "Congratulation, "
                                else "Sorry, "
                            )
                            append("you have passed ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("${data.numCorrect}/${data.numTotal}")
                            }
                            append(" calculations.\nCorrect rate reached ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("${data.correctRate}%.")
                            }
                            append("\nTry harder next time!")
                        }
                    )
                    Button(
                        onClick = { model.clearState() }
                    ) {
                        Text("Close")
                    }
                }
            }
        }

        else -> {}
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Number of calculations")
                Spacer(modifier = Modifier.width(6.dp))
                InputText(
                    value = numOfCalc,
                    onValueChange = { numOfCalc = it.filter { c -> c.isDigit() } }
                )
            }
            Button(
                onClick = onShowResult,
                enabled = shownResult.not() && listCalc.isNotEmpty(),
                content = {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Finish & check")
                }
            )
            Button(
                onClick = onGenerateNewTest,
                enabled = numOfCalc.isNotBlank(),
                content = {
                    Icon(
                        imageVector = Icons.Filled.Autorenew,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Start new")
                }
            )
        }

        Divider(modifier = Modifier.padding(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
        ) {
            items(
                items = listCalc,
                key = { it.id }
            ) {
                CalcItemView(it)
            }
        }
    }
}

@Composable
private fun CalcItemView(
    model: CalcItem
) {
    var inputValue by remember { mutableStateOf(model.inputValue?.toString().orEmpty()) }
    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${model.num1} x ${model.num2} = ",
            fontSize = 16.sp
        )
        InputText(
            value = inputValue,
            onValueChange = {
                if (it.all { c -> c.isDigit() }) {
                    inputValue = it
                    model.inputValue = it.toUIntOrNull()
                }
            },
            readOnly = model.showResult
        )
        if (model.showResult) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${model.result}",
                fontSize = 16.sp,
                color = if (model.isValid) Color.Green else Color.Red
            )
        }
    }
}