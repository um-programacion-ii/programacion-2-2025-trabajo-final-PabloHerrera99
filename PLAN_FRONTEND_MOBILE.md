# 📱 Plan de Implementación - Frontend Mobile (Kotlin Multiplatform)

## 📊 Información del Proyecto

**Nombre:** EventoMobile  
**Tecnología:** Kotlin Multiplatform (KMP) + Compose Multiplatform  
**Plataforma Principal:** Android (con soporte iOS)  
**Backend:** Spring Boot (puerto 8081)  
**Paquete:** `com.evento.mobile`  
**Ubicación:** `/Mobile`  

---

## 🎯 Objetivo General

Desarrollar una aplicación móvil multiplataforma que permita a los usuarios:
1. Autenticarse con el backend
2. Ver lista de eventos disponibles
3. Consultar detalles de eventos y disponibilidad de asientos
4. Seleccionar asientos (máximo 4)
5. Asignar nombres a los asientos seleccionados
6. Confirmar la compra de asientos
7. Mantener sesión sincronizada entre dispositivos

---

## 📐 Arquitectura del Proyecto

### **Patrón de Arquitectura:** MVVM (Model-View-ViewModel)

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐        │
│  │  Screen    │  │ ViewModel  │  │   State    │        │
│  │ (Compose)  │←─│ (Logic)    │←─│  (Data)    │        │
│  └────────────┘  └────────────┘  └────────────┘        │
└───────────────────────────┬─────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────┐
│                     Domain Layer                         │
│  ┌────────────────────────────────────────────┐         │
│  │    Use Cases (Opcional para este MVP)      │         │
│  └────────────────────────────────────────────┘         │
└───────────────────────────┬─────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────┐
│                      Data Layer                          │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐        │
│  │ Repository │  │ API Service│  │   Models   │        │
│  │            │→─│  (Ktor)    │  │   (DTOs)   │        │
│  └────────────┘  └────────────┘  └────────────┘        │
└─────────────────────────────────────────────────────────┘
```

---

## 📁 Estructura de Directorios

```
Mobile/
├── composeApp/
│   ├── src/
│   │   ├── commonMain/
│   │   │   ├── kotlin/com/evento/mobile/
│   │   │   │   ├── data/
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── auth/
│   │   │   │   │   │   │   ├── LoginRequest.kt
│   │   │   │   │   │   │   └── LoginResponse.kt
│   │   │   │   │   │   ├── evento/
│   │   │   │   │   │   │   ├── EventoDTO.kt
│   │   │   │   │   │   │   ├── EventoTipoDTO.kt
│   │   │   │   │   │   │   └── IntegranteDTO.kt
│   │   │   │   │   │   ├── sesion/
│   │   │   │   │   │   │   ├── SesionDTO.kt
│   │   │   │   │   │   │   ├── IniciarSesionRequest.kt
│   │   │   │   │   │   │   ├── SeleccionarAsientosRequest.kt
│   │   │   │   │   │   │   ├── AsignarNombresRequest.kt
│   │   │   │   │   │   │   └── AsientoDTO.kt
│   │   │   │   │   │   ├── asiento/
│   │   │   │   │   │   │   ├── DisponibilidadDTO.kt
│   │   │   │   │   │   │   └── AsientoDisponibilidadDTO.kt
│   │   │   │   │   │   └── venta/
│   │   │   │   │   │       └── VentaDTO.kt
│   │   │   │   │   ├── remote/
│   │   │   │   │   │   ├── NetworkConfig.kt
│   │   │   │   │   │   └── ApiService.kt
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   ├── AuthRepository.kt
│   │   │   │   │   │   ├── EventoRepository.kt
│   │   │   │   │   │   ├── SesionRepository.kt
│   │   │   │   │   │   └── AsientoRepository.kt
│   │   │   │   │   └── local/
│   │   │   │   │       └── TokenStorage.kt
│   │   │   │   ├── domain/ (opcional)
│   │   │   │   ├── presentation/
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── login/
│   │   │   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   │   │   ├── LoginViewModel.kt
│   │   │   │   │   │   │   └── LoginState.kt
│   │   │   │   │   │   ├── events/
│   │   │   │   │   │   │   ├── EventListScreen.kt
│   │   │   │   │   │   │   ├── EventListViewModel.kt
│   │   │   │   │   │   │   └── EventListState.kt
│   │   │   │   │   │   ├── eventdetail/
│   │   │   │   │   │   │   ├── EventDetailScreen.kt
│   │   │   │   │   │   │   ├── EventDetailViewModel.kt
│   │   │   │   │   │   │   └── EventDetailState.kt
│   │   │   │   │   │   ├── seatselection/
│   │   │   │   │   │   │   ├── SeatSelectionScreen.kt
│   │   │   │   │   │   │   ├── SeatSelectionViewModel.kt
│   │   │   │   │   │   │   └── SeatSelectionState.kt
│   │   │   │   │   │   ├── persondata/
│   │   │   │   │   │   │   ├── PersonDataScreen.kt
│   │   │   │   │   │   │   ├── PersonDataViewModel.kt
│   │   │   │   │   │   │   └── PersonDataState.kt
│   │   │   │   │   │   └── confirmation/
│   │   │   │   │   │       ├── ConfirmationScreen.kt
│   │   │   │   │   │       ├── ConfirmationViewModel.kt
│   │   │   │   │   │       └── ConfirmationState.kt
│   │   │   │   │   ├── components/
│   │   │   │   │   │   ├── LoadingIndicator.kt
│   │   │   │   │   │   ├── ErrorMessage.kt
│   │   │   │   │   │   ├── PrimaryButton.kt
│   │   │   │   │   │   ├── SeatItem.kt
│   │   │   │   │   │   └── EventCard.kt
│   │   │   │   │   ├── navigation/
│   │   │   │   │   │   └── Navigation.kt
│   │   │   │   │   └── theme/
│   │   │   │   │       ├── Theme.kt
│   │   │   │   │       ├── Color.kt
│   │   │   │   │       └── Type.kt
│   │   │   │   ├── di/
│   │   │   │   │   └── AppModule.kt
│   │   │   │   └── App.kt
│   │   │   └── resources/
│   │   ├── androidMain/
│   │   │   ├── kotlin/com/evento/mobile/
│   │   │   │   └── MainActivity.kt
│   │   │   ├── AndroidManifest.xml
│   │   │   └── res/
│   │   └── iosMain/
│   │       └── kotlin/
│   └── build.gradle.kts
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── .gitignore
```

---

## 🔧 Tecnologías y Dependencias

### **Versiones Principales**

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Kotlin | 2.0.21 | Lenguaje principal |
| Compose Multiplatform | 1.7.1 | UI Framework |
| Ktor Client | 3.0.2 | Networking HTTP |
| Kotlinx Serialization | 1.7.3 | JSON parsing |
| Kotlinx Coroutines | 1.9.0 | Programación asíncrona |
| Navigation Compose | 2.8.0-alpha10 | Navegación type-safe |
| Lifecycle ViewModel | 2.8.7 | State management |
| Android Gradle Plugin | 8.7.3 | Build Android |

### **Dependencias por Módulo**

#### **commonMain** (Código compartido)
```kotlin
// UI
- compose.runtime
- compose.foundation
- compose.material3
- compose.ui
- compose.components.resources

