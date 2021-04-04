package Themes

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val LightColorPalette = lightColors(
    primary = Primary,
    primaryVariant = Secondary,
    secondary = Secondary,
    background = Primary,
    onBackground = White1,
    onPrimary = White1,
    onSecondary = White1
)

@Composable
fun ViewBinderTheme(content: @Composable() () -> Unit) {
    val colors = LightColorPalette
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}