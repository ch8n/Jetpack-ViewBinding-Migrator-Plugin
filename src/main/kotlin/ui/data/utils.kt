package ui.data

import Themes.ViewBinderTheme
import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import com.arkivanov.decompose.extensions.compose.jetbrains.rememberRootComponent

/**
 * To support instant preview (replacement for android's @Preview annotation)
 */
fun Preview(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Window(
        title = "ViewBinder Wizard | AndroidBites",
        size = IntSize(1024, 600),
        resizable = false,
        centered = true,
    ) {
        ViewBinderTheme {
            Row(
                modifier = modifier.fillMaxSize()
            ) {
                content()
            }
        }
    }
}
