package com.github.ch8n.jetpackviewbindingmigratorplugin.services

import com.github.ch8n.jetpackviewbindingmigratorplugin.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
