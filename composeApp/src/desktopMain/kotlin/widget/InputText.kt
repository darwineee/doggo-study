package widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun InputText(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    readOnly: Boolean = false,
) = BasicTextField(
    modifier = Modifier
        .background(
            color = Color(0xffccdbbd),
            shape = RoundedCornerShape(4.dp)
        )
        .padding(vertical = 8.dp, horizontal = 6.dp),
    value = value,
    onValueChange = onValueChange,
    enabled = enabled,
    readOnly = readOnly,
)