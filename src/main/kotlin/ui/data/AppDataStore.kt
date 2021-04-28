package ui.data

import java.io.File

object AppDataStore {
    var projectConfig: ProjectSetting = ProjectSetting.SingleModuleProject("", "")
    var selectedModule = mutableMapOf<String, File>()
}