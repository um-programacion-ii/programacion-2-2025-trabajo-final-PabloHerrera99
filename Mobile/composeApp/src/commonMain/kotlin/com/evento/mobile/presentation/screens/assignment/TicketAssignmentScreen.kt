package com.evento.mobile.presentation.screens.assignment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Pantalla de asignación de nombres a tickets.
 *
 * Permite al usuario asignar un nombre completo a cada asiento seleccionado.
 * Incluye validación en tiempo real y temporizador de expiración.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketAssignmentScreen(
    viewModel: TicketAssignmentViewModel,
    onBack: () -> Unit,
    onContinue: (List<SeatWithName>) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    // Mostrar snackbar cuando hay mensaje
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asignar Nombres") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
            when (val state = uiState) {
                is TicketAssignmentUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is TicketAssignmentUiState.Success -> {
                    AssignmentContent(
                        seats = state.seats,
                        timeRemainingSeconds = state.timeRemainingSeconds,
                        isSubmitting = state.isSubmitting,
                        onNameChange = { fila, columna, nombre ->
                            viewModel.updateName(fila, columna, nombre)
                        },
                        onContinue = {
                            viewModel.submitNames(onSuccess = { seats ->
                                onContinue(seats)
                            })
                        }
                    )
                }
                is TicketAssignmentUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

/**
 * Contenido principal de la pantalla.
 */
@Composable
fun AssignmentContent(
    seats: List<SeatWithName>,
    timeRemainingSeconds: Int,
    isSubmitting: Boolean,
    onNameChange: (Int, Int, String) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Temporizador de expiración
        TimerCard(timeRemainingSeconds)
        // Instrucciones
        InstructionsCard()
        Spacer(modifier = Modifier.height(8.dp))
        // Lista de asientos con TextFields
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(seats) { index, seat ->
                SeatNameCard(
                    seat = seat,
                    index = index + 1,
                    onNameChange = { nombre ->
                        onNameChange(seat.fila, seat.columna, nombre)
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Botón de continuar
        ContinueButton(
            enabled = seats.all { it.isValid } && !isSubmitting,
            isSubmitting = isSubmitting,
            onClick = onContinue,
            modifier = Modifier.padding(16.dp)
        )
    }
}
/**
 * Card con temporizador de cuenta regresiva.
 */
@Composable
fun TimerCard(timeRemainingSeconds: Int) {
    val minutes = timeRemainingSeconds / 60
    val seconds = timeRemainingSeconds % 60
    val isWarning = timeRemainingSeconds <= 60
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isWarning) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isWarning) Icons.Default.Warning else Icons.Default.Info,
                contentDescription = null,
                tint = if (isWarning) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSecondaryContainer
                }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Tiempo restante",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isWarning) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    }
                )
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isWarning) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    }
                )
            }
        }
    }
}

/**
 * Card con instrucciones.
 */
@Composable
fun InstructionsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Ingresa el nombre completo de cada persona (mínimo 3 caracteres)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
/**
 * Card individual para cada asiento con TextField.
 */
@Composable
fun SeatNameCard(
    seat: SeatWithName,
    index: Int,
    onNameChange: (String) -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Título del asiento
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Asiento $index",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Fila ${seat.fila}, Columna ${seat.columna}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Badge con ID del asiento
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = seat.seatId,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            // TextField para nombre
            OutlinedTextField(
                value = seat.nombre,
                onValueChange = onNameChange,
                label = { Text("Nombre completo") },
                placeholder = { Text("Ej: Juan Pérez González") },
                isError = seat.nombre.isNotEmpty() && !seat.isValid,
                supportingText = {
                    if (seat.nombre.isNotEmpty() && !seat.isValid) {
                        Text(
                            text = "Mínimo ${TicketAssignmentViewModel.MIN_NAME_LENGTH} caracteres",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}
/**
 * Botón de continuar a confirmación.
 */
@Composable
fun ContinueButton(
    enabled: Boolean,
    isSubmitting: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth()
    ) {
        if (isSubmitting) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Enviando nombres...")
        } else {
            Text("Continuar a Confirmación")
        }
    }
}