// Networking
- ktor-client-core
- ktor-client-content-negotiation
- ktor-serialization-kotlinx-json
- ktor-client-logging
- ktor-client-auth

// Serialization
- kotlinx-serialization-json

// Coroutines
- kotlinx-coroutines-core

// Navigation
- navigation-compose

// ViewModel
- lifecycle-viewmodel-compose
```

#### **androidMain** (Android específico)
```kotlin
- compose.preview
- androidx.activity.compose
- ktor-client-okhttp
- kotlinx-coroutines-android
```

#### **iosMain** (iOS específico)
```kotlin
- ktor-client-darwin
```

---

## 🎨 Diseño de Pantallas

### **1. Login Screen**

**Ruta:** `LoginRoute`

**Elementos:**
- Campo de texto: Usuario
- Campo de texto: Contraseña (oculta)
- Botón: "Iniciar Sesión"
- Indicador de carga
- Mensaje de error

**Estados:**
```kotlin
data class LoginState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)
```

**Navegación:**
- ✅ Login exitoso → `EventListRoute`
- ❌ Error → Mostrar mensaje en pantalla

---

### **2. Event List Screen**

**Ruta:** `EventListRoute`

**Elementos:**
- AppBar con título "Eventos" y botón logout
- Lista scrolleable de eventos (LazyColumn)
- Card por evento mostrando:
  - Título
  - Tipo de evento
  - Fecha
  - Precio
  - Asientos disponibles
- Indicador de carga
- Mensaje de error
- Pull-to-refresh

**Estados:**
```kotlin
data class EventListState(
    val eventos: List<EventoDTO> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)
