package ui.navigation


import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import com.arkivanov.decompose.pop
import com.arkivanov.decompose.push
import com.arkivanov.decompose.router
import com.arkivanov.decompose.statekeeper.Parcelable
import framework.Timber
import framework.component.functional.NavigationComponent
import ui.screens.migration.MigrationScreenNavigationComponent
import ui.screens.projectpath.ConfigureProjectScreenNavigationComponent
import ui.screens.selectmodule.SelectModuleScreenNavigationComponent
import ui.screens.splash.SplashScreenNavigationComponent
import ui.screens.welcome.WelcomeScreenNavigationComponent


class NavHostNavigationComponent(
    componentContext: ComponentContext
) : NavigationComponent,
    ComponentContext by componentContext {

    sealed class Screens : Parcelable {
        object Splash : Screens()
        object Welcome : Screens()
        object ConfigureProject : Screens()
        object SelectModules : Screens()
        object Migration : Screens()
        object Update : Screens()
    }

    private val router = router<Screens, NavigationComponent>(
        childFactory = ::createNavigationScreenComponent,
        initialConfiguration = Screens.Splash,
    )

    fun toSplashScreen() {
        Timber.i("navigator -> toSplashScreen")
        router.push(NavHostNavigationComponent.Screens.Splash)
    }

    fun toWelcomeScreen() {
        Timber.i("navigator -> toWelcomeScreen")
        router.push(NavHostNavigationComponent.Screens.Welcome)
    }

    fun toProjectPathScreen() {
        Timber.i("navigator -> toProjectPathScreen")
        router.push(NavHostNavigationComponent.Screens.ConfigureProject)
    }

    fun toSelectModulesScreen() {
        Timber.i("navigator -> toSelectModulesScreen")
        router.push(NavHostNavigationComponent.Screens.SelectModules)
    }

    fun toMigrationScreen() {
        Timber.i("navigator -> toMigrationScreen")
        router.push(NavHostNavigationComponent.Screens.Migration)
    }

    fun toUpdateScreen() {
        Timber.i("navigator -> toUpdateScreen")
        router.push(NavHostNavigationComponent.Screens.Update)
    }

    fun onBackClicked() {
        Timber.i("navigator -> onBackClicked")
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
                ::toProjectPathScreen
            )
            is Screens.ConfigureProject -> ConfigureProjectScreenNavigationComponent(
                componentContext = componentContext,
                ::toSelectModulesScreen,
                ::onBackClicked
            )
            is Screens.SelectModules -> SelectModuleScreenNavigationComponent(
                componentContext = componentContext,
                ::toMigrationScreen,
                ::onBackClicked
            )
            is Screens.Migration -> MigrationScreenNavigationComponent(
                componentContext = componentContext,
                ::onBackClicked
            )
            is Screens.Update -> TODO()
        }
    }


    @Composable
    override fun render() {
        Timber.i("render root router...")
        Children(routerState = router.state) { child ->
            child.instance.render()
        }
    }

}

