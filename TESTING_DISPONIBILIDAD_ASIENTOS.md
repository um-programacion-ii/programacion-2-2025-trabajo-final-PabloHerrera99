# 📝 GUÍA DE TESTING: Consulta de Disponibilidad de Asientos

## 📌 Información del Endpoint

**Endpoint implementado:**
```
GET /api/eventos/{id}/asientos/disponibilidad
```

**Descripción:**  
Consulta en tiempo real la disponibilidad de todos los asientos de un evento. La información se obtiene desde Redis de la cátedra (vía Proxy) y se combina con los datos del evento almacenados localmente en PostgreSQL.

**Flujo de datos:**
```
Cliente → Backend:8081 → Proxy:8082 → Redis Cátedra:6379
                ↓
         PostgreSQL (info evento)
```

---

## 🔧 Prerrequisitos

### 1. Servicios Levantados

#### Backend (puerto 8081)
```bash
cd Backend
./mvnw spring-boot:run
```

Verificar en logs:
```
Started BackendApp in X.XXX seconds (JVM running for X.XXX)
Tomcat started on port(s): 8081 (http)
```

#### Proxy (puerto 8082)
```bash
cd Proxy
./mvnw spring-boot:run
```

Verificar en logs:
```
Started ProxyApp in X.XXX seconds (JVM running for X.XXX)
Tomcat started on port(s): 8082 (http)
```

#### Redis Local (Docker)
```bash
cd Backend
docker compose -f src/main/docker/redis.yml up -d
```

Verificar:
```bash
docker ps | grep redis
# Debe mostrar: redis:7-alpine ... Up ... 0.0.0.0:6379->6379/tcp
```

### 2. Conectividad con Cátedra

Verificar ZeroTier:
```bash
ping 192.168.194.250
```

Verificar Redis cátedra:
```bash
redis-cli -h 192.168.194.250 -p 6379 ping
# Respuesta: PONG
```

### 3. Datos de Prueba

#### Sincronizar eventos desde cátedra:
```bash
# Opción 1: Login y sincronización manual
curl -X POST http://localhost:8081/api/authenticate \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'

# Copiar el token del response (id_token)

curl -X POST http://localhost:8081/api/eventos/sync/forzar \
  -H "Authorization: Bearer {TU_TOKEN_AQUI}"
```

#### Opción 2: Esperar sincronización automática (vía Kafka)
El sistema sincroniza automáticamente cuando detecta cambios en el topic `eventos-actualizacion`.

#### Verificar eventos disponibles:
```bash
curl http://localhost:8081/api/eventos \
  -H "Authorization: Bearer {TU_TOKEN_AQUI}"
```

---

## 🧪 Casos de Prueba

### Test 1: Consulta Básica (Evento Vacío)

**Objetivo:** Verificar que el endpoint funciona correctamente con un evento sin asientos bloqueados/vendidos.

**Request:**
```bash
curl -X GET "http://localhost:8081/api/eventos/1/asientos/disponibilidad" \
  -H "Authorization: Bearer {TU_TOKEN}" \
  -H "Accept: application/json" \
  | jq .
```

**Response Esperada (HTTP 200):**
```json
{
  "eventoId": 1,
  "eventoIdCatedra": 1,
  "tituloEvento": "Conferencia Nerd",
  "totalFilas": 10,
  "totalColumnas": 20,
  "totalAsientos": 200,
  "disponibles": 200,
  "bloqueados": 0,
  "vendidos": 0,
  "asientos": [
    {
      "fila": 1,
      "columna": 1,
      "estado": "DISPONIBLE",
      "expira": null,
      "nombrePersona": null
    },
    {
      "fila": 1,
      "columna": 2,
      "estado": "DISPONIBLE",
      "expira": null,
      "nombrePersona": null
    }
    // ... 198 asientos más
  ],
  "consultadoEn": "2025-12-10T15:45:30.123456Z"
}
```

**Validaciones:**
- ✅ `disponibles + bloqueados + vendidos = totalAsientos`
- ✅ `asientos.length = totalAsientos`
- ✅ Todos los asientos en estado `DISPONIBLE`

---

### Test 2: Consulta con Asientos Bloqueados y Vendidos

**Preparación:** Bloquear asientos desde servidor de cátedra

