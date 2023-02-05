package com.wardellbagby.workflow_template.theming

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * The root theme used by Workflow screens that render using Compose.
 *
 * Will use dynamic colors on Android S and higher, and falls back to the Material3 default colors
 * when dynamic color aren't available.
 */
@Composable
fun AppTheme(
  useDarkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
  val colorScheme = when {
    dynamicColor && useDarkTheme -> dynamicDarkColorScheme(LocalContext.current)
    dynamicColor && !useDarkTheme -> dynamicLightColorScheme(LocalContext.current)
    useDarkTheme -> darkColorScheme()
    else -> lightColorScheme()
  }

  MaterialTheme(
    colorScheme = colorScheme,
  ) {
    Surface(content = content)
  }
}
