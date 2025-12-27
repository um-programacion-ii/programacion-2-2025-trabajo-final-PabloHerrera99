package com.evento.mobile

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.evento.mobile.presentation.navigation.AppNavigation
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Punto de entrada principal de la aplicación.
 *
 * Este composable es la raíz de la UI y configura:
 * - El sistema de navegación completo
 */
@Composable
@Preview
fun App() {
    MaterialTheme {
        AppNavigation()
    }
}