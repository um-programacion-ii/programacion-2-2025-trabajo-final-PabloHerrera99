package com.evento.mobile.presentation.screens.confirmation

import com.evento.mobile.presentation.screens.confirmation.PurchaseConfirmationUiState.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


/**
 * Pantalla de confirmación de compra.
 *
 * Flujo:
 * 1. Al cargar, llama automáticamente a POST /api/compra/confirmar
 * 2. Muestra loading mientras espera respuesta
 * 3. Si exitosa: muestra pantalla de confirmacion
 * 4. Si falla: muestra error con opción de reintentar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseConfirmationScreen(
    viewModel: PurchaseConfirmationViewModel,
    onNavigateToEventList: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Confirmación de Compra") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is Loading -> LoadingContent()
                is Success -> SuccessContent(onNavigateToEventList)
                is Error -> ErrorContent(
                    message = (uiState as Error).message,
                    onRetry = { viewModel.confirmPurchase() },
                    onCancel = onNavigateToEventList
                )
            }
        }
    }
}
@Composable
private fun LoadingContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator()
        Text("Procesando tu compra...")
    }
}
@Composable
private fun SuccessContent(onNavigateToEventList: () -> Unit) {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "¡Compra realizada exitosamente!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Tu compra ha sido procesada correctamente.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Button(
            onClick = onNavigateToEventList,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver a Lista de Eventos")
        }
    }
}
@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Text(
            text = "Error al confirmar compra",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = onRetry,
                modifier = Modifier.weight(1f)
            ) {
                Text("Reintentar")
            }
        }
    }
}