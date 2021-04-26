package ui.screens.selectmodule

import Themes.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import framework.Timber
import framework.component.functional.NavigationComponent
import framework.component.functional.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ui.screens.configproject.DataStore
import ui.screens.welcome.ErrorDialog
import ui.screens.welcome.WarningDialog
import java.io.File


class SelectModuleScreenNavigationComponent(
    private val componentContext: ComponentContext,
    private val toMigrationScreen: () -> Unit,
    private val onBackClicked: () -> Unit,
) : NavigationComponent, ComponentContext by componentContext {

    private val selectModuleViewModel by lazy {
        SelectModuleViewModel(
            toMigrationScreen,
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f),
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .border(width = dp2, color = White1, shape = RectangleShape)
                        .background(Secondary)
                        .fillMaxSize(0.92f)
                        .padding(horizontal = dp24)
                ) {

                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .padding(dp20)
                            .fillMaxSize()
                            .scrollable(
                                state = scrollState,
                                orientation = Orientation.Vertical
                            )
                    ) {
                        //TODO how to make scrollable???
                        val projectModules = selectModuleViewModel.projectModule.collectAsState()
                        val isLoaderVisible = remember { mutableStateOf(projectModules.value.isEmpty()) }

                        if (isLoaderVisible.value) {

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
                            isLoaderVisible.value = false
                            LazyColumn(modifier = Modifier.padding(dp16).fillMaxSize()) {
                                val modules = projectModules.value.toList()
                                items(modules) { (moduleName, file) ->
                                    val checkedState = remember {
                                        mutableStateOf(
                                            selectModuleViewModel.selectedModule.contains(moduleName)
                                        )
                                    }
                                    Row {
                                        Checkbox(
                                            checked = checkedState.value,
                                            onCheckedChange = {
                                                checkedState.value = it
                                                Timber.d("SelectModuleUI -> checked $moduleName $it ")
                                                if (checkedState.value) {
                                                    Timber.d("SelectModuleUI -> datastore added $moduleName")
                                                    selectModuleViewModel.selectedModule.put(moduleName, file)
                                                } else {
                                                    Timber.d("SelectModuleUI -> datastore removed $moduleName")
                                                    selectModuleViewModel.selectedModule.remove(moduleName)
                                                }
                                            }
                                        )

                                        Spacer(modifier = Modifier.width(dp8))
                                        Text(text = moduleName, color = White1)
                                    }

                                }

                            }
                        }
                    }

                }

            }


            // Button for navigation
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = dp48),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .height(46.dp)
                        .padding(horizontal = dp8),
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = Yellow
                    ),
                    onClick = { },
                ) {
                    Text("Buy me a coffee?", color = Color.Black)
                }

                Row {

                    OutlinedButton(
                        modifier = Modifier
                            .width(152.dp)
                            .height(46.dp)
                            .padding(horizontal = dp8),
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = Red
                        ),
                        onClick = {
                            Timber.i("SelectModule UI -> back clicked")
                            selectModuleViewModel.onBackClicked()
                        },
                    ) {
                        Text("Back", color = White1)
                    }

                    val isErrorDialogVisible = remember { mutableStateOf(false) }
                    if (isErrorDialogVisible.value) {
                        ErrorDialog(
                            errorMessage = "No module select for migration",
                            onDialogCancel = {
                                Timber.i("Select Module UI -> ErrorDialog | onDialogCancel callback | No module selected")
                                isErrorDialogVisible.value = false
                            },
                            onDialogProceeded = {
                                Timber.i("Select Module UI -> ErrorDialog | onDialogProceeded callback | No module selected")
                                isErrorDialogVisible.value = false
                            }
                        )
                    }

                    val isWarningDialogVisible = remember { mutableStateOf(false) }

                    if (isWarningDialogVisible.value) {
                        WarningDialog(
                            message = """
                                Very Risky Business now!
                                Don't terminate the app until migration complete.
                                If something went wrong, use version-control to migrate your project back!
                                All the best...
                            """.trimIndent(),
                            onDialogCancel = {
                                Timber.i("Select Module UI -> WarningDialog | onDialogProceeded callback")
                                isWarningDialogVisible.value = false
                            },
                            onDialogProceeded = {
                                Timber.i("Select Module UI -> WarningDialog | onDialogProceeded callback")
                                DataStore.selectedPath.putAll(selectModuleViewModel.selectedModule)
                                selectModuleViewModel.toMigrationScreen()
                                isWarningDialogVisible.value = false
                            }
                        )
                    }

                    OutlinedButton(
                        modifier = Modifier
                            .width(152.dp)
                            .height(46.dp)
                            .padding(horizontal = dp8),
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = Green
                        ),
                        onClick = {
                            Timber.i("SelectModule UI -> next clicked")
                            val isModuleSelected = selectModuleViewModel.selectedModule.isNotEmpty()
                            if (isModuleSelected) {
                                isWarningDialogVisible.value = true
                            } else {
                                isErrorDialogVisible.value = true
                            }
                        },
                    ) {
                        Text("Next", color = White1)
                    }
                }
            }

        }
    }
}

class SelectModuleViewModel(
    val toMigrationScreen: () -> Unit,
    val onBackClicked: () -> Unit
) : ViewModel() {


    private val _projectModules = MutableStateFlow<Map<String, File>>(emptyMap())
    val projectModule = _projectModules.asStateFlow()
    val selectedModule = mutableMapOf<String, File>()

    fun scanForModules() {
        viewModelScope.launch {
            Timber.d("SelectModuleViewModel -> scanning project for module")

            val projectPath = DataStore.projectPath
            val projectFile = File(projectPath)
            val mainPathFiles: List<File> = projectFile.walk()
                .asSequence()
                .filter { it.path.endsWith("src/main") }
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

