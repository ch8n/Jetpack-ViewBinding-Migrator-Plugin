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

}