```bash
# Login en servidor cátedra
curl -X POST http://192.168.194.250:8080/api/authenticate \
  -H "Content-Type: application/json" \
  -d '{"username":"pablo.herrera","password":"password123"}' \
  | jq -r '.id_token'

# Bloquear asientos
curl -X POST http://192.168.194.250:8080/api/endpoints/v1/bloquear-asientos \
  -H "Authorization: Bearer {TOKEN_CATEDRA}" \
  -H "Content-Type: application/json" \
  -d '{
    "eventoId": 1,
    "asientos": [
      {"fila": 2, "columna": 3},
      {"fila": 2, "columna": 4}
    ]
  }'
```

**Request Backend:**
```bash
curl -X GET "http://localhost:8081/api/eventos/1/asientos/disponibilidad" \
  -H "Authorization: Bearer {TOKEN_BACKEND}" \
  | jq .
```

**Response Esperada:**
```json
{
  "eventoId": 1,
  "eventoIdCatedra": 1,
  "tituloEvento": "Conferencia Nerd",
  "totalFilas": 10,
  "totalColumnas": 20,
  "totalAsientos": 200,
  "disponibles": 198,
  "bloqueados": 2,
  "vendidos": 0,
  "asientos": [
    {
      "fila": 2,
      "columna": 3,
      "estado": "BLOQUEADO",
      "expira": "2025-12-10T16:00:30.123Z",
      "nombrePersona": null
    },
    {
      "fila": 2,
      "columna": 4,
      "estado": "BLOQUEADO",
      "expira": "2025-12-10T16:00:30.456Z",
      "nombrePersona": null
    }
    // ... resto de asientos DISPONIBLE
  ],
  "consultadoEn": "2025-12-10T15:55:30.789Z"
}
```

**Validaciones:**
- ✅ `bloqueados = 2`
- ✅ Asientos (2,3) y (2,4) tienen `estado = "BLOQUEADO"`
- ✅ Campo `expira` es una fecha futura (bloqueo válido por 5 min)

---

### Test 3: Evento No Existente

**Request:**
```bash
curl -X GET "http://localhost:8081/api/eventos/99999/asientos/disponibilidad" \
  -H "Authorization: Bearer {TOKEN}" \
  -i
```

**Response Esperada:**
```
HTTP/1.1 404 Not Found
Content-Length: 0
```

**Validaciones:**
- ✅ Status Code: `404 NOT FOUND`
- ✅ Body vacío

---

### Test 4: Evento sin Configuración de Asientos

**Escenario:** Evento con `filaAsientos` o `columnaAsientos` = NULL

**Request:**
```bash
curl -X GET "http://localhost:8081/api/eventos/{ID_SIN_CONFIG}/asientos/disponibilidad" \
  -H "Authorization: Bearer {TOKEN}" \
  -i
```

**Response Esperada:**
```
HTTP/1.1 400 Bad Request
Content-Length: 0
```

**Validaciones:**
- ✅ Status Code: `400 BAD REQUEST`

---

### Test 5: Proxy No Disponible

**Preparación:** Detener el Proxy

```bash
# En terminal del Proxy: Ctrl+C
```

**Request:**
```bash
curl -X GET "http://localhost:8081/api/eventos/1/asientos/disponibilidad" \
  -H "Authorization: Bearer {TOKEN}" \
  -i
```

**Response Esperada:**
```
HTTP/1.1 500 Internal Server Error
Content-Length: 0
```

**Validaciones:**
- ✅ Status Code: `500 INTERNAL SERVER ERROR`
- ✅ Log en Backend muestra error de conectividad

**Logs esperados:**
```
ERROR c.e.b.w.r.AsientosDisponibilidadResource - Error obteniendo disponibilidad para evento 1: Connection refused
```

---

### Test 6: Sin Autenticación

**Request:**
```bash
curl -X GET "http://localhost:8081/api/eventos/1/asientos/disponibilidad" \
  -i
```

**Response Esperada:**
```
HTTP/1.1 401 Unauthorized
Content-Length: 0
```

**Validaciones:**
- ✅ Status Code: `401 UNAUTHORIZED`

---

### Test 7: Verificar Caché No Activa

**Objetivo:** Confirmar que los datos siempre son frescos (no cacheados)

**Request 1:**
```bash
curl -X GET "http://localhost:8081/api/eventos/1/asientos/disponibilidad" \
  -H "Authorization: Bearer {TOKEN}" \
  | jq '.consultadoEn'
```

**Esperar 2-3 segundos**

**Request 2:**
```bash
curl -X GET "http://localhost:8081/api/eventos/1/asientos/disponibilidad" \
  -H "Authorization: Bearer {TOKEN}" \
  | jq '.consultadoEn'
```

