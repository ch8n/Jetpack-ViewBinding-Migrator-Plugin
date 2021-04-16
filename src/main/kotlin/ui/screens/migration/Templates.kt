package ui.screens.migration

object Templates {

    fun appendBuildFeature(gradleContent: String): String {
        val (before, after) = gradleContent.split("android {")
        return """
${before.trim()}

android {
    buildFeatures.viewBinding = true
    ${after.trim()}

""".trimIndent()
    }

    fun appendPackageName(codeContent: String, packageName: String): String {
        return "package ${packageName}\n\n${codeContent}"
    }

    fun getViewBindingImportsForActivity(activityPackageName: String, bindingClassName: String): String {
        return """
        import $activityPackageName.base.ViewBindingActivity
        import$activityPackageName.databinding.$bindingClassName
        import android.view.LayoutInflater
        """.trimIndent()
    }

    fun getBindingInflatorTemplateForActivity(bindingClassName: String): String {
        return """   
        
        override val bindingInflater: (LayoutInflater) -> $bindingClassName
            get() = $bindingClassName::inflate
        """.trimIndent()
    }

}