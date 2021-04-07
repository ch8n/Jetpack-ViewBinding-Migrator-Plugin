package ui.screens.splash

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import framework.component.functional.NavigationComponent
import framework.component.functional.ViewModel
import ui.navigation.AppNavigationController

class SplashScreenNavigationComponent(
    componentContext: ComponentContext,
    private val navigator: AppNavigationController
) : NavigationComponent, ComponentContext by componentContext {

    private val splashViewModel = SplashViewModel()

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

fun SplashScreenUI(
    splashViewModel: SplashViewModel,
    navigator: AppNavigationController
) {

    val isSyncFinished = splashViewModel.isSyncFinished.collectAsState()
    val shouldUpdate = splashViewModel.shouldUpdate.collectAsState()

    if (shouldUpdate.value) {
        navigator.toUpdateScreen()
        return
    }

    if (isSyncFinished.value) {
        navigator.onSplashFinished()
        return
    }

    // todo create UI

}

class SplashViewModel() : ViewModel() {
    override fun syncData() {

    }
}

