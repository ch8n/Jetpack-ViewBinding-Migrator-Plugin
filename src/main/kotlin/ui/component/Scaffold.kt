package ui.component

import Themes.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import ui.screens.configproject.DataStore

@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    scrollingEnabled: Boolean = true,
    body: @Composable () -> Unit
) {
    Column(
        modifier.fillMaxWidth().fillMaxHeight(0.85f),
    ) {
        Image(
            bitmap = imageResource("images/logo2.png"),
            contentDescription = "logo",
            modifier = Modifier.fillMaxWidth(0.4f).height(dp120).padding(dp16)
        )

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .border(width = dp2, color = White1, shape = RectangleShape)
                    .background(Secondary)
                    .fillMaxSize(0.92f)
                    .padding(horizontal = dp24)
            ) {

                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .padding(dp20)
                        .fillMaxSize()
                        .apply {
                            if (scrollingEnabled) {
                                verticalScroll(state = scrollState)
                            }
                        }

                ) {
                    body.invoke()
                }

            }

        }

    }
}

@Composable
fun FooterScaffold(
    modifier: Modifier = Modifier,
    onBuyCoffeeClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onNextClicked: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = dp48),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedButton(
            modifier = Modifier.height(46.dp).padding(horizontal = dp8),
            colors = ButtonDefaults.textButtonColors(
                backgroundColor = Yellow
            ),
            onClick = onBuyCoffeeClicked,
        ) {
            Text("Buy me a coffee?", color = Color.Black)
        }

        Row {

            OutlinedButton(
                modifier = Modifier.width(152.dp).height(46.dp).padding(horizontal = dp8),
                colors = ButtonDefaults.textButtonColors(backgroundColor = Red),
                onClick = onBackClicked,
            ) {
                Text("Back", color = White1)
            }

            OutlinedButton(
                modifier = Modifier.width(152.dp).height(46.dp).padding(horizontal = dp8),
                colors = ButtonDefaults.textButtonColors(backgroundColor = Green),
                onClick = onNextClicked,
            ) {
                Text("Next", color = White1)
            }
        }
    }
}

