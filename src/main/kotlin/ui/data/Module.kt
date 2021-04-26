package ui.data

enum class LayoutIdFormat {
    CAMEL_CASE,
    UNDERSCORE_CASE,
    KEBAB_CASE
}

enum class ProjectModuleType(val label: String) {
    SINGLE("Single-Module Project [ALPHA]"),
    MULTI("Multi-Module Project [TBA]"),
    NONE("")
}

sealed class MigrateConfig {
    data class ActivityConfig(val layoutIdFormat: LayoutIdFormat, val baseActivityName: String) : MigrateConfig()
    data class FragmentConfig(val layoutIdFormat: LayoutIdFormat, val baseFragmentName: String) : MigrateConfig()
    data class RecyclerAdapterConfig(val layoutIdFormat: LayoutIdFormat) : MigrateConfig()
    data class CustomViewConfig(val layoutIdFormat: LayoutIdFormat) : MigrateConfig()
}

sealed class ProjectSetting(projectPath: String) {
    object None : ProjectSetting("")
    data class SingleModuleProject(val baseFolderName: String, val projectPath: String) : ProjectSetting(projectPath)
    data class MultiModuleProject(val baseModuleName: String, val projectPath: String) : ProjectSetting(projectPath)
}

data class Error(val isVisible: Boolean, val message: String)