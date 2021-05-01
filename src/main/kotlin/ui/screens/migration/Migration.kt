package ui.screens.migration

import Themes.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import kotlinx.coroutines.flow.MutableStateFlow
import ui.data.AppDataStore
import ui.data.Component
import ui.data.ComponentConfig
import ui.data.ProjectSetting
import java.io.File
import java.io.FileWriter
import java.nio.file.Path
import kotlin.system.exitProcess


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

                    Column(
                        modifier = Modifier
                            .padding(dp20)
                            .fillMaxSize()
                    ) {
                        //TODO how to make scrollable???
                        Text(
                            text = "Migrate Project",
                            style = MaterialTheme.typography.h1,
                            color = White1
                        )
                        val progressState: Float by migrationViewModel.progressBarState.collectAsState()

                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth().padding(top = dp8),
                            progress = progressState,
                            color = Green
                        )

                        val scrollState = rememberScrollState()
                        val logMessage: String by migrationViewModel.logMessageState.collectAsState()

                        TextField(
                            modifier = Modifier.padding(top = dp8)
                                .fillMaxSize()
                                .verticalScroll(state = scrollState),
                            value = TextFieldValue(text = logMessage),
                            onValueChange = {},
                            readOnly = true
                        )


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

                val migrationProgress: Float by migrationViewModel.progressBarState.collectAsState()

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
                        exitProcess(0)
                    },
                    enabled = migrationProgress >= 1f
                ) {
                    Text("FINISH", color = White1)
                }
            }

        }
    }
}


