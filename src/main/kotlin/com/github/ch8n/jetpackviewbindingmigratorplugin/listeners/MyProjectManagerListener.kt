package com.github.ch8n.jetpackviewbindingmigratorplugin.listeners

import com.github.ch8n.jetpackviewbindingmigratorplugin.services.MyProjectService
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener

internal class MyProjectManagerListener : ProjectManagerListener {

    override fun projectOpened(project: Project) {
        project.service<MyProjectService>()
    }
}
