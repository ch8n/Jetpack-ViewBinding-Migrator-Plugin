import androidx.compose.desktop.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

fun main() = Window(
    title = "ViewBinder Wizard | AndroidBites",
    size = IntSize(800, 720),
    resizable = false,
    centered = true,
) {
    MaterialTheme {
        WelcomeScreen()
    }
}

@Composable
fun WelcomeScreen() {

    Image(
        bitmap = imageResource("images/logo2.png"),
        contentDescription = "logo",
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .height(120.dp)
            .padding(16.dp)
    )

    Column(Modifier.fillMaxSize()) {

        val dialogState = remember { mutableStateOf(false) }

        Button(onClick = { dialogState.value = true }) {
            Text(text = "Open dialog")
        }

        if (dialogState.value) {
            Dialog(
                onDismissRequest = { dialogState.value = false }
            ) {
                // Dialog's content
            }
        }
    }
}