class MigrationViewModel(
    val onBackClicked: () -> Unit
) : ViewModel() {

    private val projectConfig = AppDataStore.projectConfig as ProjectSetting.SingleModuleProject
    private val selectedModules = AppDataStore.selectedModule
    private val componentConfig = AppDataStore.migrateComponent

    val progressBarState = MutableStateFlow(0.0f)
    val logMessageState = MutableStateFlow("")

    fun startMigration() {
        val progressStep = 1 / selectedModules.values.size.toFloat()
        //TODO TEST
        Timber.i("MigrationViewModel -> startMigration")
        val modulePaths = selectedModules.values.toList()
            .onEach(::addGradleDependency)
            .also { Timber.e("completed addGradleDependency") }
            .onEach(::addBaseClassOnModule)
            .also { Timber.e("completed addBaseClassOnModule") }
            .map(::parseResourceIds)
            .also { Timber.e("completed parseResourceIds") }
            .onEach { (moduleFile, ids) -> migrateActivityIds(moduleFile, ids) }
            .also { Timber.e("completed migrateActivityIds") }
            .onEach { progressBarState.value = progressBarState.value + progressStep }
            .also { Timber.e("completed progressBarState ${progressBarState.value}") }
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

        logMessageState.value = "${logMessageState.value}\n${moduleFile.name} | added viewbinding gradle dependency"
        Timber.e("MigrationViewModel -> addedBuildFeatureGradleContent | completed")
        Timber.e("--------------------------------")
        Timber.d(addedBuildFeatureGradleContent)
        Timber.e("--------------------------------")
    }

    private fun addBaseClassOnModule(moduleFile: File) {
        Timber.i("MigrationViewModel -> addBaseClassOnModule")

        val (packageName, moduleRoot) = packagePathAndName(moduleFile)

        if (moduleRoot == null || packageName == null) {
            Timber.e("MigrationViewModel -> Root module not found")
            return
        }

        val baseFolderName = projectConfig.baseFolderName
        val viewBinderBaseFile = moduleFile.walk()
            .firstOrNull { it.name.contains(baseFolderName) }
            ?: File("$moduleRoot/$baseFolderName")

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
        val activityConfig = componentConfig.get(Component.Activities) as ComponentConfig.ActivityConfig
        val basePackagesTemplate = packageNameTemplate
            .replace("<ReplaceBasePackage>", "$moduleRoot/$baseFolderName")
            .replace("<ReplaceBaseName>", activityConfig.baseActivityName)

        val viewBindingBaseClassFile = File("${viewBinderBaseFile.path}/ViewBindingActivity.kt")
        if (!viewBindingBaseClassFile.exists()) {
            viewBindingBaseClassFile.bufferedWriter().use { out ->
                out.write(basePackagesTemplate)
            }
            Timber.e("MigrationViewModel -> viewBindingBaseClassFile created")
        }
        logMessageState.value = "${logMessageState.value}\n${moduleFile.name} | added base classes"
        Timber.e("MigrationViewModel -> addBaseClassOnModule completed for ${moduleFile.path}")
    }

    private fun packagePathAndName(moduleFile: File): Pair<String?, String?> {

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

        return Pair(packageName, moduleRoot)
    }

    private fun parseResourceIds(moduleFile: File): Pair<File, Map<String, String>> {
        Timber.i("MigrationViewModel -> parseResourceIds \n ${moduleFile.path}")

        val resourcesFiles = File("${moduleFile.path}/res/layout")
        if (!resourcesFiles.exists()) {
            Timber.e("MigrationViewModel -> resourcesFiles not found \n ${resourcesFiles.path}")
            return moduleFile to emptyMap()
        }

        val allResourceIds = resourcesFiles.walk().asSequence()
            .filter { it.name.contains(".xml") }
            .flatMap { it.readText().lines() }
            .filter { it.contains("android:id=\"@+id/") }
            .map { it.split("android:id=\"@+id/")[1].dropLast(1).trim() }

        // todo figure out other popular conventions?
        val bindingIds = allResourceIds.map { id ->
            val words = id.split("_")
            val first = words.take(1)
            val others = words.drop(1).map { it.capitalize() }
            id to (first + others).joinToString(separator = "")
        }.toMap()

        logMessageState.value = "${logMessageState.value}\n${moduleFile.name} | parsed binding ids"
        Timber.d("MigrationViewModel -> allResourceIds \n ${allResourceIds.joinToString("\n")}")
        Timber.d("MigrationViewModel -> allBindingIds \n ${bindingIds.entries.joinToString("\n")}")

        return moduleFile to bindingIds
    }

    private fun migrateActivityIds(moduleFile: File, ids: Map</*layoutIds*/String,/*bindingIds*/String>) {
        Timber.i("MigrationViewModel -> migrateActivityIds \n ${moduleFile.path}")
        val (packageName, moduleRoot) = packagePathAndName(moduleFile)

        if (moduleRoot == null || packageName == null) {
            Timber.e("MigrationViewModel -> migrateActivityIds Root module not found")
            return
        }

        val root = File(moduleRoot)
        Timber.i("MigrationViewModel -> rootFile \n ${root.path}")
        val activityConfig = componentConfig.get(Component.Activities) as ComponentConfig.ActivityConfig
        val activities = root.walk().asSequence()
            .filter { it.name.contains(".kt") }
            .filter { !it.path.contains("base") }
            .filter { it.readText().contains("${activityConfig.baseActivityName}()") }
            .toList()
            .also {
                Timber.d("MigrationViewModel -> Activity files \n ${it.joinToString("\n")}")
            }

        activities
            .filter { !it.readText().contains("ViewBindingActivity<") }
            .onEach { activity ->
                val (_, bindingClassName) = getBindClassName(activity)
                val activityContentWithImports = modifyActivityImportsForViewBinding(activity, bindingClassName)
                val activityContentWithViewBinder =
                    modifySuperClassOfActivity(activityContentWithImports, bindingClassName)
                val activityContextWithBindings = modifyActivityLayoutIds(activityContentWithViewBinder, ids)
                overrideCurrentActivityContent(activityContextWithBindings, activity)
            }

        logMessageState.value = "${logMessageState.value}\n${moduleFile.name} | migrated layout ids to binding ids"
    }

    private fun getBindClassName(activity: File): Pair<String, String> {
        val activityContent = activity.readText()
        val activityLines = activityContent.lines()
        val layoutLine = activityLines.first { it.contains("R.layout") }
        val layoutName = layoutLine.split(".").last()
        Timber.d("MigrationViewModel -> layoutName $layoutName")

        val viewBindingClassName = layoutName.split("_")
            .joinToString(separator = "") { it.capitalize() }
            .let { "${it}Binding" }

        Timber.d("MigrationViewModel -> viewBindingClassName $viewBindingClassName")
        return layoutName to viewBindingClassName
    }

    private fun getActivityPackageName(activity: File): String {
        val activityContent = activity.readText()
        val activityLines = activityContent.lines()
        val packageNameLine = activityLines.first { it.contains("package") }
        val (_, packageName) = packageNameLine.split("package")
        return packageName
    }

    private fun modifyActivityImportsForViewBinding(activity: File, bindingClassName: String): String {
        val activityContent = activity.readText()
        val activityLines = activityContent.lines().toMutableList()

        val syntheticImportIndex = activityLines.indexOfFirst {
            it.contains("kotlinx.android.synthetic")
        }

        val activityPackageName = getActivityPackageName(activity).split(".").take(3).joinToString(".")
        val importTemplate = Templates.getViewBindingImportsForActivity(activityPackageName, bindingClassName)

        // add imports for viewbinding
        activityLines.add(syntheticImportIndex, importTemplate)

        return activityLines.joinToString("\n")
    }

    private fun modifySuperClassOfActivity(activityContent: String, bindingClassName: String): String {

        val activityLines = activityContent.lines().toMutableList()
        val activityConfig = componentConfig.get(Component.Activities) as ComponentConfig.ActivityConfig

        //replace parent activity with viewbinding Activity
        val superClassLineIndex = activityLines.indexOfFirst { it.contains(": ${activityConfig.baseActivityName}") }
        val viewBindSuperClass = activityLines[superClassLineIndex]
            .replace(activityConfig.baseActivityName, "ViewBindingActivity<$bindingClassName>")
        activityLines.removeAt(superClassLineIndex)
        activityLines.add(superClassLineIndex, viewBindSuperClass)

        // add viewbinding inflater method
        val inflatorTemplate = Templates.getBindingInflatorTemplateForActivity(bindingClassName)
        activityLines.add(superClassLineIndex + 1, inflatorTemplate)

        // remove content view
        activityLines.removeIf { it.contains("setContentView") }
        return activityLines.joinToString("\n")
    }

    private fun modifyActivityLayoutIds(activityContent: String, ids: Map<String, String>): String {
        // hack for concurrent read write
        var activityLines = activityContent.lines().toMutableList()
        ids.forEach { (layoutId, bindingId) ->
            activityLines.mapIndexed { index, line ->
                if (line.contains(layoutId)) {
                    val updatedLine = line.replace(layoutId, "binding.$bindingId")
                    activityLines.removeAt(index)
                    activityLines.add(index, updatedLine)
                }
                activityLines = activityLines.toMutableList()
            }
        }
        val activityWithBindings = activityLines.toMutableList()
        return activityWithBindings.joinToString("\n")
    }

    private fun overrideCurrentActivityContent(activityContent: String, activity: File) {
        val fileWriter = FileWriter(activity, false)
        fileWriter.write(activityContent)
        fileWriter.close()
    }
}

