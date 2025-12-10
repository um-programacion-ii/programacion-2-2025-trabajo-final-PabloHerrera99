# Plan de Implementación - Sistema de Gestión de Eventos con JHipster y Maven

## FASE 0: Configuración del Entorno

### 0.1. Prerrequisitos
- **Java Development Kit (JDK)**: Instalar JDK 17 o superior
- **Node.js y npm**: Para JHipster (versión LTS recomendada)
- **JHipster**: Instalar globalmente (`npm install -g generator-jhipster`)
- **Maven**: Verificar instalación (generalmente viene con JHipster)
- **Docker**: Para Redis local y servicios de desarrollo
- **Git**: Para control de versiones
- **IDE**: IntelliJ IDEA o Eclipse con soporte Maven

### 0.2. Acceso a Servicios de la Cátedra
- Unirse a ZeroTier (red: `93afae59630d1f1d`)
- Obtener autorización y registrar dirección asignada
- Verificar conectividad a:
  - Servidor web: `http://192.168.194.250:8080`
  - Redis cátedra: `192.168.194.250:6379`
  - Kafka: `192.168.194.250:9092`

### 0.3. Registro en el Servidor de la Cátedra
- Realizar POST a `http://192.168.194.250:8080/api/v1/agregar_usuario`
- Guardar el JWT token obtenido (será usado en configuración del backend)

---

## FASE 1: Generación del Backend con JHipster

### 1.1. Crear Aplicación JHipster

```bash
mkdir evento-backend
cd evento-backend
jhipster
```

**Configuraciones recomendadas:**
- **Tipo de aplicación**: Monolith
- **Nombre**: `eventoBackend` o similar
- **Puerto**: `8081` (8080 está usado por cátedra)
- **Autenticación**: JWT
- **Base de datos**: PostgreSQL (producción) + H2 (desarrollo)
- **Cache**: Sí, con Redis (para sesiones)
- **Build tool**: Maven
- **Framework frontend**: Angular/React/Vue (opcional, ya que tendrás app móvil)
- **Internacionalización**: Español + Inglés

### 1.2. Modelo de Entidades del Backend

Crear las siguientes entidades JHipster (usando `jhipster entity`):

#### a) Entidad: Evento
```
- id: Long (auto)
- idCatedra: Long (ID del evento en servidor cátedra)
- titulo: String (required)
- resumen: String
- descripcion: String (text)
- fecha: Instant (required)
- direccion: String
- imagen: String (URL)
- filaAsientos: Integer
- columnaAsientos: Integer
- precioEntrada: BigDecimal
- activo: Boolean
- fechaSincronizacion: Instant
```

#### b) Entidad: EventoTipo
```
- id: Long (auto)
- nombre: String (required)
- descripcion: String
```

#### c) Entidad: Integrante
```
- id: Long (auto)
- nombre: String (required)
- apellido: String (required)
- identificacion: String
- evento: ManyToOne relationship con Evento
```

#### d) Entidad: Sesion
```
- id: Long (auto)
- usuario: ManyToOne con User (JHipster built-in)
- evento: ManyToOne con Evento
- estado: Enum (SELECCION_EVENTO, SELECCION_ASIENTOS, CARGA_DATOS, COMPLETADO)
- fechaInicio: Instant
- ultimaActividad: Instant
- expiracion: Instant
- activa: Boolean
```

#### e) Entidad: AsientoSeleccionado
```
- id: Long (auto)
- sesion: ManyToOne con Sesion
- fila: Integer (required)
- columna: Integer (required)
- nombrePersona: String
```

#### f) Entidad: Venta
```
- id: Long (auto)
- idVentaCatedra: Long
- evento: ManyToOne con Evento
- usuario: ManyToOne con User
- fechaVenta: Instant
- precioTotal: BigDecimal
- exitosa: Boolean
- descripcion: String
- estadoSincronizacion: Enum (PENDIENTE, SINCRONIZADA, ERROR)
```

#### g) Entidad: AsientoVendido
```
- id: Long (auto)
- venta: ManyToOne con Venta
- fila: Integer (required)
- columna: Integer (required)
- nombrePersona: String (required)
- estado: String
```

---

## FASE 2: Implementación del Servicio Proxy

### 2.1. Crear Proyecto Proxy (Separado)
```bash
mkdir evento-proxy
cd evento-proxy
jhipster
```

**Configuraciones:**
- **Tipo**: Microservice o Monolith simple
- **Puerto**: `8082`
- **Autenticación**: JWT
- **Base de datos**: Ninguna (o H2 mínima para configuración)

