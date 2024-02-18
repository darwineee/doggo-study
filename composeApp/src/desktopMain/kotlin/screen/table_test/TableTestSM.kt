package screen.table_test

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import screen.table_test.TableTestSM.State
import java.time.Instant

class TableTestSM : StateScreenModel<State>(State.Init) {

    private var startTime: Long = 0L

    sealed interface State {
        data object Init : State
        data object SubmittedData : State
        data class LoadedResult(
            val numCorrect: Int,
            val numTotal: Int,
            val correctRate: Float
        ) : State
    }

    fun submitData(
        numCorrect: Int,
        numTotal: Int,
    ) = screenModelScope.launch(Dispatchers.IO) {
        val consumedTime = (System.currentTimeMillis() - startTime) / 1000f
        startTime = 0L
        database.databaseQueries.insert(
            submitAt = Instant.now().epochSecond,
            numCorrect = numCorrect,
            numTotal = numTotal,
            consumedTime = consumedTime,
            correctRate = (numCorrect.toFloat() / numTotal) * 100,
            avgSpeed = (consumedTime / numTotal)
        )
    }

    fun loadResult() = screenModelScope.launch(Dispatchers.IO) {
        val data = database.databaseQueries.getAll().executeAsOneOrNull() ?: return@launch
        mutableState.value = State.LoadedResult(
            numCorrect = data.numCorrect,
            numTotal = data.numTotal,
            correctRate = data.correctRate
        )
    }

    fun clearState() {
        mutableState.value = State.Init
    }

    fun startCountTestTime() {
        startTime = System.currentTimeMillis()
    }
}