
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cafe.adriel.voyager.navigator.Navigator
import screen.HomeScreen

fun main() = application {
    Window(
        onCloseRequest = {
            exitApplication()
        },
        title = "DoggoStudy",
        state = rememberWindowState(
            placement = WindowPlacement.Maximized
        )
    ) {
        Navigator(HomeScreen())
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    Navigator(HomeScreen())
}