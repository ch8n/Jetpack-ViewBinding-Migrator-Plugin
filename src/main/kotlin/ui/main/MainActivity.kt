package ui.main

import Themes.*
import androidx.compose.desktop.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.rememberRootComponent
import framework.Activity
import ui.navigation.NavHostNavigationComponent

class MainActivity : Activity() {
    override fun onCreate() {
        super.onCreate()
        setContentView()
    }

    private fun setContentView() = Window(
        title = "ViewBinder Wizard | AndroidBites",
        size = IntSize(1024, 600),
        resizable = false,
        centered = true,
    ) {
        ViewBinderTheme {
            rememberRootComponent(factory = ::NavHostNavigationComponent).render()
        }
    }
}