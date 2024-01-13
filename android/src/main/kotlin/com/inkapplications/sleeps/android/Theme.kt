package com.inkapplications.sleeps.android

import android.graphics.Typeface
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import ink.ui.render.compose.theme.ColorVariant
import ink.ui.render.compose.theme.ComposeRenderTheme
import ink.ui.render.compose.theme.TypographyVariant

@Composable
fun getRenderTheme(): ComposeRenderTheme {
    val colorVariant = if (isSystemInDarkTheme()) {
        ColorVariant.Defaults.dark
    } else {
        ColorVariant.Defaults.light
    }.let {
        if (Build.VERSION.SDK_INT >= 31) {
            it.copy(primary = colorResource(android.R.color.system_accent1_400))
        } else it
    }

    val font = FontFamily(
        Font(R.font.roboto_mono_light, FontWeight.Normal),
        Font(R.font.roboto_mono_medium, FontWeight.Bold),
    )

    return ComposeRenderTheme(
        colors = colorVariant,
        typography = TypographyVariant().withFontFamily(font),
    )
}
