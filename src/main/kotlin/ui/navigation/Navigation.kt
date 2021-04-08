package ui.navigation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.*
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import com.arkivanov.decompose.statekeeper.Parcelable
import framework.component.functional.NavigationComponent
import ui.screens.splash.SplashScreenNavigationComponent
import ui.screens.welcome.WelcomeScreenNavigationComponent


class NavHostNavigationComponent(
    componentContext: ComponentContext
) : NavigationComponent,
    ComponentContext by componentContext {

    sealed class Screens : Parcelable {
        object Splash : Screens()
        object Welcome : Screens()
        object ProjectPath : Screens()
        object SelectModules : Screens()
        object Migration : Screens()
        object Update : Screens()
    }

    private val router = router<Screens, NavigationComponent>(
        initialConfiguration = Screens.Splash,
        childFactory = ::createNavigationScreenComponent
    )

    private val appNavigator = object : AppNavigationController {

        override fun toSplashScreen() {
            router.push(Screens.Splash)
        }

        override fun toWelcomeScreen() {
            router.push(Screens.Welcome)
        }

        override fun toProjectPathScreen() {
            router.push(Screens.ProjectPath)
        }

        override fun toSelectModulesScreen() {
            router.push(Screens.SelectModules)
        }

        override fun toMigrationScreen() {
            router.push(Screens.Migration)
        }

        override fun toUpdateScreen() {
            router.push(Screens.Update)
        }

        override fun onBackClicked() {
            router.pop()
        }

    }


    private fun createNavigationScreenComponent(
        screens: Screens,
        componentContext: ComponentContext
    ): NavigationComponent {
        return when (screens) {
            is Screens.Splash -> SplashScreenNavigationComponent(
                componentContext = componentContext
            )
            is Screens.Welcome -> WelcomeScreenNavigationComponent(
                componentContext = componentContext
            )
            is Screens.ProjectPath -> TODO()
            is Screens.SelectModules -> TODO()
            is Screens.Migration -> TODO()
            is Screens.Update -> TODO()
        }
    }


    @Composable
    override fun render() {
        Children(routerState = router.state) { child ->
            child.instance.render()
        }
    }

}

interface AppNavigationController {
    fun toSplashScreen()
    fun toWelcomeScreen()
    fun toProjectPathScreen()
    fun toSelectModulesScreen()
    fun toMigrationScreen()
    fun toUpdateScreen()
    fun onBackClicked()
}


