package ui.screens.selectComponent

import Themes.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import framework.Timber
import framework.component.functional.NavigationComponent
import framework.component.functional.ViewModel
import ui.component.AppScaffold
import ui.component.FooterScaffold
import ui.data.Preview


fun main() {
    Preview {
        val testVM = ComponentSelectViewModel({}, {})
        ComponentSelectScreenUI(testVM)
    }
}

enum class ComponentTabs {
    Activities,
    Fragments,
    Adapter,
    CustomViews
}

enum class LayoutIdsFormat(val example:String) {
    CamelCase(example = "textView"),
    UnderScoreCase(example = "text_view | text_View"),
    KebabCase(example = "text-view | text-View");
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

        val tabs = listOf<ComponentTabs>(
            ComponentTabs.Activities,
            ComponentTabs.Fragments,
            ComponentTabs.Adapter,
            ComponentTabs.CustomViews
        )

        var selectedTabState by remember { mutableStateOf(tabs.get(0)) }


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
                            ComponentTabs.Activities -> activityConfigTab(componentSelectViewModel)
                            ComponentTabs.Fragments -> workInProgress()
                            ComponentTabs.Adapter -> workInProgress()
                            ComponentTabs.CustomViews -> workInProgress()
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

    var selectedId by remember { mutableStateOf(layoutIdformats.get(0)) }
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
                                    selected = (selectedId == idFormat),
                                    onClick = { selectedId = idFormat },
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
                    onCheckedChange = { selectedComponentState = it }
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

}