package ui.data

import java.io.File

object AppDataStore {
    var projectConfig: ProjectSetting = ProjectSetting.SingleModuleProject("", "")
    val selectedModule = mutableMapOf<String, File>()
    val migrateComponent = mutableMapOf<Component, ComponentConfig>()
}