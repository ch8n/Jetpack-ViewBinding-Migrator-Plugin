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
import java.io.FileWriter
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
            .map(::parseResourceIds)
            .onEach { (moduleFile, ids) -> migrateActivityIds(moduleFile, ids) }

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

        val (packageName, moduleRoot) = packagePathAndName(moduleFile)

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

        val activities = root.walk().asSequence()
            .filter { it.name.contains(".kt") }
            .filter { !it.path.contains("base") }
            .filter { it.readText().contains("AppCompatActivity()") }
            .toList()
            .also {
                Timber.d("MigrationViewModel -> Activity files \n ${it.joinToString("\n")}")
            }

        activities
            .filter { !it.readText().contains("ViewBindingActivity<") }
            .onEach { activity ->
                val (_, bindingClassName) = getBindClassName(activity)
                val activityContentWithImports = modifyActivityImportsForViewBinding(activity, bindingClassName)
                val activityContentWithViewBinder = modifySuperClassOfActivity(activityContentWithImports, bindingClassName)
                val activityContextWithBindings = modifyActivityLayoutIds(activityContentWithViewBinder, ids)
                overrideCurrentActivityContent(activityContextWithBindings,activity)
            }

    }

    private fun getBindClassName(activity: File): Pair<String, String> {
        val activityContent = activity.readText()
        val activityLines = activityContent.lines()
        val layoutLine = activityLines.first { it.contains("R.layout") }
        val layoutName = layoutLine.split(".").last().dropLast(1)
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

        val activityPackageName = getActivityPackageName(activity)
        val importTemplate = Templates.getViewBindingImportsForActivity(activityPackageName, bindingClassName)

        // add imports for viewbinding
        activityLines.add(syntheticImportIndex, importTemplate)

        return activityLines.joinToString("\n")
    }

    private fun modifySuperClassOfActivity(activityContent: String, bindingClassName: String): String {

        val activityLines = activityContent.lines().toMutableList()

        //replace parent activity with viewbinding Activity
        val superClassLineIndex = activityLines.indexOfFirst { it.contains(": AppCompatActivity") }
        val viewBindSuperClass = activityLines[superClassLineIndex]
            .replace("AppCompatActivity", "ViewBindingActivity<$bindingClassName>")
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

    private fun overrideCurrentActivityContent(activityContent: String,activity: File){
        val fileWriter = FileWriter(activity, false)
        fileWriter.write(activityContent)
        fileWriter.close()
    }
}

