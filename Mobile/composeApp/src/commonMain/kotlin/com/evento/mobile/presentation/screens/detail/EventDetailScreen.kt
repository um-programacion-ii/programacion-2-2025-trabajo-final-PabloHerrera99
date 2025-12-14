package com.evento.mobile.presentation.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.evento.mobile.data.model.event.EventoResponse
import com.evento.mobile.util.formatFechaCorta
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: Long,                          // ID del evento a mostrar
    viewModel: EventDetailViewModel,        // ViewModel con la lógica
    onNavigateBack: () -> Unit,             // Callback para volver atrás
    onNavigateToSeats: (Long) -> Unit       // Callback para ir a selección de asientos
) {
    // 1. Observar el estado del ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // 2. Estado para el Snackbar (mensajes temporales)
    val snackbarHostState = remember { SnackbarHostState() }
    // 3. Efecto: Cargar datos cuando la pantalla se abre
    LaunchedEffect(eventId) {
        viewModel.loadEventDetail(eventId)
    }
    // 4. Efecto: Manejar errores con Snackbar + navegación
    LaunchedEffect(uiState) {
        if (uiState is EventDetailUiState.Error) {
            val errorMessage = (uiState as EventDetailUiState.Error).message
            snackbarHostState.showSnackbar(errorMessage)
            onNavigateBack()  // Volver a la lista automáticamente
        }
    }
    // 5. Estructura de la pantalla
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    when (val state = uiState) {
                        is EventDetailUiState.Success -> Text(state.evento.titulo)
                        else -> Text("Detalle del Evento")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 6. Mostrar UI según el estado
            when (val state = uiState) {
                is EventDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is EventDetailUiState.Error -> {
                    // El error se maneja con Snackbar + navegación automática
                }
                is EventDetailUiState.Success -> {
                    EventDetailContent(
                        evento = state.evento,
                        asientosDisponibles = state.asientosDisponibles,
                        asientosTotales = state.asientosTotales,
                        porcentajeOcupado = state.porcentajeOcupado,
                        onVerAsientos = { onNavigateToSeats(eventId) }
                    )
                }
            }
        }
    }
}
@Composable
private fun EventDetailContent(
    evento: EventoResponse,
    asientosDisponibles: Int,
    asientosTotales: Int,
    porcentajeOcupado: Int,
    onVerAsientos: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())  // Hacer scrolleable
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. TÍTULO Y BADGE DEL TIPO
        Text(
            text = evento.titulo,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        AssistChip(
            onClick = { },  // No hace nada, solo visual
            label = { Text(evento.eventoTipo.nombre) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                labelColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        )
        // 2. CARD CON INFORMACIÓN BÁSICA
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Fecha con icono
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    val fechaLocal = evento.fecha.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                    Text(
                        text = "Fecha: ${formatFechaCorta(fechaLocal)}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                // Ubicación con icono
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Ubicación: ${evento.direccion ?: "No especificada"}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                // Precio con icono
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Paid,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Precio: $${String.format("%.2f", evento.precioEntrada)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        // 3. DESCRIPCIÓN
        if (!evento.descripcion.isNullOrBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = evento.descripcion,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Sin descripción disponible",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        // 4. DISPONIBILIDAD DE ASIENTOS
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (asientosDisponibles > 0) {
                    MaterialTheme.colorScheme.primaryContainer  // Verde si hay
                } else {
                    MaterialTheme.colorScheme.errorContainer    // Rojo si no hay
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Disponibilidad",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$asientosDisponibles de $asientosTotales disponibles ($porcentajeOcupado% ocupado)",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        // 5. BOTÓN VER ASIENTOS
        Button(
            onClick = onVerAsientos,
            modifier = Modifier.fillMaxWidth(),
            enabled = asientosDisponibles > 0  // Deshabilitado si no hay
        ) {
            Text(
                text = if (asientosDisponibles > 0) {
                    "Ver Asientos Disponibles"
                } else {
                    "Evento Agotado"
                }
            )
        }
    }
}