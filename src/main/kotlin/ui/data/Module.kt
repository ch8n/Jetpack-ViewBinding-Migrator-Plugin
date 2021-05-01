package ui.data

enum class Component {
    Activities,
    Fragments,
    Adapter,
    CustomViews
}

enum class LayoutIdsFormat(val example:String) {
    CamelCase(example = "textView"),
    UnderScoreCase(example = "text_view | text_View"),
    KebabCase(example = "text-view | text-View");
}

enum class ProjectModuleType(val label: String) {
    SINGLE("Single-Module Project [ALPHA]"),
    MULTI("Multi-Module Project [TBA]")
}

sealed class ComponentConfig {
    data class ActivityConfig(val layoutIdFormat: LayoutIdsFormat, val baseActivityName: String) : ComponentConfig()
    data class FragmentConfig(val layoutIdFormat: LayoutIdsFormat, val baseFragmentName: String) : ComponentConfig()
    data class RecyclerAdapterConfig(val layoutIdFormat: LayoutIdsFormat) : ComponentConfig()
    data class CustomViewConfig(val layoutIdFormat: LayoutIdsFormat) : ComponentConfig()
}

sealed class ProjectSetting(projectPath: String) {
    data class SingleModuleProject(val baseFolderName: String, val projectPath: String) : ProjectSetting(projectPath)
    data class MultiModuleProject(val baseModuleName: String, val projectPath: String) : ProjectSetting(projectPath)
}

data class Error(val isVisible: Boolean, val message: String)