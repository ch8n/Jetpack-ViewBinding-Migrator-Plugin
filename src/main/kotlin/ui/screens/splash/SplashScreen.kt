package ui.screens.splash

import Themes.Green
import Themes.Primary
import Themes.dp12
import Themes.dp400
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import framework.Timber
import framework.component.functional.NavigationComponent
import framework.component.functional.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashScreenNavigationComponent(
    private val componentContext: ComponentContext,
    private val toWelcomeScreen: () -> Unit
) : NavigationComponent, ComponentContext by componentContext {

    private val splashViewModel by lazy { SplashViewModel() }

    @Composable
    override fun render() {

        val scope = rememberCoroutineScope()
        LaunchedEffect(splashViewModel) {
            splashViewModel.init(scope)
            splashViewModel.startTimer(1000) {
                toWelcomeScreen.invoke()
            }
        }

        SplashScreenUI()
    }
}


@Composable
fun SplashScreenUI() {
    Surface(
        color = Primary,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                bitmap = imageResource("images/logo2.png"),
                contentDescription = "logo",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.width(dp400)
            )
            Spacer(Modifier.padding(dp12))
            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = Green
            )
        }
    }
}

class SplashViewModel : ViewModel() {

    fun startTimer(millis: Long, onComplete: () -> Unit) {
        Timber.i("Splash VM -> startTimer")
        viewModelScope.launch {
            delay(millis)
            onComplete.invoke()
            Timber.i("Splash VM -> Timer completed")
        }
    }

}


