import Themes.*
import androidx.compose.desktop.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
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
    size = IntSize(800, 450),
    resizable = false,
    centered = true,
) {
    ViewBinderTheme {
        WelcomeScreen()
    }
}

@Composable
fun WelcomeScreen() {
    Surface(
        color = Primary,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            Modifier.fillMaxSize(),
        ) {
            Image(
                bitmap = imageResource("images/logo2.png"),
                contentDescription = "logo",
                modifier = Modifier.fillMaxWidth(0.4f).height(dp120).padding(dp16)
            )

            Box(
                modifier = Modifier
                    .background(Secondary)
                    .fillMaxWidth(0.8f)
                    .padding(dp24)
                    .fillMaxHeight(0.8f)
                    .padding(dp24)
            ) { }

            Row {
                OutlinedButton(
                    onClick = { },
                ) {
                    Text("Buy me a coffee?")
                }

                OutlinedButton(
                    onClick = { },
                ) {
                    Text("finish")
                }

                OutlinedButton(
                    onClick = { },
                ) {
                    Text("next")
                }
            }

        }
    }

}