package ui.screens.welcome

import Themes.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import framework.component.functional.NavigationComponent
import framework.component.functional.ViewModel
import ui.navigation.AppNavigationController

class WelcomeScreenNavigationComponent(
    componentContext: ComponentContext,
    private val navigator: AppNavigationController
) : NavigationComponent, ComponentContext by componentContext {

    private val welcomeViewModel by lazy { WelcomeViewModel() }

    @Composable
    override fun render() {
        val scope = rememberCoroutineScope()

        LaunchedEffect(welcomeViewModel) {
            welcomeViewModel.init(scope)
            welcomeViewModel.syncData()
        }

        WelcomeScreenUI(
            welcomeViewModel = welcomeViewModel,
            navigator = navigator
        )
    }
}

@Composable
fun WelcomeScreenUI(
    welcomeViewModel: WelcomeViewModel,
    navigator: AppNavigationController
) {

    val isSyncFinished = welcomeViewModel.isSyncFinished.collectAsState()
    // todo create UI
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

class WelcomeViewModel() : ViewModel() {
    override fun syncData() {
        _isSyncFinished.value = true
    }
}