```

**Acciones:**
- Click en evento → `EventDetailRoute(eventId)`
- Pull-to-refresh → Recargar eventos
- Logout → `LoginRoute` (limpiar stack)

---

### **3. Event Detail Screen**

**Ruta:** `EventDetailRoute(eventId: Long)`

**Elementos:**
- AppBar con botón back
- Imagen del evento
- Título
- Descripción completa
- Fecha y hora
- Dirección
- Tipo de evento
- Integrantes/presentadores
- Precio por entrada
- Mapa de asientos (grid visual)
- Estadísticas:
  - Total de asientos
  - Disponibles
  - Bloqueados
  - Vendidos
- Botón: "Iniciar Compra"
- Indicador de carga

**Estados:**
```kotlin
data class EventDetailState(
    val evento: EventoDTO? = null,
    val disponibilidad: DisponibilidadDTO? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
```

**Acciones:**
- Click en "Iniciar Compra" → Iniciar sesión + navegar a `SeatSelectionRoute(eventId)`
- Back → `EventListRoute`

---

### **4. Seat Selection Screen**

**Ruta:** `SeatSelectionRoute(eventId: Long)`

**Elementos:**
- AppBar con botón back
- Título del evento
- Selector de cantidad (1-4 asientos)
- Mapa visual de asientos (Grid)
  - 🟢 Verde = Disponible (clickeable)
  - 🔴 Rojo = Vendido (disabled)
  - 🟡 Amarillo = Bloqueado por otro (disabled)
  - 🔵 Azul = Seleccionado por mí (clickeable para deseleccionar)
- Leyenda de colores
- Lista de asientos seleccionados
- Botón: "Continuar"
- Indicador de carga

**Estados:**
```kotlin
data class SeatSelectionState(
    val eventoId: Long,
    val sesionId: Long? = null,
    val disponibilidad: DisponibilidadDTO? = null,
    val asientosSeleccionados: List<AsientoDTO> = emptyList(),
    val cantidadMaxima: Int = 4,
    val isLoading: Boolean = false,
    val error: String? = null
)
```

**Acciones:**
- Click en asiento disponible → Seleccionar (si no excede máximo)
- Click en asiento seleccionado → Deseleccionar
- Click en "Continuar" → Bloquear asientos en backend + navegar a `PersonDataRoute`
- Back → Cancelar sesión + `EventDetailRoute`

**Validaciones:**
- Máximo 4 asientos
- Al menos 1 asiento seleccionado para continuar

---

### **5. Person Data Screen**

**Ruta:** `PersonDataRoute`

**Elementos:**
- AppBar con botón back
- Título: "Datos de los asientos"
- Lista de asientos seleccionados
- Por cada asiento:
  - Label: "Asiento Fila X, Columna Y"
  - Campo de texto: "Nombre completo"
- Botón: "Continuar a confirmación"
- Indicador de carga

**Estados:**
```kotlin
data class PersonDataState(
    val asientosSeleccionados: List<AsientoDTO> = emptyList(),
    val nombres: Map<String, String> = emptyMap(), // "fila-columna" -> "nombre"
    val isLoading: Boolean = false,
    val error: String? = null,
    val nombresCompletos: Boolean = false
)
```

**Acciones:**
- Cambiar nombre → Actualizar estado
- Click en "Continuar" → Enviar nombres al backend + navegar a `ConfirmationRoute`
- Back → `SeatSelectionRoute` (mantener sesión)

**Validaciones:**
- Todos los asientos deben tener nombre (mínimo 3 caracteres)
- No permitir continuar si faltan nombres

---

### **6. Confirmation Screen**

**Ruta:** `ConfirmationRoute`

**Elementos:**
- AppBar con botón back
- Título: "Confirmar Compra"
- Resumen de la compra:
  - Evento
  - Fecha
  - Asientos seleccionados (con nombres)
  - Precio por entrada
  - **Precio total**
- Botón: "Confirmar Compra"
- Indicador de carga

**Estados:**
```kotlin
data class ConfirmationState(
    val evento: EventoDTO? = null,
    val asientos: List<AsientoConNombreDTO> = emptyList(),
    val precioTotal: Double = 0.0,
    val isLoading: Boolean = false,
    val compraExitosa: Boolean = false,
    val ventaId: Long? = null,
    val error: String? = null
)

data class AsientoConNombreDTO(
    val fila: Int,
    val columna: Int,
    val nombre: String
)
```

**Acciones:**
- Click en "Confirmar Compra" → POST /api/compra/confirmar
- Compra exitosa → Mostrar diálogo de éxito + navegar a `EventListRoute` (limpiar stack)
- Error → Mostrar mensaje
- Back → `PersonDataRoute` (mantener sesión)

---

## 🔄 Flujo de Navegación Completo

```
┌─────────────┐
│   Login     │
│   Screen    │
└──────┬──────┘
       │ (login exitoso)
       ▼
┌─────────────┐
│ Event List  │◄──────────────┐
│   Screen    │               │
└──────┬──────┘               │
       │ (click evento)       │
       ▼                      │
┌─────────────┐               │
│Event Detail │               │
│   Screen    │               │
└──────┬──────┘               │
       │ (iniciar compra)     │
       ▼                      │
┌─────────────┐               │
│Seat Select. │               │
│   Screen    │               │
└──────┬──────┘               │
       │ (continuar)          │
       ▼                      │
┌─────────────┐               │
│Person Data  │               │
│   Screen    │               │
└──────┬──────┘               │
       │ (continuar)          │
       ▼                      │
┌─────────────┐               │
│Confirmation │               │
│   Screen    │               │
└──────┬──────┘               │
       │ (compra exitosa)     │
       └──────────────────────┘
```

---

## 🌐 Endpoints del Backend a Consumir

### **Autenticación**

#### POST `/api/authenticate`
**Request:**
```json
{
  "username": "admin",
  "password": "admin",
  "rememberMe": false
}
```

**Response:**
```json
{
  "id_token": "eyJhbGciOiJIUzUxMiJ9..."
}
```

---

### **Eventos**

#### GET `/api/eventos`
**Headers:** `Authorization: Bearer {token}`

**Response:**
```json
[
  {
    "id": 1054,
    "idCatedra": 4,
    "titulo": "Ciclo de Música Clásica Evento 2",
    "resumen": "...",
    "descripcion": "...",
    "fecha": "2026-01-30T20:00:00Z",
    "direccion": "...",
    "imagen": "https://...",
    "filaAsientos": 20,
    "columnaAsientos": 10,
    "precioEntrada": 9700.00,
    "activo": true,
    "eventoTipo": {
      "id": 1002,
      "nombre": "Concierto"
    }
  }
]
```

---

### **Disponibilidad de Asientos**

#### GET `/api/eventos/{eventoId}/asientos/disponibilidad`
**Headers:** `Authorization: Bearer {token}`

**Response:**
```json
{
  "eventoId": 1054,
  "eventoIdCatedra": 4,
  "tituloEvento": "...",
  "totalFilas": 20,
  "totalColumnas": 10,
  "totalAsientos": 200,
  "disponibles": 196,
  "bloqueados": 2,
  "vendidos": 2,
  "asientos": [
    {
      "fila": 1,
      "columna": 1,
      "estado": "DISPONIBLE",
      "expira": null,
      "nombrePersona": null
    }
  ],
  "consultadoEn": "2025-12-11T18:30:00Z"
}
```

---

### **Gestión de Sesión de Compra**

#### POST `/api/compra/iniciar`
**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "eventoId": 1054
}
```

**Response:**
```json
{
  "id": 1151,
  "estado": "SELECCION_ASIENTOS",
  "fechaInicio": "2025-12-11T18:36:55Z",
  "expiracion": "2025-12-11T19:06:55Z",
  "activa": true,
  "usuario": {
    "id": 1,
    "login": "admin"
  },
  "evento": {
    "id": 1054,
    "titulo": "..."
  }
}
```

---

#### GET `/api/compra/estado`
**Headers:** `Authorization: Bearer {token}`

**Response:** Same as `/api/compra/iniciar`

---

#### POST `/api/compra/seleccionar-asientos`
**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "asientos": [
    {"fila": 20, "columna": 9},
    {"fila": 20, "columna": 10}
  ]
}
```

**Response:**
```json
{
  "id": 1151,
  "estado": "CARGA_DATOS",
  ...
}
```

---

#### POST `/api/compra/asignar-nombres`
**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "nombres": {
    "20-9": "María García",
    "20-10": "Carlos López"
  }
}
```

