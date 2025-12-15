package com.evento.mobile.presentation.screens.seats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evento.mobile.data.model.seat.AsientoDisponibilidadResponse
import com.evento.mobile.data.model.seat.EstadoAsiento

/**
 * Pantalla de selección de asientos.
 *
 * Permite al usuario:
 * - Ver disponibilidad de asientos en formato grid
 * - Seleccionar hasta 4 asientos
 * - Bloquear asientos y continuar con el proceso de compra
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatSelectionScreen(
    viewModel: SeatSelectionViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToTicketAssignment: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Manejo de mensajes con Snackbar
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = if (uiState is SeatSelectionUiState.Error) "Reintentar" else null,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.retry()
            }
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Selección de Asientos")
                        if (uiState is SeatSelectionUiState.Success) {
                            Text(
                                "Sesión activa",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 11.sp
                            )
                        }
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
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (val state = uiState) {
                is SeatSelectionUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is SeatSelectionUiState.Success -> {
                    SeatGridContent(
                        matriz = state.matriz,
                        selectedSeats = state.selectedSeats,
                        isBlocking = state.isBlocking,
                        onSeatClick = { seatId -> viewModel.toggleSeat(seatId) },
                        onContinueClick = {
                            viewModel.blockSeatsAndContinue { sessionId ->
                                onNavigateToTicketAssignment(sessionId)
                            }
                        }
                    )
                }

                is SeatSelectionUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.retry() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}
/**
 * Contenido principal: grid de asientos + controles.
 */
@Composable
fun SeatGridContent(
    matriz: com.evento.mobile.data.model.seat.MatrizAsientosResponse,
    selectedSeats: Set<String>,
    isBlocking: Boolean,
    onSeatClick: (String) -> Unit,
    onContinueClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Indicador de pantalla/escenario
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "PANTALLA / ESCENARIO",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid de asientos con scroll vertical
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            items(matriz.totalFilas) { filaIndex ->
                val fila = filaIndex + 1
                SeatRow(
                    fila = fila,
                    totalColumnas = matriz.totalColumnas,
                    asientos = matriz.asientos,
                    selectedSeats = selectedSeats,
                    onSeatClick = onSeatClick
                )
            }
        }

        // Leyenda de colores
        HorizontalDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(color = Color.Gray, text = "Disponible")
            LegendItem(color = Color.Red, text = "Vendido/Bloqueado")
            LegendItem(color = Color.Green, text = "Seleccionado")
        }

        // Info y botón de continuar
        HorizontalDivider()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Asientos seleccionados: ${selectedSeats.size}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onContinueClick,
                enabled = selectedSeats.isNotEmpty() && !isBlocking,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isBlocking) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Bloqueando asientos...")
                } else {
                    Text("Continuar con ${selectedSeats.size} asiento(s)")
                }
            }
        }
    }
}
/**
 * Fila de asientos con scroll horizontal.
 */
@Composable
fun SeatRow(
    fila: Int,
    totalColumnas: Int,
    asientos: List<AsientoDisponibilidadResponse>,
    selectedSeats: Set<String>,
    onSeatClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Número de fila
        Text(
            text = "$fila",
            modifier = Modifier.width(30.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        // Asientos con scroll horizontal
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(totalColumnas) { columnaIndex ->
                val columna = columnaIndex + 1
                val asiento = asientos.find { it.fila == fila && it.columna == columna }

                if (asiento != null) {
                    val seatId = "$fila-$columna"
                    SeatItem(
                        asiento = asiento,
                        columna = columna,
                        isSelected = seatId in selectedSeats,
                        onClick = { onSeatClick(seatId) }
                    )
                }
            }
        }
    }
}
/**
 * Componente individual de asiento.
 */
@Composable
fun SeatItem(
    asiento: AsientoDisponibilidadResponse,
    columna: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> Color.Green
        asiento.estado == EstadoAsiento.VENDIDO || asiento.estado == EstadoAsiento.BLOQUEADO -> Color.Red
        asiento.estado == EstadoAsiento.DISPONIBLE -> Color.Gray
        else -> Color.DarkGray
    }

    val enabled = asiento.estado == EstadoAsiento.DISPONIBLE
    val columnaLetra = ('A' + (columna - 1)).toString()

    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                color = backgroundColor.copy(alpha = if (enabled || isSelected) 1f else 0.7f),
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = columnaLetra,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
/**
 * Item de leyenda (color + texto).
 */
@Composable
fun LegendItem(color: Color, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, shape = RoundedCornerShape(3.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            style = MaterialTheme.typography.labelSmall
        )
    }
}