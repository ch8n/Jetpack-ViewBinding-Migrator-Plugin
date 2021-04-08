package ui.screens.splash

import Themes.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
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


