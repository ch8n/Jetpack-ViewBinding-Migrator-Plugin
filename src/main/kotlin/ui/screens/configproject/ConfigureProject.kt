package ui.screens.configproject

import Themes.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import framework.Timber
import framework.component.functional.NavigationComponent
import framework.component.functional.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ui.component.AppScaffold
import ui.component.FooterScaffold
import ui.data.Error
import ui.data.ProjectModuleType
import ui.data.ProjectSetting
import ui.screens.configproject.components.ModuleSelectRadioButton
import ui.screens.welcome.ErrorDialog
import java.io.File

// TODO break this dependency
object DataStore {
    var projectPath = "/Users/chetangupta/StudioProjects/GitTrends"
    var isSingleModuleProject: Boolean = false
    var errorMessage = ""
    val baseActivityName: String
        get() = packageNameBaseActivity.split(".").last()

    var packageNameBaseActivity = "dev.ch8n.gittrends.ui.base.BaseActivity"

    val selectedPath = mutableMapOf<String, File>()
}


class ConfigureProjectScreenNavigationComponent(
    private val componentContext: ComponentContext,
    private val toSelectModulesScreen: () -> Unit,
    private val onBackPress: () -> Unit,
) : NavigationComponent, ComponentContext by componentContext {

    private val projectPathViewModel by lazy {
        ProjectPathViewModel(
            toSelectModulesScreen,
            onBackPress
        )
    }

    @Composable
    override fun render() {

        val scope = rememberCoroutineScope()
        LaunchedEffect(projectPathViewModel) {
            projectPathViewModel.init(scope)
        }
        ConfigProjectScreenUI(projectPathViewModel)
    }
}

@Composable
fun ConfigProjectScreenUI(projectPathViewModel: ProjectPathViewModel) {

    // todo create UI
    Surface(
        color = Primary,
        modifier = Modifier.fillMaxSize()
    ) {

        val moduleSelectOption = listOf(ProjectModuleType.SINGLE, ProjectModuleType.MULTI)
        val errorState by projectPathViewModel.errorState.collectAsState()
        val loadingState by projectPathViewModel.loadingState.collectAsState()
        val (errorVisible, errorMessage) = errorState

        var projectPathState by remember { mutableStateOf(TextFieldValue("")) }
        var projectTypeState by remember { mutableStateOf(ProjectModuleType.NONE) }
        var baseFolderOrModuleName by remember { mutableStateOf(TextFieldValue("")) }


        if (errorVisible) {
            ErrorDialog(
                errorMessage = errorMessage,
                onDialogCancel = {
                    projectPathViewModel.errorState.value = errorState.copy(isVisible = false)
                },
                onDialogProceeded = {
                    projectPathViewModel.errorState.value = errorState.copy(isVisible = false)
                }
            )
        }

        Column {

            AppScaffold {

                Text("Enter Project Path")

                TextField(
                    value = projectPathState,
                    modifier = Modifier.fillMaxWidth().padding(top = dp8),
                    onValueChange = { projectPathState = it }
                )

                Spacer(modifier = Modifier.height(dp16))

                ModuleSelectRadioButton(
                    moduleOptions = moduleSelectOption,
                    onModuleOptionSelected = { selectedModule ->
                        projectTypeState = selectedModule
                    }
                )

                Spacer(modifier = Modifier.height(dp16))

                val baseLabel = when (projectTypeState) {
                    ProjectModuleType.SINGLE, ProjectModuleType.NONE -> "Base Folder Name"
                    ProjectModuleType.MULTI -> "Base Module Name"
                }


                Text(text = baseLabel)

                TextField(
                    value = baseFolderOrModuleName,
                    modifier = Modifier.padding(top = dp8),
                    onValueChange = {
                        baseFolderOrModuleName = it
                    }
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
                            Text("Loading...", color = White1)
                        }
                    }
                }
            }

            FooterScaffold(
                onBuyCoffeeClicked = {},
                onBackClicked = {
                    Timber.i("ProjectPath UI -> back clicked")
                    projectPathViewModel.onBackPress()
                },
                onNextClicked = {
                    Timber.i("ProjectPath UI -> next clicked")
                    try {
                        projectPathViewModel.loadingState.value = true
                        projectPathViewModel.validatePath(DataStore.projectPath) {
                            projectPathViewModel.loadingState.value = false
                            projectPathViewModel.toSelectModulesScreen()
                        }
                    } catch (e: Exception) {
                        projectPathViewModel.errorState.value = errorState.copy(
                            isVisible = true, message = e.localizedMessage
                        )
                    }
                }
            )
        }

    }
}

class ProjectPathViewModel(
    val toSelectModulesScreen: () -> Unit,
    val onBackPress: () -> Unit
) : ViewModel() {

    val errorState = MutableStateFlow(Error(isVisible = false, message = ""))
    val loadingState = MutableStateFlow(false)
    val projectSetting = MutableStateFlow<ProjectSetting>(ProjectSetting.None)

    fun validatePath(projectPath: String, onSuccess: () -> Unit) = runBlocking {
        viewModelScope.launch {
            val projectRoot = File(projectPath)
            val existing = projectRoot.exists()
            if (!existing) {
                throw Exception("invalid path")
            }

            projectRoot.walk()
                .asSequence()
                .firstOrNull {
                    it.name.contains("AndroidManifest.xml")
                } ?: throw Exception("Not an android project")

            delay(2000)
            onSuccess.invoke()
        }
    }


}

