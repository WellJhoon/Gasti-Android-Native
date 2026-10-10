package com.jhonmedina.gasty.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// =========================================================================
// 🎨 SISTEMA DE COLORES SEMÁNTICOS DE GASTY
// Contiene todos los tokens de diseño que cambian entre Dark y Light
// =========================================================================
data class GastyColors(
    val isDark: Boolean,
    val bg: Color,
    val card: Color,
    val border: Color,
    val text: Color,
    val textMuted: Color,
    val olive: Color = GastyOlive,
    val oliveSoft: Color,
    val coral: Color = GastyCoral,
    val coralSoft: Color,
    val amber: Color = GastyAmber,
    val amberSoft: Color,
    val blue: Color = GastyBlue,
    val blueSoft: Color,
    val purple: Color = GastyPurple,
    val purpleSoft: Color,
    val teal: Color = GastyTeal,
    val tealSoft: Color
)

// Modo Oscuro (Predefinido de Gasty)
val DarkGastyColors = GastyColors(
    isDark = true,
    bg = DarkBg,
    card = DarkCard,
    border = DarkBorder,
    text = DarkText,
    textMuted = DarkMuted,
    oliveSoft = DarkOliveSoft,
    coralSoft = DarkCoralSoft,
    amberSoft = DarkAmberSoft,
    blueSoft = DarkBlueSoft,
    purpleSoft = DarkPurpleSoft,
    tealSoft = DarkTealSoft
)

// Modo Claro (Diseño suave idéntico a Tailwind playful.light)
val LightGastyColors = GastyColors(
    isDark = false,
    bg = LightBg,
    card = LightCard,
    border = LightBorder,
    text = LightText,
    textMuted = LightMuted,
    oliveSoft = LightOliveSoft,
    coralSoft = LightCoralSoft,
    amberSoft = LightAmberSoft,
    blueSoft = LightBlueSoft,
    purpleSoft = LightPurpleSoft,
    tealSoft = LightTealSoft
)

// =========================================================================
// ⚛️ COMPOSITION LOCAL (Equivalente al React.createContext)
// =========================================================================
val LocalGastyColors = staticCompositionLocalOf { DarkGastyColors }

// =========================================================================
// 🚀 ACCESO GLOBAL: GastyTheme.colors (Equivalente al hook useTheme())
// =========================================================================
object GastyTheme {
    val colors: GastyColors
        @Composable
        @ReadOnlyComposable
        get() = LocalGastyColors.current
}

// =========================================================================
// 📱 THEME PROVIDER (Equivalente a <ThemeProvider value={...}>)
// Envuelve la aplicación o preview y provee los colores correctos.
// =========================================================================
@Composable
fun GastyTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkGastyColors else LightGastyColors

    CompositionLocalProvider(LocalGastyColors provides colors) {
        MaterialTheme(
            colorScheme = if (darkTheme) {
                darkColorScheme(
                    background = colors.bg,
                    surface = colors.card,
                    onBackground = colors.text,
                    onSurface = colors.text,
                    outline = colors.border
                )
            } else {
                lightColorScheme(
                    background = colors.bg,
                    surface = colors.card,
                    onBackground = colors.text,
                    onSurface = colors.text,
                    outline = colors.border
                )
            },
            content = content
        )
    }
}
