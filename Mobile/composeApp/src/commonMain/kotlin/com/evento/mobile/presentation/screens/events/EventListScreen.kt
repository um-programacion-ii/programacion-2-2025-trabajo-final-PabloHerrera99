package com.evento.mobile.presentation.screens.events

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.evento.mobile.data.repository.AuthRepository
/**
 * Pantalla placeholder de lista de eventos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(
    authRepository: AuthRepository,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eventos") },
                actions = {
                    // Botón de logout en la esquina superior derecha
                    IconButton(
                        onClick = {
                            // Limpiar el token
                            authRepository.logout()
                            // Navegar al login
                            onLogout()
                        }
                    ) {
                        Text(
                            text = "Salir",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        // Contenido centrado
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Título principal
            Text(
                text = "Lista de Eventos",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Mensaje placeholder
            Text(
                text = "Próximamente...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}