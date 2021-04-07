package ui.screens.splash

import Themes.Primary
import Themes.dp120
import Themes.dp16
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.imageResource
import com.arkivanov.decompose.ComponentContext
import framework.component.functional.NavigationComponent

class SplashScreenNavigationComponent(
    private val componentContext: ComponentContext,
) : NavigationComponent, ComponentContext by componentContext {

    @Composable
    override fun render() {
        SplashScreenUI()
    }
}




@Composable
fun SplashScreenUI() {
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


