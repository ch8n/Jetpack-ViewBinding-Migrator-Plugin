package ui.screens.migration

object Templates {

    fun appendBuildFeature(gradleContent: String): String {
        val (before, after) = gradleContent.split("android {")
        return """
${before.trim()}

android {
    
    /* 
     * ViewBinderWizard Notes :
     *  1. Gradle plugin should be more than 4.0.0
     *     i.e `build.gradle` of project should be minimum `com.android.tools.build:gradle:4.0.0`  
     *  2. Minimum Gradle version should be `6.1.1` 
     *     i.e `gradle-wrapper.properties` should be minimum `distributionUrl=https\://services.gradle.org/distributions/gradle-6.1.1.zip`
     *  3. Sync project after migration complete
     */
    
    buildFeatures.viewBinding = true
    ${after.trim()}

""".trimIndent()
    }

    fun appendPackageName(codeContent: String, packageName: String): String {
        return "package ${packageName}\n\n${codeContent}"
    }

    fun getViewBindingImportsForActivity(basePackageName:String,activityPackageName: String, bindingClassName: String): String {
        return """
        import $basePackageName.ViewBindingActivity
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