### 2.2. Dependencias Maven para Proxy (pom.xml)
```xml
<!-- Redis Client -->
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
</dependency>

<!-- Kafka Client -->
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

### 2.3. Funcionalidades del Proxy

#### a) Servicio Redis:
- `ConsultarAsientosService`: Lee de Redis cátedra (`evento_{id}`)
- Endpoint REST: `GET /api/proxy/asientos/{eventoId}`

#### b) Consumer Kafka:
- Suscribirse al tópico: `eventos-actualizacion`
- `group.id`: Único por alumno (ej: `grupo-pablo-12345`)
- Procesar mensajes y notificar al backend

#### c) Notificación al Backend:
Opciones:
- **Opción A (Recomendada)**: Webhook - El proxy hace POST al backend
- **Opción B**: WebSocket - Conexión persistente
- **Opción C**: Polling - Backend consulta periódicamente al proxy

Implementar endpoint en Backend:
```
POST /api/eventos/notificacion-cambio
Body: { "eventoId": 123, "tipoCambio": "ACTUALIZADO" }
```

---

## FASE 3: Integración Backend con Servicios Externos

### 3.1. Cliente HTTP para Servidor Cátedra

**Crear Service: `CatedraClientService`**

Usar RestTemplate o WebClient (Spring) con:
- Base URL: `http://192.168.194.250:8080`
- Interceptor para agregar JWT en headers:
  ```java
  Authorization: Bearer {TOKEN_OBTENIDO_EN_REGISTRO}
  ```

Métodos:
- `getEventosResumidos()`
- `getEventosCompletos()`
- `getEventoById(Long id)`
- `bloquearAsientos(BloqueoRequest request)`
- `realizarVenta(VentaRequest request)`
- `listarVentas()`
- `getVentaById(Long id)`

### 3.2. Cliente para Proxy

**Crear Service: `ProxyClientService`**
- Método: `getAsientosEvento(Long eventoId)`
- URL: `http://localhost:8082/api/proxy/asientos/{eventoId}`

### 3.3. Configuración en `application.yml`
```yaml
application:
  catedra:
    base-url: http://192.168.194.250:8080
    token: eyJhbGci... # Token obtenido
  proxy:
    base-url: http://localhost:8082
```

---

## FASE 4: Lógica de Negocio Backend

### 4.1. Sincronización de Eventos

**Crear: `EventoSyncService`**

Funcionalidades:
- **Sincronización inicial**: Al iniciar, consultar todos los eventos
- **Sincronización incremental**: 
  - Listener para notificaciones desde Proxy
  - Actualizar base de datos local
  - Marcar eventos como inactivos si se cancelan/expiran

Estrategia:
```java
@Service
public class EventoSyncService {
    
    @Scheduled(fixedDelay = 3600000) // Cada hora como backup
    public void sincronizarEventos() {
        // Obtener eventos del servidor cátedra
        // Comparar con DB local
        // Actualizar/crear/desactivar según corresponda
    }
    
    @Transactional
    public void procesarCambioEvento(Long eventoIdCatedra) {
        // Llamar a /api/endpoints/v1/evento/{id}
        // Actualizar en DB local
    }
}
```

### 4.2. Gestión de Sesiones

**Crear: `SesionService`**

Funcionalidades:
- **Crear sesión**: Al login desde móvil
- **Validar sesión**: En cada request
- **Actualizar actividad**: Extender expiración (30 min)
- **Recuperar sesión**: Permitir múltiples clientes con mismo usuario
- **Expirar sesión**: Cleanup automático

Usar Redis local para:
```java
// Key: "sesion:usuario:{userId}"
// Value: {
//   "sesionId": 123,
//   "eventoId": 5,
//   "estado": "SELECCION_ASIENTOS",
//   "asientosSeleccionados": [...],
//   "ultimaActividad": "2025-12-03T10:30:00Z"
// }
```

### 4.3. Proceso de Venta

**Crear: `VentaService`**

Flujo:
1. **Selección de asientos**:
   - Validar disponibilidad consultando Proxy (Redis cátedra)
   - Guardar en sesión (Redis local)

2. **Bloqueo**:
   - Llamar a `/api/endpoints/v1/bloquear-asientos` (cátedra)
   - Si exitoso, actualizar sesión
   - Iniciar timer de 5 minutos

3. **Carga de datos**:
   - Asociar nombres a asientos en sesión

4. **Venta**:
   - Llamar a `/api/endpoints/v1/realizar-venta` (cátedra)
   - Si exitoso: Guardar en DB local como SINCRONIZADA
   - Si falla: Guardar como PENDIENTE y reintentar
   - Limpiar sesión

