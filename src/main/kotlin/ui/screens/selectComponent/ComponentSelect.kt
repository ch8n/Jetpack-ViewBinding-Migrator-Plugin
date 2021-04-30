package ui.screens.selectComponent

import Themes.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import framework.Timber
import framework.component.functional.NavigationComponent
import framework.component.functional.ViewModel
import ui.component.AppScaffold
import ui.component.FooterScaffold
import ui.data.AppDataStore
import ui.data.ProjectModuleType
import ui.data.ProjectSetting
import ui.screens.configproject.DataStore
import ui.screens.configproject.components.ModuleSelectRadioButton

class SelectComponentScreenNavigationComponent(
    private val componentContext: ComponentContext,
    private val onBackClicked: () -> Unit,
    private val toMigrateScreen: () -> Unit
) : NavigationComponent, ComponentContext by componentContext {

    private val componentSelectViewModel by lazy {
        ComponentSelectViewModel(
            toMigrateScreen,
            onBackClicked
        )
    }

    @Composable
    override fun render() {
        val scope = rememberCoroutineScope()
        LaunchedEffect(componentSelectViewModel) {
            componentSelectViewModel.init(scope)
        }
        ComponentSelectScreenUI(componentSelectViewModel)
    }
}

@Composable
fun ComponentSelectScreenUI(componentSelectViewModel: ComponentSelectViewModel) {

    Surface(
        color = Primary,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {

            AppScaffold(scrollingEnabled = false) {

                Text(
                    text ="ComponentSelectScreenUI",
                    style = MaterialTheme.typography.h1,
                    color = White1
                )

            }

            FooterScaffold(
                onBuyCoffeeClicked = {},
                onBackClicked = {
                    Timber.i("ComponentSelectScreen UI -> back clicked")
                    componentSelectViewModel.onBackClicked()
                },
                onNextClicked = {
                    Timber.i("ComponentSelectScreen UI -> next clicked")
                }
            )
        }
    }



}


class ComponentSelectViewModel(
    val onBackClicked: () -> Unit,
    val toMigrateScreen: () -> Unit
) : ViewModel() {

}