package Themes

import androidx.compose.material.Typography

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font

val Poppins = FontFamily(
    Font("font/poppins_regular.otf", FontWeight.Normal),
    Font("font/poppins_bold.otf", FontWeight.Bold),
    Font("font/poppins_medium.otf", FontWeight.Medium),
    Font("font/poppins_semibold.otf", FontWeight.SemiBold),
)

val Typography = Typography(
    h1 = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Bold,
        fontSize = sp24,
        color = TextColor
    ),
    h3 = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Bold,
        fontSize = sp16,
        color = TextColor
    ),
    caption = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = sp10,
        color = TextColor
    ),
    body1 = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = sp14,
        color = TextColor
    ),
    body2 = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = sp12,
        color = TextColor
    ),
    button = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Medium,
        fontSize = sp16,
        color = TextColor
    ),
)