**Validaciones:**
- ✅ `consultadoEn` de Request 2 > `consultadoEn` de Request 1
- ✅ Response headers contienen: `Cache-Control: no-cache, no-store, max-age=0, must-revalidate`

---

## 🔍 Verificación en Redis Cátedra

### Consultar directamente los datos en Redis:

```bash
# Conectar a Redis cátedra
redis-cli -h 192.168.194.250 -p 6379

# Consultar key del evento
GET evento_1
```

**Response ejemplo:**
```json
{
  "eventoId": 1,
  "asientos": [
    {"fila": 2, "columna": 3, "estado": "Bloqueado", "expira": "2025-12-10T16:00:30.123Z"},
    {"fila": 2, "columna": 4, "estado": "Vendido"}
  ]
}
```

**Nota:** Solo aparecen asientos con estado `Bloqueado` o `Vendido`. Los asientos disponibles NO están en Redis.

---

## 📊 Estructura de Respuesta Completa

```json
{
  // Información del Evento
  "eventoId": 1,              // ID local (PostgreSQL Backend)
  "eventoIdCatedra": 1,       // ID en servidor cátedra
  "tituloEvento": "Conferencia Nerd",
  
  // Dimensiones
  "totalFilas": 10,
  "totalColumnas": 20,
  "totalAsientos": 200,       // totalFilas * totalColumnas
  
  // Estadísticas
  "disponibles": 196,         // Asientos libres
  "bloqueados": 2,            // Asientos bloqueados (no expirados)
  "vendidos": 2,              // Asientos vendidos
  
  // Matriz completa
  "asientos": [
    {
      "fila": 1,              // Número de fila (1-based)
      "columna": 1,           // Número de columna (1-based)
      "estado": "DISPONIBLE", // DISPONIBLE | BLOQUEADO | VENDIDO
      "expira": null,         // Timestamp (solo para BLOQUEADO)
      "nombrePersona": null   // String (solo para VENDIDO/BLOQUEADO)
    }
    // ... 199 asientos más
  ],
  
  // Metadata
  "consultadoEn": "2025-12-10T15:45:30.123456Z"
}
```

---

## 🛠️ Troubleshooting

### ❌ Error: "Evento no encontrado"
**Solución:**
1. Verificar que el evento existe: `GET /api/eventos`
2. Forzar sincronización: `POST /api/eventos/sync/forzar`

### ❌ Error: "Connection refused" (Proxy)
**Solución:**
1. Verificar Proxy corriendo: `curl http://localhost:8082/management/health`
2. Revisar configuración en `application-dev.yml` → `application.proxy.base-url`

### ❌ Error: "Connection timeout" (Redis Cátedra)
**Solución:**
1. Verificar ZeroTier: `ping 192.168.194.250`
2. Verificar Redis: `redis-cli -h 192.168.194.250 -p 6379 ping`
3. Revisar configuración en Proxy

### ❌ Error: "Bad Request" (Estado inválido)
**Solución:**
1. Verificar que el evento tenga `filaAsientos` y `columnaAsientos` configurados
2. Query en H2 Console:
   ```sql
   SELECT id, titulo, fila_asientos, columna_asientos 
   FROM evento WHERE id = 1;
   ```

---

## 📝 Checklist de Testing

- [ ] Backend corriendo en puerto 8081
- [ ] Proxy corriendo en puerto 8082
- [ ] Redis local levantado
- [ ] Conectividad con Redis cátedra verificada
- [ ] Token JWT obtenido (`POST /api/authenticate`)
- [ ] Al menos 1 evento sincronizado en BD local
- [ ] Test 1: Consulta básica exitosa (200 OK)
- [ ] Test 2: Asientos bloqueados/vendidos reflejados
- [ ] Test 3: Evento inexistente (404)
- [ ] Test 4: Evento sin config (400)
- [ ] Test 5: Proxy caído (500)
- [ ] Test 6: Sin auth (401)
- [ ] Test 7: Caché deshabilitado verificado
- [ ] Logs del Backend sin errores

---

## 🎯 Comando Rápido para Test Completo

```bash
# 1. Obtener token
TOKEN=$(curl -s -X POST http://localhost:8081/api/authenticate \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}' \
  | jq -r '.id_token')

# 2. Consultar disponibilidad
curl -s -X GET "http://localhost:8081/api/eventos/1/asientos/disponibilidad" \
  -H "Authorization: Bearer $TOKEN" \
  | jq '{
      evento: .tituloEvento,
      total: .totalAsientos,
      stats: {
        disponibles: .disponibles,
        bloqueados: .bloqueados,
        vendidos: .vendidos
      },
      timestamp: .consultadoEn
    }'
```

