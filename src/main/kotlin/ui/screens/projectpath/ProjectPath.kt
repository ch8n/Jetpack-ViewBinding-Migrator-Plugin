package ui.screens.projectpath

import Themes.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
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
                        Text("PROJECT PATH...")
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
                            projectPathViewModel.toSelectModulesScreen()
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
) : ViewModel()