**Response:**
```json
{
  "id": 1151,
  "estado": "CARGA_DATOS",
  ...
}
```

---

#### POST `/api/compra/confirmar`
**Headers:** `Authorization: Bearer {token}`

**Request:** (vacío)

**Response:**
```json
{
  "id": 1251,
  "idVentaCatedra": 1573,
  "fechaVenta": "2025-12-11T18:39:56Z",
  "precioTotal": 19400.00,
  "exitosa": true,
  "estadoSincronizacion": "SINCRONIZADA",
  "evento": {
    "id": 1054,
    "titulo": "..."
  },
  "usuario": {
    "id": 1,
    "login": "admin"
  }
}
```

---

#### POST `/api/compra/cancelar`
**Headers:** `Authorization: Bearer {token}`

**Response:** 204 No Content

---

## 📋 Plan de Implementación por Fases

### **FASE 1: Setup Inicial del Proyecto** ⏱️ 1-2 horas

#### Tareas:
1. ✅ Crear proyecto KMP con Android Studio Wizard
   - Nombre: EventoMobile
   - Paquete: com.evento.mobile
   - Ubicación: `/Mobile`
   - Plataformas: Android + iOS
   - UI: Compose Multiplatform

2. ✅ Configurar `gradle/libs.versions.toml`
   - Agregar todas las versiones de dependencias
   - Configurar plugins

3. ✅ Actualizar `composeApp/build.gradle.kts`
   - Agregar dependencias en commonMain
   - Agregar dependencias en androidMain
   - Agregar dependencias en iosMain
   - Configurar Android SDK (min: 26, target: 35)

4. ✅ Configurar `AndroidManifest.xml`
   - Agregar permiso INTERNET
   - Configurar usesCleartextTraffic para desarrollo

5. ✅ Crear estructura de paquetes en `commonMain`
   - data/model/
   - data/remote/
   - data/repository/
   - presentation/screens/
   - presentation/components/
   - presentation/navigation/
   - di/

6. ✅ Crear `.gitignore` para Mobile
   - Ignorar build/, .gradle/, .idea/, etc.

7. ✅ Sync Gradle y verificar build exitoso

**Entregables:**
- Proyecto KMP funcional
- Estructura de carpetas completa
- Build exitoso sin errores

---

### **FASE 2: Networking y Autenticación** ⏱️ 2-3 horas

#### Tareas:
1. ✅ Crear `NetworkConfig.kt`
   - Definir BASE_URL para emulador (10.0.2.2:8081)
   - Definir BASE_URL para dispositivo físico

2. ✅ Configurar HttpClient en `AppModule.kt`
   - Plugin: ContentNegotiation (JSON)
   - Plugin: Logging
   - Plugin: Auth (Bearer token)
   - Plugin: HttpTimeout
   - Configurar base URL

3. ✅ Crear DTOs de autenticación
   - `LoginRequest.kt`
   - `LoginResponse.kt`

4. ✅ Crear `AuthRepository.kt`
   - Método: `login(username, password): Result<LoginResponse>`
   - Método: `logout()`
   - Método: `isLoggedIn(): Boolean`
   - Guardar token en `AppModule.jwtToken`

5. ✅ Crear `LoginState.kt`
   - username, password, isLoading, error, isLoggedIn

6. ✅ Crear `LoginViewModel.kt`
   - StateFlow<LoginState>
   - onUsernameChange()
   - onPasswordChange()
   - onLoginClick()
   - Validaciones

7. ✅ Crear `LoginScreen.kt`
   - TextField para usuario
   - TextField para contraseña (oculta)
   - Button de login
   - Indicador de carga
   - Mensaje de error
   - LaunchedEffect para navegar al éxito

8. ✅ Crear navegación básica en `Navigation.kt`
   - Definir rutas: LoginRoute, EventListRoute
   - NavHost con pantalla de login

