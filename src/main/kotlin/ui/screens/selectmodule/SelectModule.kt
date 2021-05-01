package ui.screens.selectmodule

import Themes.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import framework.Timber
import framework.component.functional.NavigationComponent
import framework.component.functional.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ui.component.AppScaffold
import ui.component.FooterScaffold
import ui.data.AppDataStore
import ui.data.Error
import ui.data.ProjectSetting
import ui.screens.welcome.ErrorDialog
import java.io.File


class SelectModuleScreenNavigationComponent(
    private val componentContext: ComponentContext,
    private val toSelectComponentScreen: () -> Unit,
    private val onBackClicked: () -> Unit,
) : NavigationComponent, ComponentContext by componentContext {

    private val selectModuleViewModel by lazy {
        SelectModuleViewModel(
            toSelectComponentScreen,
            onBackClicked
        )
    }

    @Composable
    override fun render() {

        val scope = rememberCoroutineScope()
        LaunchedEffect(selectModuleViewModel) {
            selectModuleViewModel.init(scope)
            selectModuleViewModel.scanForModules()
        }

        SelectModuleScreenUI(selectModuleViewModel)
    }
}

@Composable
fun SelectModuleScreenUI(selectModuleViewModel: SelectModuleViewModel) {

    Surface(
        color = Primary,
        modifier = Modifier.fillMaxSize()
    ) {

        val projectConfig = selectModuleViewModel.projectConfig
        val errorState by selectModuleViewModel.errorState.collectAsState()

        val loadingState by selectModuleViewModel.loadingState.collectAsState()
        val (errorVisible, errorMessage) = errorState

        val projectModules = selectModuleViewModel.projectModule.collectAsState()

        if (errorVisible) {
            ErrorDialog(
                errorMessage = errorMessage,
                onDialogCancel = {
                    Timber.i("Select Module UI -> ErrorDialog | onDialogCancel callback | No module selected")
                    selectModuleViewModel.errorState.value = errorState.copy(isVisible = false)
                },
                onDialogProceeded = {
                    Timber.i("Select Module UI -> ErrorDialog | onDialogProceeded callback | No module selected")
                    selectModuleViewModel.errorState.value = errorState.copy(isVisible = false)
                }
            )
        }


        Column {

            AppScaffold(scrollingEnabled = false) {

                Text(
                    text ="Select module",
                    style = MaterialTheme.typography.h1,
                    color = White1
                )

                if (loadingState) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp),
                                color = Green
                            )
                            Text("Loading Modules...", color = White1)
                        }
                    }
                }

                if (projectModules.value.isNotEmpty()) {
                    Timber.d("SelectModuleUI -> stop loading")
                    selectModuleViewModel.loadingState.value = false
                    LazyColumn(modifier = Modifier.padding(dp16).fillMaxSize()) {
                        val modules = projectModules.value.toList()
                        items(modules) { (moduleName, file) ->

                            var selectedState by remember {
                                mutableStateOf(
                                    selectModuleViewModel.selectedModule.contains(moduleName)
                                )
                            }

                            Row {

                                when (projectConfig) {
                                    is ProjectSetting.MultiModuleProject -> {
                                        Checkbox(
                                            checked = selectedState,
                                            onCheckedChange = {
                                                selectedState = it
                                                Timber.d("SelectModuleUI -> checked $moduleName $it ")
                                                if (selectedState) {
                                                    Timber.d("SelectModuleUI -> datastore added $moduleName")
                                                    selectModuleViewModel.selectedModule.put(moduleName, file)
                                                } else {
                                                    Timber.d("SelectModuleUI -> datastore removed $moduleName")
                                                    selectModuleViewModel.selectedModule.remove(moduleName)
                                                }
                                            }
                                        )
                                    }
                                    is ProjectSetting.SingleModuleProject -> {
                                        RadioButton(
                                            selected = selectedState,
                                            onClick = {
                                                selectModuleViewModel.selectedModule.put(moduleName, file)
                                                selectedState = selectModuleViewModel.selectedModule.contains(moduleName)
                                                Timber.d("SelectModuleUI -> selected $moduleName ")
                                            },
                                            colors = RadioButtonDefaults.colors(selectedColor = Green)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(dp8))
                                Text(text = moduleName, color = White1)
                            }

                        }

                    }
                }
            }

            FooterScaffold(
                onBuyCoffeeClicked = {},
                onBackClicked = {
                    Timber.i("SelectModule UI -> back clicked")
                    selectModuleViewModel.onBackClicked()
                },
                onNextClicked = {
                    Timber.i("SelectModule UI -> next clicked")
                    val isModuleSelected = selectModuleViewModel.selectedModule.isNotEmpty()
                    if (isModuleSelected) {
                        AppDataStore.selectedModule.putAll(selectModuleViewModel.selectedModule)
                        selectModuleViewModel.toSelectComponentScreen()
                        //selectModuleViewModel.warningState.value = true
                    } else {
                        selectModuleViewModel.errorState.value = errorState.copy(
                            isVisible = true,
                            message = "Please select module"
                        )
                    }
                }
            )
        }

    }
}

class SelectModuleViewModel(
    val toSelectComponentScreen: () -> Unit,
    val onBackClicked: () -> Unit
) : ViewModel() {

    val projectConfig = AppDataStore.projectConfig

    val errorState = MutableStateFlow(Error(isVisible = false, message = ""))
    val loadingState = MutableStateFlow(true)

    private val _projectModules = MutableStateFlow<Map<String, File>>(emptyMap())
    val projectModule = _projectModules.asStateFlow()

    val selectedModule = mutableMapOf<String, File>()

    fun scanForModules() {
        viewModelScope.launch {
            Timber.d("SelectModuleViewModel -> scanning project for module")
            val projectSetting = projectConfig as ProjectSetting.SingleModuleProject
            val projectPath = projectSetting.projectPath
            val projectFile = File(projectPath)
            val mainPathFiles: List<File> = projectFile.walk()
                .asSequence()
                .filter { it.path.endsWith("src/main") }
                .toList()
                .asSequence()
                .filter {
                    it.walk().asSequence().firstOrNull {
                        it.name.contains("AndroidManifest.xml")
                    } != null
                }
                .toList()
                .also { Timber.e(it.joinToString(separator = "\n")) }

            val moduleNames: Map<String, File> = mainPathFiles.map {
                val key = it.path.split("/").dropLast(2).last()
                val value = it
                return@map key to value
            }.toMap()
            delay(1000)
            Timber.d("SelectModuleViewModel -> found modules $moduleNames")
            _projectModules.value = moduleNames
        }

    }


}