**Output esperado:**
```json
{
  "evento": "Conferencia Nerd",
  "total": 200,
  "stats": {
    "disponibles": 196,
    "bloqueados": 2,
    "vendidos": 2
  },
  "timestamp": "2025-12-10T15:45:30.123456Z"
}
```

---

## 📚 Endpoints Relacionados

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/authenticate` | POST | Obtener JWT token |
| `/api/eventos` | GET | Listar eventos |
| `/api/eventos/{id}` | GET | Detalle evento |
| `/api/eventos/{id}/asientos/disponibilidad` | GET | **Disponibilidad asientos** |
| `/api/eventos/sync/forzar` | POST | Forzar sincronización |

---

## 🎓 Ejemplo Completo: Flow End-to-End

### Paso 1: Autenticación
```bash
# Login Backend
curl -X POST http://localhost:8081/api/authenticate \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}' \
  | jq -r '.id_token' > token.txt

TOKEN=$(cat token.txt)
```

### Paso 2: Sincronizar Eventos
```bash
curl -X POST http://localhost:8081/api/eventos/sync/forzar \
  -H "Authorization: Bearer $TOKEN"
```

### Paso 3: Listar Eventos
```bash
curl -X GET http://localhost:8081/api/eventos \
  -H "Authorization: Bearer $TOKEN" \
  | jq '.[] | {id, titulo, filas: .filaAsientos, columnas: .columnaAsientos}'
```

### Paso 4: Consultar Disponibilidad
```bash
curl -X GET "http://localhost:8081/api/eventos/1/asientos/disponibilidad" \
  -H "Authorization: Bearer $TOKEN" \
  | jq .
```

### Paso 5: Bloquear Asientos (desde cátedra)
```bash
# Login cátedra
TOKEN_CATEDRA=$(curl -s -X POST http://192.168.194.250:8080/api/authenticate \
  -H "Content-Type: application/json" \
  -d '{"username":"pablo.herrera","password":"password123"}' \
  | jq -r '.id_token')

# Bloquear
curl -X POST http://192.168.194.250:8080/api/endpoints/v1/bloquear-asientos \
  -H "Authorization: Bearer $TOKEN_CATEDRA" \
  -H "Content-Type: application/json" \
  -d '{
    "eventoId": 1,
    "asientos": [
      {"fila": 5, "columna": 10},
      {"fila": 5, "columna": 11}
    ]
  }'
```

### Paso 6: Verificar Cambios
```bash
# Consultar nuevamente disponibilidad
curl -X GET "http://localhost:8081/api/eventos/1/asientos/disponibilidad" \
  -H "Authorization: Bearer $TOKEN" \
  | jq '{
      disponibles,
      bloqueados,
      vendidos,
      asientosBloqueados: [.asientos[] | select(.estado == "BLOQUEADO")]
    }'
```

---

## 🔬 Testing Avanzado: Estados de Asientos

### Filtrar solo asientos BLOQUEADOS
```bash
curl -s -X GET "http://localhost:8081/api/eventos/1/asientos/disponibilidad" \
  -H "Authorization: Bearer $TOKEN" \
  | jq '.asientos[] | select(.estado == "BLOQUEADO")'
```

### Filtrar solo asientos VENDIDOS
```bash
curl -s -X GET "http://localhost:8081/api/eventos/1/asientos/disponibilidad" \
  -H "Authorization: Bearer $TOKEN" \
  | jq '.asientos[] | select(.estado == "VENDIDO")'
```

### Contar asientos por estado
```bash
curl -s -X GET "http://localhost:8081/api/eventos/1/asientos/disponibilidad" \
  -H "Authorization: Bearer $TOKEN" \
  | jq '.asientos | group_by(.estado) | map({estado: .[0].estado, cantidad: length})'
```

**Output ejemplo:**
```json
[
  {"estado": "DISPONIBLE", "cantidad": 196},
  {"estado": "BLOQUEADO", "cantidad": 2},
  {"estado": "VENDIDO", "cantidad": 2}
]
```

---

## 📖 Documentación de Referencia

- **Consigna del Proyecto**: `Enunciado Trabajo Final 2025 - v1.2.txt`
- **Plan de Implementación**: `plan.md`
- **Testing Sincronización**: `TESTING_SINCRONIZACION.md`
- **Integración Kafka**: `KAFKA_INTEGRATION.md`
- **Sincronización Eventos**: `SYNC_EVENTOS_CATEDRA.md`