9. ✅ Actualizar `App.kt`
   - Llamar a AppNavigation()

10. ✅ Actualizar `MainActivity.kt`
    - Llamar a App()

**Testing:**
- Probar login con credenciales válidas (admin/admin)
- Probar login con credenciales inválidas
- Verificar manejo de errores
- Verificar que token se guarda correctamente

**Entregables:**
- Login funcional
- Token JWT guardado
- Navegación básica implementada

---

### **FASE 3: Lista de Eventos** ⏱️ 2-3 horas

#### Tareas:
1. ✅ Crear DTOs de eventos
   - `EventoDTO.kt`
   - `EventoTipoDTO.kt`
   - `IntegranteDTO.kt`

2. ✅ Crear `EventoRepository.kt`
   - Método: `getEventos(): Result<List<EventoDTO>>`
   - Método: `getEventoById(id): Result<EventoDTO>`

3. ✅ Crear `EventListState.kt`
   - eventos, isLoading, isRefreshing, error

4. ✅ Crear `EventListViewModel.kt`
   - StateFlow<EventListState>
   - loadEventos()
   - refreshEventos()
   - Manejar errores

5. ✅ Crear componente `EventCard.kt`
   - Mostrar título, tipo, fecha, precio
   - Clickeable

6. ✅ Crear `EventListScreen.kt`
   - AppBar con título y botón logout
   - LazyColumn con eventos
   - Pull-to-refresh
   - Indicador de carga
   - Mensaje de error
   - Empty state

7. ✅ Actualizar `Navigation.kt`
   - Agregar ruta EventListRoute
   - Configurar navegación desde Login

**Testing:**
- Verificar que carga eventos del backend
- Probar pull-to-refresh
- Probar navegación a detalle
- Probar logout

**Entregables:**
- Lista de eventos funcional
- Pull-to-refresh implementado
- Logout funcional

---

### **FASE 4: Detalle de Evento** ⏱️ 3-4 horas

#### Tareas:
1. ✅ Crear DTOs de disponibilidad
   - `DisponibilidadDTO.kt`
   - `AsientoDisponibilidadDTO.kt`

2. ✅ Crear `AsientoRepository.kt`
   - Método: `getDisponibilidad(eventoId): Result<DisponibilidadDTO>`

3. ✅ Crear `EventDetailState.kt`
   - evento, disponibilidad, isLoading, error

4. ✅ Crear `EventDetailViewModel.kt`
   - StateFlow<EventDetailState>
   - loadEventoDetalle(eventoId)
   - loadDisponibilidad(eventoId)

5. ✅ Crear componente `SeatGridPreview.kt`
   - Grid visual de asientos (solo lectura)
   - Colores según estado

6. ✅ Crear `EventDetailScreen.kt`
   - AppBar con back button
   - Imagen del evento
   - Información completa
   - Preview de mapa de asientos
   - Estadísticas de disponibilidad
   - Botón "Iniciar Compra"

7. ✅ Actualizar `Navigation.kt`
   - Agregar ruta EventDetailRoute(eventId)
   - Configurar navegación desde lista

**Testing:**
- Verificar que carga evento correcto
- Verificar que muestra disponibilidad
- Probar navegación back
- Probar botón "Iniciar Compra"

**Entregables:**
- Detalle de evento funcional
- Preview de asientos implementado
- Navegación completa

---

### **FASE 5: Sesión de Compra** ⏱️ 2 horas

#### Tareas:
1. ✅ Crear DTOs de sesión
   - `SesionDTO.kt`
   - `IniciarSesionRequest.kt`
   - `UsuarioDTO.kt`

2. ✅ Crear `SesionRepository.kt`
   - Método: `iniciarSesion(eventoId): Result<SesionDTO>`
   - Método: `obtenerEstado(): Result<SesionDTO?>`
   - Método: `cancelarSesion(): Result<Unit>`
   - Método: `actualizarActividad(): Result<Unit>`

3. ✅ Implementar lógica de sesión en ViewModels
   - Iniciar sesión antes de selección de asientos
   - Cancelar sesión al salir
   - Keep-alive periódico (opcional)

**Testing:**
- Verificar que crea sesión correctamente
- Verificar que cancela sesión al back
- Verificar estado de sesión

**Entregables:**
- Gestión de sesión implementada
- Integración con flujo de compra

---

### **FASE 6: Selección de Asientos** ⏱️ 4-5 horas

#### Tareas:
1. ✅ Crear DTOs de selección
   - `SeleccionarAsientosRequest.kt`
   - `AsientoDTO.kt`

2. ✅ Actualizar `SesionRepository.kt`
   - Método: `seleccionarAsientos(asientos): Result<SesionDTO>`

3. ✅ Crear `SeatSelectionState.kt`
   - eventoId, sesionId, disponibilidad
   - asientosSeleccionados (lista local)
   - cantidadMaxima (4)
   - isLoading, error

4. ✅ Crear `SeatSelectionViewModel.kt`
   - StateFlow<SeatSelectionState>
   - loadDisponibilidad()
   - toggleAsiento(fila, columna)
   - confirmarSeleccion() → Enviar al backend
   - Validaciones (max 4, mínimo 1)

5. ✅ Crear componente `SeatItem.kt`
   - Representación visual de un asiento
   - Estados: disponible, vendido, bloqueado, seleccionado
   - Colores según estado
   - Clickeable si disponible

