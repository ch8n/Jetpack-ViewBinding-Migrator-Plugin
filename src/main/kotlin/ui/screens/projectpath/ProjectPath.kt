package ui.screens.projectpath

import Themes.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import kotlinx.coroutines.launch
import ui.screens.welcome.ErrorDialog
import java.io.File

object DataStore {
    var projectPath = "/Users/chetangupta/StudioProjects/ColorChetan"
    var errorMessage = ""
}


class ProjectPathScreenNavigationComponent(
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

        ProjectPathScreenUI(projectPathViewModel)
    }
}

@Composable
fun ProjectPathScreenUI(projectPathViewModel: ProjectPathViewModel) {

    // todo create UI
    Surface(
        color = Primary,
        modifier = Modifier.fillMaxSize()
    ) {

        val isErrorDialogVisible = remember { mutableStateOf(false) }
        val isLoaderVisible = remember { mutableStateOf(false) }

        if (isErrorDialogVisible.value) {
            ErrorDialog(
                errorMessage = DataStore.errorMessage,
                onDialogCancel = {
                    DataStore.errorMessage = ""
                    isErrorDialogVisible.value = false
                },
                onDialogProceeded = {
                    DataStore.errorMessage = ""
                    isErrorDialogVisible.value = false
                }
            )
        }



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
                        val textState =
                            remember { mutableStateOf(TextFieldValue(DataStore.projectPath)) }
                        TextField(
                            value = textState.value,
                            onValueChange = {
                                DataStore.projectPath = it.text
                                textState.value = it
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Text("Enter Project Path...", color = White1)

                        Spacer(modifier = Modifier.height(dp120))

                        if (isLoaderVisible.value) {
                            Box(modifier = Modifier.fillMaxWidth()) {

                                Column(modifier = Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally)  {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(28.dp),
                                        color = Green
                                    )
                                    Text("Validating Project...", color = White1)
                                }

                            }
                        }

                    }

                }

            }

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
                            Timber.i("ProjectPath UI -> back clicked")
                            projectPathViewModel.onBackPress()
                        },
                    ) {
                        Text("Back", color = White1)
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
                            Timber.i("ProjectPath UI -> next clicked")
                            try {
                                isLoaderVisible.value = true
                                projectPathViewModel.validatePath(DataStore.projectPath) {
                                    isLoaderVisible.value = false
                                    projectPathViewModel.toSelectModulesScreen()
                                }
                            } catch (e: Exception) {
                                DataStore.errorMessage = e.message ?: "Something went wrong"
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

class ProjectPathViewModel(
    val toSelectModulesScreen: () -> Unit,
    val onBackPress: () -> Unit
) : ViewModel() {

    fun validatePath(projectPath: String, onSuccess: () -> Unit) {
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

            delay(1000)
            onSuccess.invoke()
        }
    }


}