### 4.4. Endpoints REST del Backend

```java
// Eventos
GET    /api/eventos                    // Listar eventos activos
GET    /api/eventos/{id}               // Detalle evento
GET    /api/eventos/{id}/asientos      // Mapa de asientos (consulta proxy)

// Sesiones
POST   /api/sesiones/iniciar           // Crear/recuperar sesión
GET    /api/sesiones/actual            // Estado actual de sesión
PUT    /api/sesiones/seleccionar-asientos
PUT    /api/sesiones/asignar-nombres
DELETE /api/sesiones/cancelar

// Ventas
POST   /api/ventas/realizar            // Confirmar compra
GET    /api/ventas/mis-ventas          // Ventas del usuario

// Webhook (desde Proxy)
POST   /api/eventos/notificacion-cambio
```

---

## FASE 5: Configuración de Redis Local

### 5.1. Docker Compose para Desarrollo
```yaml
# docker-compose.yml en carpeta backend
version: '3.8'
services:
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes
    volumes:
      - redis-data:/data

volumes:
  redis-data:
```

### 5.2. Configuración Spring Redis (application.yml)
```yaml
spring:
  redis:
    host: localhost
    port: 6379
  session:
    store-type: redis
    timeout: 30m
```

---

## FASE 6: Tests y Validación

### 6.1. Tests Unitarios
- Servicios de sincronización
- Lógica de sesiones
- Cálculo de disponibilidad

### 6.2. Tests de Integración
- Comunicación con servidor cátedra (mocks)
- Flujo completo de venta
- Manejo de errores de red

### 6.3. Tests Manuales
- Usar Postman/Insomnia para probar endpoints
- Simular notificaciones de Kafka manualmente
- Verificar sincronización con `/api/endpoints/v1/forzar-actualizacion`

---

## FASE 7: Cliente Móvil (KMP) - Preparación Backend

### 7.1. Documentación de API
- Generar documentación OpenAPI/Swagger (JHipster lo incluye)
- Documentar flujo de sesiones para desarrollador móvil

### 7.2. Endpoints Específicos para Móvil
```java
// Autenticación
POST /api/authenticate
{
  "username": "user",
  "password": "pass"
}

// Flujo móvil
GET  /api/mobile/eventos             // Lista simplificada
GET  /api/mobile/eventos/{id}/detalles
GET  /api/mobile/sesion/estado       // Estado actual para recuperación
POST /api/mobile/sesion/evento/{id}  // Iniciar selección
POST /api/mobile/sesion/asientos     // Seleccionar y bloquear
PUT  /api/mobile/sesion/nombres      // Asignar nombres
POST /api/mobile/venta/confirmar     // Realizar venta
```

---

## FASE 8: Deployment y Configuración de Producción

### 8.1. Perfiles Maven
```xml
<!-- pom.xml -->
<profiles>
    <profile>
        <id>dev</id>
        <!-- Configuración desarrollo -->
    </profile>
    <profile>
        <id>prod</id>
        <!-- Configuración producción -->
    </profile>
</profiles>
```

### 8.2. Build y Empaquetado
```bash
# Backend
cd evento-backend
./mvnw clean package -Pprod

# Proxy
cd evento-proxy
./mvnw clean package -Pprod
```

### 8.3. Configuración de Producción
- Variables de entorno para tokens
- Base de datos PostgreSQL
- Redis persistente
- Logs estructurados

---

## ORDEN DE IMPLEMENTACIÓN RECOMENDADO

1. **Semana 1-2**: Fases 0, 1 (Backend base + entidades)
2. **Semana 3**: Fase 3 (Integración con cátedra)
3. **Semana 4**: Fase 2 (Proxy básico - Redis)
4. **Semana 5**: Fase 2 (Proxy Kafka + notificaciones)
5. **Semana 6**: Fase 4 (Lógica de sesiones y ventas)
6. **Semana 7**: Fase 6 (Tests) + Fase 7 (Preparar para móvil)
7. **Semana 8+**: Cliente móvil KMP (en paralelo con refinamiento backend)

---

## NOTAS IMPORTANTES

- El puerto 8080 está ocupado por el servidor de cátedra, usar 8081 para backend
- Cada alumno necesita un `group.id` único para Kafka
- Los bloqueos de asientos expiran a los 5 minutos
- Las sesiones de usuario expiran a los 30 minutos de inactividad
- Redis de cátedra solo almacena asientos bloqueados/vendidos, el resto son disponibles
- El token JWT del servidor de cátedra debe guardarse de forma segura