6. ✅ Crear componente `SeatGrid.kt`
   - LazyVerticalGrid con asientos
   - Responsive según tamaño de pantalla

7. ✅ Crear componente `SeatLegend.kt`
   - Leyenda de colores

8. ✅ Crear `SeatSelectionScreen.kt`
   - AppBar con back (confirmar cancelación)
   - Título del evento
   - Selector de cantidad (opcional)
   - Grid de asientos
   - Leyenda
   - Lista de seleccionados
   - Botón "Continuar"

9. ✅ Actualizar `Navigation.kt`
   - Agregar ruta SeatSelectionRoute(eventoId)
   - Pasar datos necesarios

**Testing:**
- Probar selección de asientos
- Verificar límite de 4 asientos
- Probar deselección
- Verificar bloqueo en backend
- Probar navegación back (cancelar sesión)

**Entregables:**
- Selección de asientos funcional
- Grid visual implementado
- Validaciones correctas
- Bloqueo en backend funcionando

---

### **FASE 7: Asignación de Nombres** ⏱️ 2-3 horas

#### Tareas:
1. ✅ Crear DTOs
   - `AsignarNombresRequest.kt`

2. ✅ Actualizar `SesionRepository.kt`
   - Método: `asignarNombres(nombres): Result<SesionDTO>`

3. ✅ Crear `PersonDataState.kt`
   - asientosSeleccionados
   - nombres (Map<String, String>)
   - isLoading, error
   - nombresCompletos (validación)

4. ✅ Crear `PersonDataViewModel.kt`
   - StateFlow<PersonDataState>
   - onNombreChange(asientoKey, nombre)
   - confirmarNombres() → Enviar al backend
   - Validación (min 3 caracteres, todos completos)

5. ✅ Crear `PersonDataScreen.kt`
   - AppBar con back
   - Lista de asientos con TextField para nombre
   - Validación visual
   - Botón "Continuar"

6. ✅ Actualizar `Navigation.kt`
   - Agregar ruta PersonDataRoute
   - Pasar asientos seleccionados

**Testing:**
- Probar ingreso de nombres
- Verificar validación de 3 caracteres
- Verificar que no permite continuar sin nombres completos
- Probar navegación back

**Entregables:**
- Asignación de nombres funcional
- Validaciones correctas
- Integración con backend

---

### **FASE 8: Confirmación y Compra** ⏱️ 3-4 horas

#### Tareas:
1. ✅ Crear DTOs
   - `VentaDTO.kt`
   - `AsientoVendidoDTO.kt`

2. ✅ Actualizar `SesionRepository.kt`
   - Método: `confirmarCompra(): Result<VentaDTO>`

3. ✅ Crear `ConfirmationState.kt`
   - evento, asientos (con nombres)
   - precioTotal
   - isLoading, error
   - compraExitosa, ventaId

4. ✅ Crear `ConfirmationViewModel.kt`
   - StateFlow<ConfirmationState>
   - loadResumen()
   - confirmarCompra() → POST /api/compra/confirmar
   - calcularPrecioTotal()

5. ✅ Crear componente `PurchaseSummary.kt`
   - Resumen visual de la compra

6. ✅ Crear `ConfirmationScreen.kt`
   - AppBar con back
   - Resumen de compra
   - Precio total destacado
   - Botón "Confirmar Compra"
   - Diálogo de éxito
   - Diálogo de error

7. ✅ Actualizar `Navigation.kt`
   - Agregar ruta ConfirmationRoute
   - Configurar navegación post-compra

8. ✅ Implementar lógica de post-compra
   - Mostrar diálogo de éxito
   - Limpiar sesión
   - Navegar a lista de eventos (limpiar stack)

**Testing:**
- Probar confirmación exitosa
- Verificar cálculo de precio total
- Probar manejo de errores (asiento vendido por otro)
- Verificar navegación post-compra
- Verificar que sesión se limpia

**Entregables:**
- Flujo completo de compra funcional
- Confirmación implementada
- Manejo de éxitos y errores
- Navegación correcta

---

### **FASE 9: Mejoras de UX y Manejo de Estados** ⏱️ 2-3 horas

#### Tareas:
1. ✅ Implementar persistencia de sesión
   - Guardar estado en local storage
   - Recuperar sesión al reiniciar app

2. ✅ Implementar keep-alive automático
   - Timer para actualizar actividad cada X minutos

3. ✅ Mejorar manejo de errores
   - Mensajes descriptivos
   - Retry automático para network errors

4. ✅ Implementar estados de carga globales
   - Overlay de carga
   - Skeleton loaders

5. ✅ Agregar animaciones
   - Transiciones de pantalla
   - Animación de selección de asientos

6. ✅ Implementar pull-to-refresh en todas las listas

7. ✅ Agregar diálogos de confirmación
   - Al cancelar sesión
   - Al logout
   - Al volver atrás en flujo de compra

**Testing:**
- Probar recuperación de sesión
- Verificar keep-alive
- Probar todos los flujos de error
- Verificar animaciones

**Entregables:**
- UX mejorada
- Manejo robusto de estados
- Persistencia funcional

---

### **FASE 10: Testing y Debugging** ⏱️ 3-4 horas

#### Tareas:
1. ✅ Testing en emulador
   - Probar flujo completo de compra
   - Probar casos de error
   - Probar navegación

