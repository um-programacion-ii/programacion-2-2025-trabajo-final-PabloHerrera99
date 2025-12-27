# 🎟️ Sistema de Compra de Entradas para Eventos

> **Trabajo Final - Programación 2 - 2025**  
> Sistema completo de gestión y compra de entradas para eventos.
---
## Alumno

- **Autor**: Pablo Herrera
- **Legajo**: 60082

---

## 📋 Tabla de Contenidos

- [Descripción del Proyecto](#-descripción-del-proyecto)
- [Características Implementadas](#-características-implementadas)
- [Arquitectura del Sistema](#️-arquitectura-del-sistema)
- [Tecnologías Utilizadas](#-tecnologías-utilizadas)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación y Configuración](#-instalación-y-configuración)
  - [Backend (Spring Boot)](#1-backend-spring-boot)
  - [Mobile (Kotlin Multiplatform)](#2-mobile-kotlin-multiplatform)
- [Ejecución del Proyecto](#-ejecución-del-proyecto)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Endpoints Principales del API](#-endpoints-principales-del-api)
- [Flujo de Usuario (Mobile)](#-flujo-de-usuario-mobile)
- [Funcionalidades Detalladas](#-funcionalidades-detalladas)
- [Limitaciones Conocidas](#-limitaciones-conocidas)
- [Trabajo Futuro](#-trabajo-futuro)
- [Testing](#-testing)
- [Documentación Adicional](#-documentación-adicional)
- [Créditos](#-créditos)
- [Licencia](#-licencia)

---

## 📖 Descripción del Proyecto

Este proyecto implementa un **sistema completo de venta de entradas para eventos únicos** 
con arquitectura distribuida. El sistema permite a los usuarios:

- 🔐 **Autenticarse** en la aplicación móvil
- 📅 **Visualizar eventos** disponibles con información detallada
- 🪑 **Seleccionar asientos** de forma interactiva (hasta 4 por compra)
- ✍️ **Asignar nombres** a cada entrada comprada
- 💳 **Confirmar compras** con sincronización en tiempo real
---

## ✅ Características Implementadas

### Backend (Spring Boot + JHipster)

✔️ **Autenticación JWT**: Login seguro con tokens  
✔️ **Gestión de Eventos**: Sincronización con servidor de cátedra vía Kafka  
✔️ **Sincronización de Asientos**: Integración con Redis de cátedra para disponibilidad en tiempo real  
✔️ **Sesiones de Compra**: Manejo de sesiones concurrentes con Redis local  
✔️ **Proceso de Compra Completo**:
  - Creación de sesión
  - Selección de asientos (1-4)
  - Bloqueo temporal (5 minutos)
  - Asignación de nombres
  - Confirmación y sincronización con cátedra  
✔️ **Persistencia**: Almacenamiento local de eventos, ventas y asientos vendidos  
✔️ **API RESTful**: Endpoints documentados con Swagger/OpenAPI

### Frontend Mobile (Kotlin Multiplatform + Compose)

✔️ **6 Pantallas Completas**:
  1. **LoginScreen**: Autenticación con JWT
  2. **EventListScreen**: Listado de eventos con scroll infinito y paginación
  3. **EventDetailScreen**: Detalles del evento y contador de asientos disponibles
  4. **SeatSelectionScreen**: Grilla interactiva de selección de asientos
  5. **TicketAssignmentScreen**: Formulario de asignación de nombres con timer de 5 minutos
  6. **PurchaseConfirmationScreen**: Confirmación de compra exitosa

✔️ **Arquitectura MVVM**: Separación de responsabilidades (UI, ViewModel, Repository)  
✔️ **Networking con Ktor**: Cliente HTTP multiplataforma  
✔️ **Navegación con Compose Navigation**: Flujo completo de usuario  
✔️ **Material Design 3**: UI moderna y consistente  
✔️ **Validaciones en tiempo real**: Máximo de asientos, nombres obligatorios, timeouts

### Integraciones Externas

✔️ **API Cátedra**: Consumo de endpoints para eventos, bloqueos y ventas  
✔️ **Redis Cátedra**: Consulta de disponibilidad de asientos en tiempo real  
✔️ **Kafka**: Sincronización de cambios en eventos (vía Proxy)  
✔️ **ZeroTier VPN**: Conexión segura a servicios de cátedra

---

### Descripción de Componentes

#### 1. Cliente Móvil (Mobile)
- **Tecnología**: Kotlin Multiplatform, Compose Multiplatform
- **Responsabilidad**: Interfaz de usuario, validaciones cliente, comunicación con Backend
- **Plataforma**: Android (iOS preparado pero no implementado)

#### 2. Backend (Backend)
- **Tecnología**: Spring Boot 3.4.5, JHipster 8.11.0, Java 21
- **Responsabilidad**:
  - Autenticación de usuarios
  - Gestión de sesiones de compra
  - Sincronización de eventos con cátedra
  - Bloqueo y venta de asientos
  - Persistencia local de datos
- **Base de Datos**: PostgreSQL (producción), H2 (desarrollo)
- **Caché**: Redis (sesiones)

#### 3. Proxy (Proxy)
- **Tecnología**: Spring Boot
- **Responsabilidad**:
  - Consumir mensajes de Kafka (cambios en eventos)
  - Consultar Redis de cátedra (disponibilidad de asientos)
  - Intermediario entre Backend y servicios externos
- **Acceso**: Único componente con acceso a Kafka y Redis cátedra

#### 4. Servicios Cátedra (Externos)
- **API Cátedra**: Endpoints para eventos, bloqueos, ventas
- **Kafka**: Cola de mensajes con notificaciones de cambios
- **Redis**: Estado actualizado de asientos por evento
- **Acceso**: Vía ZeroTier VPN

---

### Verificación de Instalación

```bash
# Verificar Java
java -version
# Debe mostrar: openjdk version "17.x.x" o superior

# Verificar Maven
mvn -version
# Debe mostrar: Apache Maven 3.x.x

# Verificar Node.js
node --version
# Debe mostrar: v20.x.x o superior

# Verificar Git
git --version
# Debe mostrar: git version 2.x.x

# Verificar PostgreSQL
psql --version
# Debe mostrar: psql (PostgreSQL) 16.x
```

---

## 🔧 Instalación y Configuración

### 1. Backend (Spring Boot)

#### Paso 1.1: Clonar el Repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
cd programacion-2-2025-trabajo-final-PabloHerrera99
```

#### Paso 1.2: Configurar Base de Datos

**PostgreSQL (Producción)**

1. Crear base de datos:

```bash
# Conectar a PostgreSQL
psql -U postgres

# Crear base de datos
CREATE DATABASE evento_db;

# Crear usuario (opcional)
CREATE USER evento_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE evento_db TO evento_user;

# Salir
\q
```

2. Configurar credenciales en `Backend/src/main/resources/config/application-dev.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/evento_db
    username: evento_user
    password: your_password
```

#### Paso 1.3: Configurar Redis (Sesiones)

**Opción A: Docker (Recomendado)**

```bash
cd Backend
docker compose -f src/main/docker/redis.yml up -d
```

#### Paso 1.4: Configurar Conexión a API Cátedra

Editar `Backend/src/main/resources/config/application.yml`:

```yaml
application:
  catedra:
    api-url: http://192.168.194.250:8080  # IP del servidor cátedra (vía ZeroTier)
    username: tu_usuario_catedra
    password: tu_password_catedra
```

> **Nota**: Asegúrate de estar conectado a la VPN ZeroTier antes de ejecutar el backend.

#### Paso 1.5: Compilar Backend

```bash
cd Backend

# Primera compilación (descarga dependencias)
./mvnw clean install -DskipTests

# Debe terminar con: BUILD SUCCESS
```
---

### 2. Mobile (Kotlin Multiplatform)

#### Paso 2.1: Abrir Proyecto en Android Studio

1. Abrir Android Studio
2. **File → Open**
3. Navegar a la carpeta `Mobile`
4. Click en **OK**
5. Esperar a que Gradle sincronice (puede tardar 5-10 minutos la primera vez)

#### Paso 2.2: Configurar SDK de Android

1. **File → Project Structure → SDK Location**
2. Verificar que **Android SDK Location** esté configurado (ejemplo: `C:\Users\Usuario\AppData\Local\Android\Sdk`)
3. **Tools → SDK Manager**
4. En la pestaña **SDK Platforms**, instalar:
   - ✅ Android 13.0 (API 33) o superior
   - ✅ Android 14.0 (API 34)
5. En la pestaña **SDK Tools**, instalar:
   - ✅ Android SDK Build-Tools
   - ✅ Android Emulator
   - ✅ Android SDK Platform-Tools

#### Paso 2.3: Configurar URL del Backend

Editar `Mobile/composeApp/src/commonMain/kotlin/com/evento/mobile/data/remote/Endpoints.kt`:

```kotlin
object Endpoints {
    // Para emulador Android (localhost del host = 10.0.2.2)
    const val BASE_URL = "http://10.0.2.2:8081"
    
    // Para dispositivo físico en la misma red:
    // const val BASE_URL = "http://192.168.1.XXX:8081"  // Reemplazar con IP local de tu PC
}
```

**Obtener IP local de tu PC:**

- **Linux/Mac**: `ip addr show` o `ifconfig`
- **Windows**: `ipconfig`

Buscar la IP en la sección `inet` (Linux/Mac) o `IPv4 Address` (Windows) de tu adaptador de red activo.

#### Paso 2.4: Crear Emulador Android (Opcional)

Si no tienes un dispositivo físico:

1. **Tools → Device Manager**
2. Click en **Create Device**
3. Seleccionar: **Pixel 5** (o cualquier dispositivo moderno)
4. Seleccionar System Image: **API 33** (Android 13) - Descargar si es necesario
5. Click en **Finish**

#### Paso 2.5: Compilar Mobile

**Opción A: Desde Android Studio**
- Click en el botón **▶ Run** en la barra superior
- Seleccionar el emulador o dispositivo conectado
---

## 🚀 Ejecución del Proyecto

### Orden de Inicio de Servicios

#### 2. Iniciar Backend


```bash
cd Backend
./mvnw spring-boot:run
```

**Verificar Backend:**

```bash
# Health check
curl http://localhost:8081/management/health

# Debe responder:
# {"status":"UP"}
```

#### 3. Iniciar Proxy (Opcional - solo si usas Kafka)

```bash
cd Proxy
./mvnw spring-boot:run
```

#### 4. Ejecutar Mobile

**Opción A: Desde Android Studio**
- Asegurarse de que el backend esté corriendo
- Click en **▶ Run**
- Esperar a que la app se instale y abra en el emulador

**Opción B: Desde Terminal**

```bash
cd Mobile
./gradlew :composeApp:installDebug

# Abrir app manualmente en el dispositivo
```

### Credenciales de Prueba

Usuario por defecto (JHipster):

```
Username: admin
Password: admin
```

---

## 📂 Estructura del Proyecto

### Backend

```
Backend/
├── src/
│   ├── main/
│   │   ├── java/com/evento/backend/
│   │   │   ├── config/               # Configuraciones (Security, Redis, etc.)
│   │   │   ├── domain/               # Entidades JPA
│   │   │   │   ├── Evento.java
│   │   │   │   ├── Venta.java
│   │   │   │   ├── AsientoVendido.java
│   │   │   │   ├── Sesion.java
│   │   │   │   └── ...
│   │   │   ├── repository/           # Repositorios Spring Data JPA
│   │   │   ├── service/              # Lógica de negocio
│   │   │   │   ├── dto/              # DTOs (Data Transfer Objects)
│   │   │   │   ├── mapper/           # Mappers (Entity ↔ DTO)
│   │   │   │   ├── EventoService.java
│   │   │   │   ├── CompraService.java
│   │   │   │   └── ...
│   │   │   └── web/rest/             # Controllers REST
│   │   │       ├── EventoResource.java
│   │   │       ├── CompraResource.java
│   │   │       └── ...
│   │   └── resources/
│   │       ├── config/
│   │       │   ├── application.yml           # Configuración principal
│   │       │   ├── application-dev.yml       # Perfil desarrollo
│   │       │   └── liquibase/                # Scripts de BD
│   │       └── logback-spring.xml
│   └── test/                         # Tests unitarios e integración
├── pom.xml                           # Dependencias Maven
└── README.md
```

### Mobile

```
Mobile/
├── composeApp/
│   └── src/
│       ├── androidMain/              # Código específico Android
│       ├── commonMain/               # Código compartido
│       │   └── kotlin/com/evento/mobile/
│       │       ├── data/
│       │       │   ├── model/        # Modelos de datos
│       │       │   │   ├── auth/
│       │       │   │   ├── event/
│       │       │   │   └── purchase/
│       │       │   ├── remote/       # API Services
│       │       │   │   ├── AuthApiService.kt
│       │       │   │   ├── EventApiService.kt
│       │       │   │   └── PurchaseApiService.kt
│       │       │   └── repository/   # Repositorios
│       │       ├── presentation/
│       │       │   └── screens/      # Pantallas de la app
│       │       │       ├── login/
│       │       │       │   ├── LoginScreen.kt
│       │       │       │   ├── LoginViewModel.kt
│       │       │       │   └── LoginUiState.kt
│       │       │       ├── events/
│       │       │       ├── detail/
│       │       │       ├── seats/
│       │       │       ├── assignment/
│       │       │       └── confirmation/
│       │       ├── navigation/       # Navegación
│       │       │   ├── Screen.kt
│       │       │   └── AppNavigation.kt
│       │       └── App.kt
│       ├── iosMain/                  # Código específico iOS (preparado)
│       └── commonTest/               # Tests compartidos
├── gradle/
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 🌐 Endpoints Principales del API

### Autenticación

#### `POST /api/authenticate`
Autenticación de usuario

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

### Eventos

#### `GET /api/eventos?page=0&size=20`
Lista paginada de eventos

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
[
  {
    "id": 1,
    "idCatedra": 4,
    "titulo": "Conferencia de Tecnología",
    "resumen": "Charla sobre IA y Machine Learning",
    "descripcion": "Descripción completa del evento...",
    "fecha": "2025-11-10T11:00:00Z",
    "direccion": "Aula Magna - Universidad de Mendoza",
    "precioUnitario": 2500.00,
    "filaAsientos": 10,
    "columnaAsientos": 20,
    "activo": true,
    "eventoTipo": {
      "id": 1,
      "nombre": "Conferencia"
    }
  }
]
```

#### `GET /api/eventos/{id}`
Detalle completo de un evento

**Response:** Igual al anterior pero un solo objeto.

#### `GET /api/eventos/{id}/asientos/disponibilidad`
Disponibilidad de asientos en tiempo real

**Response:**
```json
{
  "eventoId": 1,
  "totalAsientos": 200,
  "disponibles": 145,
  "ocupados": 35,
  "bloqueados": 20,
  "asientos": [
    {
      "fila": 1,
      "columna": 1,
      "estado": "DISPONIBLE"
    },
    {
      "fila": 1,
      "columna": 2,
      "estado": "OCUPADO"
    }
  ]
}
```

---

### Proceso de Compra

#### `POST /api/compra/iniciar`
Iniciar sesión de compra

**Request:**
```json
{
  "eventoId": 1
}
```

**Response:**
```json
{
  "id": 123,
  "eventoId": 1,
  "estado": "SELECCION_ASIENTOS",
  "fechaInicio": "2025-12-16T10:00:00Z",
  "fechaExpiracion": "2025-12-16T10:30:00Z",
  "activa": true
}
```

#### `POST /api/compra/seleccionar-asientos`
Seleccionar asientos (máx. 4)

**Request:**
```json
{
  "asientos": [
    {"fila": 5, "columna": 10},
    {"fila": 5, "columna": 11}
  ]
}
```

**Response:**
```json
{
  "id": 123,
  "estado": "CARGA_DATOS",
  "asientosSeleccionados": [
    {"fila": 5, "columna": 10},
    {"fila": 5, "columna": 11}
  ],
  "tiempoRestante": 300
}
```

#### `POST /api/compra/asignar-nombres`
Asignar nombres a asientos

**Request:**
```json
{
  "nombres": {
    "5-10": "Juan Pérez",
    "5-11": "María García"
  }
}
```

**Response:**
```json
{
  "id": 123,
  "estado": "CARGA_DATOS",
  "listo": true
}
```

#### `POST /api/compra/confirmar`
Confirmar compra

**Response:**
```json
{
  "id": 456,
  "idVentaCatedra": 1506,
  "fechaVenta": "2025-12-16T10:05:00Z",
  "precioTotal": 5000.00,
  "exitosa": true,
  "descripcion": "Venta realizada con éxito",
  "estadoSincronizacion": "SINCRONIZADA",
  "evento": {
    "id": 1,
    "titulo": "Conferencia de Tecnología"
  }
}
```

---

### Ventas

#### `GET /api/ventas`
Lista de ventas del usuario autenticado

**Response:**
```json
[
  {
    "id": 456,
    "fechaVenta": "2025-12-16T10:05:00Z",
    "precioTotal": 5000.00,
    "exitosa": true,
    "estadoSincronizacion": "SINCRONIZADA"
  }
]
```

#### `GET /api/ventas/{id}`
Detalle de una venta

**Response:**
```json
{
  "id": 456,
  "idVentaCatedra": 1506,
  "fechaVenta": "2025-12-16T10:05:00Z",
  "precioTotal": 5000.00,
  "exitosa": true,
  "descripcion": "Venta realizada con éxito",
  "estadoSincronizacion": "SINCRONIZADA",
  "evento": {
    "id": 1,
    "titulo": "Conferencia de Tecnología"
  }
}
```

---

## 📱 Flujo de Usuario (Mobile)

### Diagrama de Navegación

```
┌─────────────────┐
│  LoginScreen    │
│  - Username     │
│  - Password     │
│  - Botón Login  │
└────────┬────────┘
         │ Autenticación exitosa (JWT)
         ↓
┌─────────────────┐
│ EventListScreen │
│  - Lista de     │
│    eventos      │
│  - Paginación   │
│  - Pull refresh │
└────────┬────────┘
         │ Click en evento
         ↓
┌──────────────────┐
│EventDetailScreen │
│  - Detalles      │
│  - Asientos      │
│    disponibles   │
│  - Botón Comprar │
└────────┬─────────┘
         │ Iniciar compra
         ↓
┌──────────────────┐
│SeatSelectionScrn │
│  - Grilla de     │
│    asientos      │
│  - Selección     │
│    visual        │
│  - Contador 1-4  │
└────────┬─────────┘
         │ Continuar (asientos bloqueados)
         ↓
┌──────────────────┐
│TicketAssignment │
│  - Formulario    │
│    nombres       │
│  - Timer 5 min   │
│  - Validación    │
└────────┬─────────┘
         │ Confirmar compra
         ↓
┌──────────────────┐
│PurchaseConfirm   │
│  - Mensaje       │
│    éxito         │
│  - Botón volver  │
└──────────────────┘
```

### Descripción de Pantallas

#### 1️⃣ LoginScreen
- **Propósito**: Autenticación del usuario
- **Componentes**:
  - Campo de texto: Username
  - Campo de texto: Password (oculto)
  - Botón: "Iniciar Sesión"
  - Indicador de carga durante autenticación
- **Validaciones**:
  - Campos obligatorios
  - Credenciales válidas (verificadas por backend)
- **Navegación**: Al autenticarse correctamente → EventListScreen

#### 2️⃣ EventListScreen
- **Propósito**: Mostrar lista de eventos disponibles
- **Componentes**:
  - LazyColumn con cards de eventos
  - Cada card muestra:
    - Título del evento
    - Fecha y hora
    - Tipo de evento
    - Precio de entrada
    - Asientos totales
  - Pull-to-refresh
  - Paginación automática (scroll infinito)
- **Navegación**: Click en evento → EventDetailScreen

#### 3️⃣ EventDetailScreen
- **Propósito**: Mostrar detalles completos del evento
- **Componentes**:
  - Información completa del evento:
    - Título, resumen, descripción
    - Fecha, hora, dirección
    - Precio por entrada
    - Organizadores/presentadores
  - **Contador de asientos disponibles en tiempo real**
  - Botón: "Comprar Entradas"
  - Botón: "Volver"
- **Navegación**: Comprar Entradas → SeatSelectionScreen

#### 4️⃣ SeatSelectionScreen
- **Propósito**: Selección interactiva de asientos
- **Componentes**:
  - Grilla de asientos (filas x columnas)
    - Estados: Disponible (gris), Ocupado (rojo), Seleccionado (verde)
    - Click en asiento disponible → selecciona/deselecciona
  - Contador de asientos seleccionados (0-4)
  - Botones: "Volver" | "Continuar"
- **Validaciones**:
  - Máximo 4 asientos por compra
  - No permitir seleccionar asientos ocupados/bloqueados
- **Navegación**: Continuar → TicketAssignmentScreen

#### 5️⃣ TicketAssignmentScreen
- **Propósito**: Asignar nombre a cada entrada comprada
- **Componentes**:
  - Lista de asientos seleccionados
  - Campo de texto por cada asiento:
    - Label: "Fila X, Columna Y"
    - Placeholder: "Nombre completo"
  - **Timer de cuenta regresiva (5 minutos)**
  - Indicador de progreso: "X de Y completados"
  - Botones: "Volver" | "Confirmar Compra"
- **Validaciones**:
  - Todos los nombres obligatorios
  - Mínimo 3 caracteres por nombre
  - Timer expirado → sesión cancelada
- **Navegación**: Confirmar → PurchaseConfirmationScreen

#### 6️⃣ PurchaseConfirmationScreen
- **Propósito**: Confirmación de compra exitosa
- **Componentes**:
  - Icono de éxito ✅
  - Mensaje: "¡Compra realizada exitosamente!"
  - Botón: "Volver al inicio"
- **Navegación**: Volver → EventListScreen

---

## 🔍 Funcionalidades Detalladas

### 1. Sincronización de Eventos (Kafka)

El sistema mantiene sincronizados los eventos entre el servidor de cátedra y el backend local:

**Flujo:**
1. Servidor cátedra publica cambios en Kafka (nuevo evento, modificación, cancelación)
2. **Proxy** consume mensajes de Kafka
3. Proxy notifica al **Backend** sobre el cambio
4. Backend actualiza su base de datos local
5. Clientes móviles ven datos actualizados en próxima consulta

**Tipos de cambios manejados:**
- ✅ Nuevos eventos agregados
- ✅ Eventos modificados (cambio de fecha, precio, etc.)
- ✅ Eventos cancelados
- ✅ Eventos expirados

---

### 2. Gestión de Sesiones (Redis)

**Características:**
- **Timeout de inactividad**: 30 minutos (configurable)
- **Sesiones concurrentes**: Un usuario puede tener una sesión activa en múltiples dispositivos
- **Persistencia**: Las sesiones sobreviven reinicios del backend
- **Estados de sesión**:
  - `SELECCION_ASIENTOS`: Sesión creada, esperando selección
  - `CARGA_DATOS`: Asientos seleccionados y bloqueados, esperando nombres
  - `COMPLETADO`: Compra finalizada
  - `EXPIRADO`: Sesión cancelada por timeout
  - `CANCELADO`: Sesión cancelada manualmente

**Bloqueo de asientos:**
- Duración inicial: **5 minutos**
- Se renueva al confirmar compra
- Liberación automática si no se completa

---

### 3. Proceso de Compra (Backend)

#### Paso 1: Iniciar Sesión
```java
POST /api/compra/iniciar
```
- Crea sesión en Redis
- Asigna tiempo de expiración
- Estado: `SELECCION_ASIENTOS`

#### Paso 2: Seleccionar Asientos
```java
POST /api/compra/seleccionar-asientos
```
- Valida máximo 4 asientos
- Consulta disponibilidad en Redis cátedra (vía Proxy)
- Bloquea asientos en cátedra (5 minutos)
- Guarda selección en sesión
- Estado: `CARGA_DATOS`

#### Paso 3: Asignar Nombres
```java
POST /api/compra/asignar-nombres
```
- Valida que todos los asientos tengan nombre
- Guarda nombres en sesión
- Mantiene bloqueo activo

#### Paso 4: Confirmar Compra
```java
POST /api/compra/confirmar
```
- Verifica asientos aún disponibles
- Crea registro en tabla `venta`
- Crea registros en tabla `asiento_vendido`
- **Sincroniza con cátedra**: `POST /api/endpoints/v1/realizar-venta`
- Actualiza estado a `COMPLETADO`
- Libera sesión

**Estados de sincronización:**
- `PENDIENTE`: Venta guardada localmente, esperando sincronización
- `SINCRONIZADA`: Venta confirmada en cátedra
- `ERROR`: Error al sincronizar (reintento automático)

---

### 4. Interfaz Móvil (Android)

#### Arquitectura MVVM

**ViewModel:**
```kotlin
class EventListViewModel(
    private val eventRepository: EventRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(EventListUiState())
    val uiState: StateFlow<EventListUiState> = _uiState.asStateFlow()
    
    fun loadEvents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = eventRepository.getEvents(page = 0)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        events = result.data,
                        isLoading = false
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        error = result.message,
                        isLoading = false
                    )
                }
            }
        }
    }
}
```

**Screen (Compose):**
```kotlin
@Composable
fun EventListScreen(
    viewModel: EventListViewModel,
    onEventClick: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LazyColumn {
        items(uiState.events) { event ->
            EventCard(
                event = event,
                onClick = { onEventClick(event.id) }
            )
        }
    }
}
```

#### Networking con Ktor

```kotlin
suspend fun getEvents(page: Int, size: Int): NetworkResult<List<EventoResponse>> {
    return try {
        val response = httpClient.get("${Endpoints.BASE_URL}/api/eventos") {
            parameter("page", page)
            parameter("size", size)
            bearerAuth(tokenManager.getToken())
        }
        
        when (response.status) {
            HttpStatusCode.OK -> NetworkResult.Success(response.body())
            HttpStatusCode.Unauthorized -> NetworkResult.Error("Sesión expirada", 401)
            else -> NetworkResult.Error(response.status.description, response.status.value)
        }
    } catch (e: Exception) {
        NetworkResult.Error(e.message ?: "Error desconocido")
    }
}
```

---

## ⚠️ Limitaciones Conocidas

### Backend

1. **VentaMapper devuelve datos mínimos del evento**
   - **Problema**: El mapper `VentaMapper.java` usa `@BeanMapping(ignoreByDefault = true)` lo que causa que solo se mapeen `evento.id` y `evento.titulo`.
   - **Impacto**: En `PurchaseConfirmationScreen` no se puede mostrar información completa del evento.
   - **Solución implementada**: Frontend usa `EventoMinimalInVenta` con solo `id` y `titulo`.
   - **Solución futura**: Crear `VentaConDetallesDTO` con evento completo para endpoints específicos.

2. **Funcionalidad "Mis Compras" no implementada**
   - **Diseño completo documentado** pero no implementado por limitación de tiempo.
   - **Archivos preparados**: Modelos, rutas de navegación.
   - Ver sección [Trabajo Futuro](#-trabajo-futuro).

3. **Sincronización de eventos manual**
   - No hay listener activo de Kafka en tiempo real.
   - Sincronización mediante endpoint manual: `POST /api/eventos/sincronizar-todo`
   - **Proxy** preparado pero no completamente integrado.

### Mobile

1. **Solo Android implementado**
   - iOS preparado en estructura pero sin testing.
   - Requiere configuración adicional de Xcode.

2. **Sin persistencia local en mobile**
   - Datos se consultan siempre del backend.
   - No hay caché offline.

3. **Imágenes de eventos**
   - Coil3 integrado pero imágenes no se muestran actualmente.
   - URLs de imágenes disponibles en modelo pero componente no renderizado.

4. **Sin notificaciones push**
   - No hay notificaciones de cambios en eventos.
   - No hay alertas de asientos bloqueados por otros.

---

## 🚀 Trabajo Futuro

### Alta Prioridad

#### 1. Implementar "Mis Compras" (Diseño completo)

**Backend (7 archivos):**
- `MisComprasDTO.java` - DTO con asientos incluidos
- `EventoSimpleDTO.java` - Evento simplificado
- Modificar `VentaRepository.java` - Agregar queries con JOIN FETCH
- `MisComprasService.java` - Lógica de negocio
- `MisComprasMapper.java` - Mapeo entidad → DTO
- `MisComprasResource.java` - Controller REST
- Verificar `Venta.java` - Relación @OneToMany con AsientoVendido

**Endpoints:**
```
GET /api/mis-compras?page=0&size=20  # Lista paginada
GET /api/mis-compras/{id}            # Detalle con asientos
```

**Frontend (12 archivos):**
- `PurchaseWithSeats.kt` - Modelo de respuesta
- Modificar `PurchaseApiService.kt` - Agregar métodos API
- Modificar `PurchaseRepository.kt` - Wrappers
- `MyPurchasesListScreen.kt` - Lista con scroll infinito
- `MyPurchasesListViewModel.kt` - Paginación
- `MyPurchasesListUiState.kt` - Estado
- `PurchaseDetailScreen.kt` - Detalle completo
- `PurchaseDetailViewModel.kt` - Carga por ID
- `PurchaseDetailUiState.kt` - Estado
- Modificar `Screen.kt` - Rutas MyPurchases, PurchaseDetail
- Modificar `AppNavigation.kt` - Composables
- Modificar `EventListScreen.kt` - Botón "Mis Compras"

**Tiempo estimado**: 2.5 horas

#### 2. Integración completa de Kafka

- Listener automático de eventos en Proxy
- Notificación al Backend vía WebSocket o REST
- Actualización automática en Mobile

### Media Prioridad

#### 3. Soporte iOS
- Testing en simulador iOS
- Configuración de firma de código
- Ajustes específicos de plataforma

#### 4. Persistencia local en Mobile
- Room database para Android
- SQLDelight para KMP compartido
- Modo offline básico

#### 5. Mejoras de UX
- Animaciones en transiciones
- Skeleton loaders
- Error states mejorados
- Imágenes de eventos (ya integrado Coil3)

### Baja Prioridad

#### 6. Notificaciones Push
- Firebase Cloud Messaging
- Notificaciones de eventos nuevos
- Alertas de asientos bloqueados

#### 7. Tests automatizados
- Tests unitarios (JUnit + MockK)
- Tests de integración (Testcontainers)
- Tests de UI (Compose UI Testing)

#### 8. CI/CD
- GitHub Actions
- Build automático
- Deploy a entornos

---

## 🧪 Testing

### Tests Manuales Realizados

El proyecto incluye guías completas de testing manual:

#### 1. Guía de Compra Completa
Ver archivo: [`GUIA_COMPRA.md`](GUIA_COMPRA.md)

**Flujo testeado:**
1. ✅ Autenticación (JWT)
2. ✅ Listar eventos
3. ✅ Consultar disponibilidad de asientos
4. ✅ Iniciar sesión de compra
5. ✅ Seleccionar asientos (1-4)
6. ✅ Asignar nombres
7. ✅ Confirmar compra
8. ✅ Verificar venta en BD local
9. ✅ Verificar sincronización con cátedra

#### 2. Testing de Disponibilidad de Asientos
Ver archivo: [`TESTING_DISPONIBILIDAD_ASIENTOS.md`](TESTING_DISPONIBILIDAD_ASIENTOS.md)

**Escenarios testeados:**
- ✅ Asientos disponibles
- ✅ Asientos ocupados
- ✅ Asientos bloqueados por otro usuario
- ✅ Estados en tiempo real desde Redis

#### 3. Testing de Sincronización
Ver archivo: [`TESTING_SINCRONIZACION.md`](TESTING_SINCRONIZACION.md)

**Validaciones:**
- ✅ Sincronización manual de eventos
- ✅ Venta registrada en backend local
- ✅ Venta sincronizada con cátedra
- ✅ Estados de sincronización (PENDIENTE → SINCRONIZADA)

### Tests Automatizados

**Backend:**
```bash
cd Backend

# Tests unitarios
./mvnw test

# Tests de integración
./mvnw verify
```

**Mobile:**
```bash
cd Mobile

# Tests unitarios
./gradlew :composeApp:testDebugUnitTest

# Tests de UI (Android)
./gradlew :composeApp:connectedAndroidTest
```

---

## 📚 Documentación Adicional

### Archivos Incluidos

| Archivo | Descripción |
|---------|-------------|
| [`GUIA_COMPRA.md`](GUIA_COMPRA.md) | Guía paso a paso para realizar una compra completa con comandos curl |
| [`TESTING_DISPONIBILIDAD_ASIENTOS.md`](TESTING_DISPONIBILIDAD_ASIENTOS.md) | Testing de consulta de disponibilidad de asientos en tiempo real |
| [`TESTING_SINCRONIZACION.md`](TESTING_SINCRONIZACION.md) | Validación de sincronización con servidor cátedra |
| [`Enunciado Trabajo Final 2025 - v1.2.txt`](Enunciado%20Trabajo%20Final%202025%20-%20v1.2.txt) | Enunciado oficial del trabajo práctico |

---

El sistema está **completamente funcional** para el flujo principal de compra de entradas, desde la autenticación hasta la confirmación de la venta con sincronización al servidor de cátedra.

---