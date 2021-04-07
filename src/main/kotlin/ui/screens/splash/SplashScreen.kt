package ui.screens.splash

import Themes.Primary
import Themes.dp120
import Themes.dp16
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.imageResource
import com.arkivanov.decompose.ComponentContext
import framework.component.functional.NavigationComponent
import framework.component.functional.ViewModel
import ui.navigation.AppNavigationController

class SplashScreenNavigationComponent(
    componentContext: ComponentContext,
    private val navigator: AppNavigationController
) : NavigationComponent, ComponentContext by componentContext {

    private val splashViewModel by lazy { SplashViewModel() }

    @Composable
    override fun render() {
        val scope = rememberCoroutineScope()
        LaunchedEffect(splashViewModel) {
            splashViewModel.init(scope)
            splashViewModel.syncData()
        }

        SplashScreenUI(
            splashViewModel = splashViewModel,
            navigator = navigator
        )
    }
}

@Composable
fun SplashScreenUI(
    splashViewModel: SplashViewModel,
    navigator: AppNavigationController
) {

    val isSyncFinished = splashViewModel.isSyncFinished.collectAsState()
    val shouldUpdate = splashViewModel.shouldUpdate.collectAsState()

//    if (shouldUpdate.value) {
//        navigator.toUpdateScreen()
//        return
//    }

    if (isSyncFinished.value) {
        navigator.toWelcomeScreen()
        return
    }

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
        }
    }
}

class SplashViewModel() : ViewModel() {
    override fun syncData() {
        _isSyncFinished.value = true
    }
}