2. ✅ Testing en dispositivo físico
   - Configurar BASE_URL con IP de red
   - Probar flujo completo

3. ✅ Testing de casos extremos
   - Sin conexión a internet
   - Backend caído
   - Sesión expirada
   - Asientos vendidos por otro usuario
   - Múltiples selecciones/deselecciones

4. ✅ Debugging y fixes
   - Revisar logs
   - Corregir bugs encontrados
   - Optimizar rendimiento

5. ✅ Testing de sesión multi-dispositivo
   - Iniciar sesión en emulador
   - Continuar en dispositivo físico
   - Verificar sincronización

**Entregables:**
- App estable y testeada
- Bugs corregidos
- Performance optimizada

---

### **FASE 11: Documentación y Deployment** ⏱️ 2-3 horas

#### Tareas:
1. ✅ Documentar código
   - KDoc en clases principales
   - Comments en lógica compleja

2. ✅ Crear README.md del proyecto Mobile
   - Instrucciones de setup
   - Cómo correr la app
   - Configuración de backend

3. ✅ Generar APK de release
   - Configurar signing
   - Build release

4. ✅ Testing del APK release
   - Instalar en dispositivo
   - Probar flujo completo

5. ✅ Crear guía de usuario (opcional)
   - Screenshots de cada pantalla
   - Flujo paso a paso

**Entregables:**
- Código documentado
- README completo
- APK funcional
- Proyecto finalizado

---

## 🧪 Plan de Testing

### **Testing Manual por Pantalla**

#### **Login Screen**
- [ ] Login exitoso con credenciales válidas
- [ ] Login fallido con credenciales inválidas
- [ ] Mostrar error si campos vacíos
- [ ] Mostrar loading spinner durante request
- [ ] Manejo de error de red
- [ ] Navegación correcta post-login

#### **Event List Screen**
- [ ] Carga de eventos exitosa
- [ ] Mostrar loading durante carga
- [ ] Mostrar error si falla request
- [ ] Pull-to-refresh funcional
- [ ] Click en evento navega a detalle
- [ ] Logout funcional
- [ ] Empty state si no hay eventos

#### **Event Detail Screen**
- [ ] Carga de evento correcto
- [ ] Carga de disponibilidad
- [ ] Preview de asientos visible
- [ ] Estadísticas correctas
- [ ] Botón "Iniciar Compra" funcional
- [ ] Back button funcional

#### **Seat Selection Screen**
- [ ] Sesión se crea correctamente
- [ ] Grid de asientos se renderiza
- [ ] Selección de asiento funcional
- [ ] Deselección de asiento funcional
- [ ] Límite de 4 asientos respetado
- [ ] Estados visuales correctos (colores)
- [ ] Bloqueo en backend exitoso
- [ ] Continuar solo con asientos seleccionados
- [ ] Cancelar sesión al back

#### **Person Data Screen**
- [ ] Lista de asientos seleccionados
- [ ] Ingreso de nombres funcional
- [ ] Validación de 3 caracteres
- [ ] No permite continuar sin nombres completos
- [ ] Envío al backend exitoso
- [ ] Back mantiene sesión

#### **Confirmation Screen**
- [ ] Resumen correcto de compra
- [ ] Cálculo de precio total correcto
- [ ] Confirmación exitosa
- [ ] Diálogo de éxito visible
- [ ] Navegación post-compra correcta
- [ ] Manejo de error de compra
- [ ] Back mantiene sesión

### **Testing de Casos Extremos**
- [ ] Sin conexión a internet
- [ ] Backend caído
- [ ] Sesión expirada durante flujo
- [ ] Asiento vendido por otro entre selección y confirmación
- [ ] Token JWT expirado
- [ ] Múltiples clicks rápidos en botones
- [ ] Rotación de pantalla (conservar estado)

### **Testing Multi-Dispositivo**
- [ ] Iniciar sesión en dispositivo A
- [ ] Continuar sesión en dispositivo B
- [ ] Sincronización correcta de estado

---

## 📊 Estimación de Tiempo Total

| Fase | Tiempo Estimado |
|------|-----------------|
| 1. Setup Inicial | 1-2 horas |
| 2. Networking y Autenticación | 2-3 horas |
| 3. Lista de Eventos | 2-3 horas |
| 4. Detalle de Evento | 3-4 horas |
| 5. Sesión de Compra | 2 horas |
| 6. Selección de Asientos | 4-5 horas |
| 7. Asignación de Nombres | 2-3 horas |
| 8. Confirmación y Compra | 3-4 horas |
| 9. Mejoras de UX | 2-3 horas |
| 10. Testing y Debugging | 3-4 horas |
| 11. Documentación | 2-3 horas |
| **TOTAL** | **26-36 horas** |

**Estimación realista:** ~30 horas de trabajo concentrado (~1 semana de trabajo full-time o 2-3 semanas part-time)

---

## 🎯 Criterios de Éxito

El proyecto estará completo cuando:

1. ✅ **Autenticación funcional**
   - Usuario puede hacer login
   - Token JWT se guarda y usa en requests
   - Logout funciona correctamente

2. ✅ **Navegación fluida**
   - Todas las pantallas accesibles
   - Back navigation correcta
   - Deep linking funcional (opcional)

3. ✅ **Flujo de compra completo**
   - Iniciar sesión de compra
   - Seleccionar asientos (1-4)
   - Asignar nombres
   - Confirmar compra
   - Ver confirmación exitosa

