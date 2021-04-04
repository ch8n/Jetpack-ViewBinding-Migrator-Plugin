import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

fun main() = Window(
    title = "Compose for Desktop",
    size = IntSize(800, 720),
    resizable = false
) {
    val count = remember { mutableStateOf(0) }
    MaterialTheme {
        WelcomeScreen()
    }
}

@Composable
fun WelcomeScreen() {
    Column(Modifier.fillMaxSize()) {

    }
}