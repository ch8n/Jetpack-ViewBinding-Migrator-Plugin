package ui.screens.migration

import Themes.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
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
import ui.screens.projectpath.DataStore
import java.io.File
import java.nio.file.Path


class MigrationScreenNavigationComponent(
    private val componentContext: ComponentContext,
    private val onBackClicked: () -> Unit
) : NavigationComponent, ComponentContext by componentContext {

    private val migrationViewModel by lazy {
        MigrationViewModel(
            onBackClicked
        )
    }

    @Composable
    override fun render() {

        val scope = rememberCoroutineScope()
        LaunchedEffect(migrationViewModel) {
            migrationViewModel.init(scope)
            migrationViewModel.startMigration()
        }

        MigrationScreenUI(migrationViewModel)
    }
}

@Composable
fun MigrationScreenUI(migrationViewModel: MigrationViewModel) {

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
                        Text("Migrate Project...")
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
                            Timber.i("Migrate UI -> back clicked")
                            migrationViewModel.onBackClicked()
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
                            Timber.i("Migrate UI -> finish clicked")
                            migrationViewModel.startMigration()
                        },
                    ) {
                        Text("FINISH", color = White1)
                    }
                }
            }

        }
    }
}


class MigrationViewModel(
    val onBackClicked: () -> Unit
) : ViewModel() {
    fun startMigration() {
        val selectedModules = DataStore.selectedPath
        Timber.i("MigrationViewModel -> startMigration")
        val modulePaths = selectedModules.values.toList()
            .onEach(::addGradleDependency)
            .onEach(::addBaseClassOnModule)

    }

    private fun addGradleDependency(moduleFile: File) {
        Timber.i("MigrationViewModel -> addGradleDependency")
        // find source path
        val moduleRoot = moduleFile.path.split("/").dropLast(2).joinToString("/")

        // find build gradle
        val buildGradle = File(moduleRoot).walk()
            .asSequence()
            .firstOrNull { it.path.contains("build.gradle") } ?: return

        Timber.d("MigrationViewModel -> addGradleDependency | buildGradle \n ${buildGradle.path}")

        val isKotlinDSL = buildGradle.name.contains(".kt")
        if (isKotlinDSL) {
            TODO("flow not developed")
        }

        // append build feature and update dependency file
        val gradleContent = buildGradle.readText()
        val isBuildFeaturePresent = gradleContent.contains("buildFeatures")
        if (isBuildFeaturePresent) {
            Timber.e("MigrationViewModel -> addGradleDependency | isBuildFeaturePresent : $isBuildFeaturePresent")
            //todo develop flow to handle
            return
        }
        val addedBuildFeatureGradleContent: String = Templates.appendBuildFeature(gradleContent)
        buildGradle.bufferedWriter().use { out ->
            out.write(addedBuildFeatureGradleContent)
        }
        Timber.e("MigrationViewModel -> addedBuildFeatureGradleContent | completed")
        Timber.e("--------------------------------")
        Timber.d(addedBuildFeatureGradleContent)
        Timber.e("--------------------------------")
    }

    private fun addBaseClassOnModule(moduleFile: File) {
        Timber.i("MigrationViewModel -> addBaseClassOnModule")

        val packageRoot = moduleFile.walk().asSequence()
            .firstOrNull { it.path.contains(".kt") }

        // TODO flow if package is in kotlin folder not java
        // TODO refactor code to support `\` in case of windows

        val packagePath = packageRoot?.path
            ?.split("java")
            ?.getOrNull(1)
            ?.split("/")
            ?.take(4)

        val packageName = packagePath?.joinToString(".")?.drop(1)

        val moduleRoot = packagePath
            ?.joinToString("/", prefix = "${moduleFile.path}/java")

        if (moduleRoot == null || packageName == null) {
            Timber.e("MigrationViewModel -> Root module not found")
            return
        }

        val viewBinderBaseFile = File("$moduleRoot/base")
        if (!viewBinderBaseFile.exists()) {
            viewBinderBaseFile.mkdir()
        }
        val wizardRoot = File(Path.of("").toAbsolutePath().toString())
        val viewBindingTemplate = wizardRoot.walk().asSequence()
            .firstOrNull { it.name.contains("ViewBindingTemplate.txt") }

        if (viewBindingTemplate?.exists() == false) {
            Timber.e("MigrationViewModel -> ViewBindingTemplate not found in project \n ${viewBindingTemplate.path}")
            return
        }

        val templateContent = viewBindingTemplate?.readText()
        if (templateContent.isNullOrEmpty()) {
            Timber.e("MigrationViewModel -> ViewBindingTemplate content is empty \n ${viewBindingTemplate?.path}")
            return
        }

        val packageNameTemplate = Templates.appendPackageName(templateContent, "${packageName}.base")
        val viewBindingBaseClassFile = File("${viewBinderBaseFile.path}/ViewBindingActivity.kt")
        if (!viewBindingBaseClassFile.exists()) {
            viewBindingBaseClassFile.bufferedWriter().use { out ->
                out.write(packageNameTemplate)
            }
            Timber.e("MigrationViewModel -> viewBindingBaseClassFile created")
        }
        Timber.e("MigrationViewModel -> addBaseClassOnModule completed for ${moduleFile.path}")
    }

}

