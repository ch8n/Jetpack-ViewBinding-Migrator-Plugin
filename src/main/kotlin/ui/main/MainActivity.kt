package ui.main

import Themes.ViewBinderTheme
import androidx.compose.desktop.Window
import androidx.compose.ui.unit.IntSize
import com.arkivanov.decompose.extensions.compose.jetbrains.rememberRootComponent
import framework.Activity
import framework.Timber
import ui.navigation.NavHostNavigationComponent

class MainActivity : Activity() {

    override fun onCreate() {
        super.onCreate()
        Timber.i("Init MainActivity...")
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