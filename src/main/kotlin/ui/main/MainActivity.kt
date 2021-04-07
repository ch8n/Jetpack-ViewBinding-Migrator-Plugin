package ui.main

import Themes.*
import androidx.compose.desktop.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import framework.Activity

class MainActivity : Activity() {
    override fun onCreate() {
        super.onCreate()
        setContentView()
    }

    private fun setContentView() = Window(
        title = "ViewBinder Wizard | AndroidBites",
        size = IntSize(1024, 600),
        resizable = false,
        centered = true,
    ) {
        ViewBinderTheme {
            WelcomeScreen()
        }
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
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f),
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .border(width = dp2, color = White1, shape = RectangleShape)
                        .background(Secondary)
                        .fillMaxSize(0.92f)
                        .padding(horizontal = dp24)
                ) {


                }

            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = dp48),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    modifier = Modifier.height(46.dp).padding(horizontal = dp8),
                    onClick = { },
                ) {
                    Text("Buy me a coffee?")
                }

                Row() {

                    OutlinedButton(
                        modifier = Modifier.width(152.dp).height(46.dp).padding(horizontal = dp8),
                        onClick = { },
                    ) {
                        Text("Exit")
                    }

                    OutlinedButton(
                        modifier = Modifier.width(152.dp).height(46.dp).padding(horizontal = dp8),
                        onClick = { },
                    ) {
                        Text("next")
                    }
                }
            }

        }
    }

}