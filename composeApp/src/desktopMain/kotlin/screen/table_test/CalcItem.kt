package screen.table_test

data class CalcItem(
    val id: String,
    val num1: UInt,
    val num2: UInt,
    val showResult: Boolean = false,
    var inputValue: UInt? = null
) {
    val result: UInt get() = num1 * num2
    val isValid: Boolean get() = inputValue == result
}