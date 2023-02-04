package tech.xken.tripbook.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorPalette = darkColors(
    primary = MainColorLight,
    primaryVariant = MainColorLight,
    secondary = SecondaryColorDark,
    background = MainColorDark,
    surface = MainColorDark,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
//    primary = Purple200,
//    primaryVariant = Purple700,
//    secondary = Teal200,
)

private val LightColorPalette = lightColors(
    primary = MainColorLight,
    primaryVariant = MainColorLight,
    secondary = MainColorLight,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    error = Color(0xFFB00020),
//    onError = Color(0xFF4FFFDF)
)

@Composable
fun TripbookTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }
    val view = LocalView.current
    if (!view.isInEditMode)
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if (darkTheme) {
                colors.background.copy(alpha = 0.905f)
            } else {
                colors.primary
            }.toArgb()

            window.navigationBarColor = if (darkTheme) {
                colors.background.copy()
            } else {
                colors.primary
            }.toArgb()

            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightNavigationBars = false
        }
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}