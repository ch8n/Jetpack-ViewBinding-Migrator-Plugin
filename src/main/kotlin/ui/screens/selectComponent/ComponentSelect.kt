package ui.screens.selectComponent

import Themes.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import framework.Timber
import framework.component.functional.NavigationComponent
import framework.component.functional.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import ui.component.AppScaffold
import ui.component.FooterScaffold
import ui.data.*
import ui.screens.welcome.ErrorDialog
import ui.screens.welcome.WarningDialog


fun main() {
    Preview {
        val testVM = ComponentSelectViewModel({}, {})
        ComponentSelectScreenUI(testVM)
    }
}


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

        val tabs = listOf<Component>(
            Component.Activities,
            Component.Fragments,
            Component.Adapter,
            Component.CustomViews
        )

        var selectedTabState by remember { mutableStateOf(tabs.get(0)) }
        val warningState by componentSelectViewModel.warningState.collectAsState()
        val errorState by componentSelectViewModel.errorState.collectAsState()
        val (errorVisible, errorMessage) = errorState

        if (errorVisible) {
            ErrorDialog(
                errorMessage = errorMessage,
                onDialogCancel = {
                    componentSelectViewModel.errorState.value = errorState.copy(isVisible = false)
                },
                onDialogProceeded = {
                    componentSelectViewModel.errorState.value = errorState.copy(isVisible = false)
                }
            )
        }


        // todo remove
        if (warningState) {
            WarningDialog(
                message = """
                                Very Risky Business now!
                                Don't terminate the app until migration complete.
                                If something went wrong, use version-control to migrate your project back!
                                All the best...
                            """.trimIndent(),
                onDialogCancel = {
                    Timber.i("Select Module UI -> WarningDialog | onDialogProceeded callback")
                    componentSelectViewModel.warningState.value = false
                },
                onDialogProceeded = {
                    Timber.i("Select Module UI -> WarningDialog | onDialogProceeded callback")
                    val config = componentSelectViewModel.selectedComponentConfig
                    AppDataStore.migrateComponent.putAll(config)
                    componentSelectViewModel.toMigrateScreen()
                    componentSelectViewModel.warningState.value = false
                }
            )
        }


        Column {

            AppScaffold(scrollingEnabled = false) {

                Text(
                    text = "Select Migration Components",
                    style = MaterialTheme.typography.h1,
                    color = White1
                )

                Column {

                    TabRow(
                        selectedTabIndex = selectedTabState.ordinal,
                        backgroundColor = Gray,
                        contentColor = MaterialTheme.colors.primary,
                        modifier = Modifier.fillMaxWidth().padding(top = dp8)
                    ) {
                        // Tabs
                        tabs.forEach { componentTabs ->
                            Tab(
                                selected = componentTabs == selectedTabState,
                                onClick = { selectedTabState = componentTabs },
                                text = { Text(text = componentTabs.name) }
                            )
                        }
                    }

                    Surface(color = White1, modifier = Modifier.fillMaxSize().border(2.dp, Primary)) {
                        when (selectedTabState) {
                            Component.Activities -> activityConfigTab(componentSelectViewModel)
                            Component.Fragments -> workInProgress()
                            Component.Adapter -> workInProgress()
                            Component.CustomViews -> workInProgress()
                        }
                    }
                }

            }

            FooterScaffold(
                onBuyCoffeeClicked = {},
                onBackClicked = {
                    Timber.i("ComponentSelectScreen UI -> back clicked")
                    componentSelectViewModel.onBackClicked()
                },
                onNextClicked = {
                    val config = componentSelectViewModel.selectedComponentConfig
                    if (config.isEmpty()){
                        componentSelectViewModel.errorState.value = errorState.copy(
                            isVisible = true, message = "Please select a component to migrate"
                        )
                    }else{
                        componentSelectViewModel.warningState.value = true
                    }
                    Timber.i("ComponentSelectScreen UI -> next clicked")
                }
            )
        }
    }


}

@Composable
fun workInProgress() {
    Box(contentAlignment = Alignment.Center) {
        Text(
            text = "Work in Progress",
            style = MaterialTheme.typography.h1,
        )
    }

}

@Composable
fun activityConfigTab(componentSelectViewModel: ComponentSelectViewModel) {

    val layoutIdformats = listOf(
        LayoutIdsFormat.UnderScoreCase,
        LayoutIdsFormat.CamelCase,
        LayoutIdsFormat.KebabCase
    )

    var selectedLayoutIdFormat by remember { mutableStateOf(layoutIdformats.get(0)) }
    var baseActivityName by remember { mutableStateOf("AppCompatActivity") }
    var selectedComponentState by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {

            Column(modifier = Modifier.fillMaxSize()) {

                Column(modifier = Modifier.fillMaxSize().padding(dp16)) {
                    Text(
                        text = "Select Layout Id format",
                        style = MaterialTheme.typography.body1,
                    )

                    Row(modifier = Modifier.padding(top = dp16)) {
                        layoutIdformats.onEach { idFormat ->
                            Row(modifier = Modifier.padding(end = dp16)) {
                                RadioButton(
                                    selected = (selectedLayoutIdFormat == idFormat),
                                    onClick = { selectedLayoutIdFormat = idFormat },
                                    colors = RadioButtonDefaults.colors(selectedColor = Green)
                                )
                                Text(
                                    text = "${idFormat.name}\nex: ${idFormat.example}",
                                    style = MaterialTheme.typography.body1,
                                    modifier = Modifier.padding(start = dp16)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(dp16))

                    Text(text = "Base Activity Name")

                    TextField(
                        value = baseActivityName,
                        modifier = Modifier.padding(top = dp8),
                        onValueChange = { baseActivityName = it },
                        maxLines = 1
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
            Row(modifier = Modifier.padding(dp16)) {
                Checkbox(
                    checked = selectedComponentState,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            val config = ComponentConfig.ActivityConfig(selectedLayoutIdFormat, baseActivityName)
                            componentSelectViewModel.addConfig(config)
                        } else {
                            componentSelectViewModel.removeConfig(Component.Activities)
                        }
                        selectedComponentState = isChecked
                    }
                )
                Spacer(Modifier.width(dp16))
                Text(
                    text = "Activities",
                    style = MaterialTheme.typography.body1,
                )
                Spacer(Modifier.width(dp16))
            }
        }
    }


}


class ComponentSelectViewModel(
    val onBackClicked: () -> Unit,
    val toMigrateScreen: () -> Unit
) : ViewModel() {

    val errorState = MutableStateFlow(Error(isVisible = false, message = ""))
    val warningState = MutableStateFlow(false)

    private val _selectedComponentConfig = mutableMapOf<Component, ComponentConfig>()
    val selectedComponentConfig
        get() = _selectedComponentConfig.toMap()

    fun addConfig(config: ComponentConfig) = when (config) {
        is ComponentConfig.ActivityConfig -> _selectedComponentConfig.put(Component.Activities, config)
        is ComponentConfig.CustomViewConfig -> _selectedComponentConfig.put(Component.CustomViews, config)
        is ComponentConfig.FragmentConfig -> _selectedComponentConfig.put(Component.Fragments, config)
        is ComponentConfig.RecyclerAdapterConfig -> _selectedComponentConfig.put(Component.Adapter, config)
    }

    fun removeConfig(component: Component) = _selectedComponentConfig.remove(component)

}