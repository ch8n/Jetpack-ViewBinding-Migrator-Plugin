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
        childFactory = ::createNavigationScreenComponent,
        initialConfiguration = Screens.Splash,
    )

    fun toSplashScreen() {
        router.push(NavHostNavigationComponent.Screens.Splash)
    }

    fun toWelcomeScreen() {
        router.push(NavHostNavigationComponent.Screens.Welcome)
    }

    fun toProjectPathScreen() {
        router.push(NavHostNavigationComponent.Screens.ProjectPath)
    }

    fun toSelectModulesScreen() {
        router.push(NavHostNavigationComponent.Screens.SelectModules)
    }

    fun toMigrationScreen() {
        router.push(NavHostNavigationComponent.Screens.Migration)
    }

    fun toUpdateScreen() {
        router.push(NavHostNavigationComponent.Screens.Update)
    }

    fun onBackClicked() {
        router.pop()
    }

    private fun createNavigationScreenComponent(
        screens: Screens,
        componentContext: ComponentContext
    ): NavigationComponent {
        return when (screens) {
            is Screens.Splash -> SplashScreenNavigationComponent(
                componentContext = componentContext,
                ::toWelcomeScreen
            )
            is Screens.Welcome -> WelcomeScreenNavigationComponent(
                componentContext = componentContext,
                ::toProjectPathScreen,
                ::toWelcomeScreen
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