4. ✅ **Sincronización con backend**
   - Todos los endpoints integrados
   - Estados sincronizados (asientos bloqueados, vendidos)
   - Manejo correcto de errores del backend

5. ✅ **UX de calidad**
   - Loading states
   - Error messages
   - Empty states
   - Validaciones en tiempo real
   - Feedback visual inmediato

6. ✅ **Testing completo**
   - Casos happy path funcionan
   - Casos de error manejados
   - App estable en emulador y dispositivo

7. ✅ **Código limpio**
   - Arquitectura MVVM respetada
   - Separación de concerns
   - Código documentado
   - Sin warnings críticos

---

## 🚀 Quick Start

### **Prerrequisitos**

```bash
# Backend corriendo
cd Backend
./mvnw spring-boot:run
# Verificar: http://localhost:8081/management/health

# Android Studio instalado
# Android SDK configurado
# Emulador Android creado
```

### **Crear Proyecto**

1. Abrir Android Studio
2. New Project → Kotlin Multiplatform App
3. Configurar:
   - Name: EventoMobile
   - Package: com.evento.mobile
   - Location: .../Mobile
   - Min SDK: API 26
   - Targets: Android + iOS
   - Share UI: Yes (Compose Multiplatform)
4. Finish

### **Primera Ejecución**

```bash
# Navegar al proyecto
cd Mobile

# Sync Gradle
./gradlew build

# Ejecutar en emulador (desde Android Studio)
# Run → Run 'composeApp'
```

### **Configurar Backend URL**

Editar `NetworkConfig.kt`:
```kotlin
// Para emulador
const val BASE_URL = "http://10.0.2.2:8081"

// Para dispositivo físico (reemplazar con tu IP)
const val BASE_URL = "http://192.168.1.XXX:8081"
```

---

## 📝 Notas Importantes

### **Consideraciones de Desarrollo**

1. **URL del Backend:**
   - Emulador Android: `10.0.2.2` mapea a `localhost` del host
   - Dispositivo físico: Usar IP de la máquina en la red local
   - `usesCleartextTraffic=true` solo para desarrollo

2. **Token JWT:**
   - Se guarda en memoria (`AppModule.jwtToken`)
   - Para producción, usar almacenamiento persistente encriptado
   - Implementar refresh token si es necesario

3. **Sesión de Compra:**
   - Expira en 30 minutos de inactividad
   - Implementar keep-alive si se requiere más tiempo
   - Cancelar sesión al salir del flujo

4. **Estados de Asientos:**
   - `DISPONIBLE` → Verde (clickeable)
   - `VENDIDO` → Rojo (disabled)
   - `BLOQUEADO` → Amarillo (disabled)
   - `SELECCIONADO` → Azul (clickeable para deseleccionar)

5. **Límites:**
   - Máximo 4 asientos por compra
   - Mínimo 3 caracteres por nombre
   - Timeout HTTP: 30 segundos

6. **Sincronización:**
   - Refrescar disponibilidad antes de confirmar
   - Manejar caso donde asiento fue vendido por otro
   - Retry automático en caso de error de red

---

## 🔗 Referencias

### **Documentación Oficial**

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Ktor Client](https://ktor.io/docs/client.html)
- [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)
- [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)

### **Backend del Proyecto**

- Endpoints: Ver `/GUIA_COMPRA.md`
- Testing: Ver `/TESTING_DISPONIBILIDAD_ASIENTOS.md`
- Sincronización: Ver `/TESTING_SINCRONIZACION.md`

---

## ✅ Checklist Final

Antes de considerar el proyecto terminado:

### **Funcionalidad**
- [ ] Login funcional
- [ ] Lista de eventos carga correctamente
- [ ] Detalle de evento muestra toda la información
- [ ] Selección de asientos funciona (1-4)
- [ ] Asignación de nombres valida correctamente
- [ ] Confirmación de compra exitosa
- [ ] Sesión se sincroniza entre dispositivos
- [ ] Logout funciona

### **Calidad**
- [ ] Sin crashes en flujo normal
- [ ] Manejo de errores implementado
- [ ] Loading states en todas las pantallas
- [ ] Validaciones en tiempo real
- [ ] Navegación intuitiva
- [ ] UI responsiva

### **Integración**
- [ ] Todos los endpoints del backend integrados
- [ ] JWT token funciona correctamente
- [ ] Estados sincronizados con backend
- [ ] Errores del backend manejados

### **Testing**
- [ ] Testeado en emulador
- [ ] Testeado en dispositivo físico
- [ ] Casos de error probados
- [ ] Flujo completo verificado

### **Documentación**
- [ ] README.md creado
- [ ] Código comentado
- [ ] Instrucciones de setup claras

---

## 🎉 Conclusión

Este plan proporciona una guía completa y detallada para implementar el frontend móvil del sistema de venta de asientos para eventos. Siguiendo las fases en orden y completando cada checklist, obtendrás una aplicación móvil profesional, funcional y escalable que cumple con todos los requisitos del enunciado del trabajo final.

**Próximos pasos:**
1. Revisar este plan completo
2. Hacer preguntas si algo no está claro
3. Comenzar con Fase 1: Setup Inicial
4. Avanzar fase por fase, testeando en cada paso

¡Éxito con la implementación! 🚀
