# 🎟️ Guía de Compra de Asientos - Paso a Paso

## Prerequisitos

Asegurarse de tener los servicios corriendo:

```bash
# Terminal 1: Backend
cd Backend && ./mvnw spring-boot:run

# Terminal 2: Proxy
cd Proxy && ./mvnw spring-boot:run

# Terminal 3: Redis
docker compose -f Backend/src/main/docker/redis.yml up -d
```

---

## Paso a Paso para Realizar una Compra

### 1_Autenticarse y Obtener Token

```bash
TOKEN=$(curl -s -X POST http://localhost:8081/api/authenticate \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}' | jq -r '.id_token')

echo "Token: $TOKEN"
```

### 2_Listar Eventos Disponibles

```bash
curl -s http://localhost:8081/api/eventos \
  -H "Authorization: Bearer $TOKEN" | \
  jq '.[] | {id, titulo, fecha, precio: .precioEntrada, asientos: (.filaAsientos * .columnaAsientos)}'
```

**Anotar el `id` del evento que querés comprar**

### 3_Consultar Disponibilidad de Asientos

```bash
# Reemplazar 1054 con el ID del evento
EVENTO_ID=1054

curl -s "http://localhost:8081/api/eventos/${EVENTO_ID}/asientos/disponibilidad" \
  -H "Authorization: Bearer $TOKEN" | jq .
```

**Elegir las coordenadas (fila, columna) de los asientos disponibles**

### 4_Iniciar Sesión de Compra

```bash
curl -s -X POST http://localhost:8081/api/compra/iniciar \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"eventoId\": ${EVENTO_ID}}" | jq .
```

**Verificar que el estado sea:** `SELECCION_ASIENTOS`

### 5_Seleccionar Asientos (1 a 4 máximo)

```bash
curl -s -X POST http://localhost:8081/api/compra/seleccionar-asientos \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "asientos": [
      {"fila": 20, "columna": 9},
      {"fila": 20, "columna": 10}
    ]
  }' | jq .
```

**Verificar que el estado cambie a:** `CARGA_DATOS`

### 6_Asignar Nombres a los Asientos

```bash
curl -s -X POST http://localhost:8081/api/compra/asignar-nombres \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombres": {
      "20-9": "María García",
      "20-10": "Carlos López"
    }
  }' | jq .
```

**Formato:** `"fila-columna": "Nombre Completo"` (mínimo 3 caracteres)

### 7_Confirmar la Compra

```bash
curl -s -X POST http://localhost:8081/api/compra/confirmar \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq .
```

**✅ Verificar respuesta:**
- `exitosa: true`
- `estadoSincronizacion: "SINCRONIZADA"`
- `idVentaCatedra` no nulo

**Anotar el `id` de la venta para verificación**

### 8_Verificar la Venta

```bash
# Reemplazar con el ID de la venta obtenido
VENTA_ID=1251

# Ver detalles de la venta
curl -s "http://localhost:8081/api/ventas/${VENTA_ID}" \
  -H "Authorization: Bearer $TOKEN" | jq .

# Ver asientos vendidos
curl -s "http://localhost:8081/api/asiento-vendidos?ventaId.equals=${VENTA_ID}" \
  -H "Authorization: Bearer $TOKEN" | jq .
```

---

## Compra Completada Exitosamente

Si todos los pasos anteriores funcionaron correctamente:
- Asientos bloqueados en Redis cátedra
- Venta sincronizada con servidor cátedra
- Asientos marcados como vendidos
- Sesión de compra cerrada

---

## Comandos Útiles

### Verificar Estado de Servicios

```bash
# Backend
curl -s http://localhost:8081/management/health | jq .

# Proxy
curl -s http://localhost:8082/management/health | jq .

# Redis local
redis-cli ping
```

### Sincronizar Eventos desde Cátedra

```bash
curl -s -X POST http://localhost:8081/api/eventos/sincronizar-todo \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq .
```

### Ver Estado de Sesión Activa

```bash
curl -s http://localhost:8081/api/compra/estado \
  -H "Authorization: Bearer $TOKEN" | jq .
```

### Cancelar Sesión Activa

```bash
curl -s -X POST http://localhost:8081/api/compra/cancelar \
  -H "Authorization: Bearer $TOKEN" | jq .
```

### Ver Todas las Ventas

```bash
curl -s http://localhost:8081/api/ventas \
  -H "Authorization: Bearer $TOKEN" | jq .
```

### Filtrar Eventos Activos con idCatedra

```bash
curl -s http://localhost:8081/api/eventos \
  -H "Authorization: Bearer $TOKEN" | \
  jq '[.[] | select(.activo == true and .idCatedra != null)]'
```

### Ver Solo Asientos Disponibles de un Evento

```bash
curl -s "http://localhost:8081/api/eventos/${EVENTO_ID}/asientos/disponibilidad" \
  -H "Authorization: Bearer $TOKEN" | \
  jq '.asientos[] | select(.estado == "DISPONIBLE") | {fila, columna}'
```

### Consultar Venta en Servidor de Cátedra

```bash
# Autenticarse en cátedra
TOKEN_CATEDRA=$(curl -s -X POST http://192.168.194.250:8080/api/authenticate \
  -H "Content-Type: application/json" \
  -d '{"username":"pablo.herrera","password":"password123"}' | jq -r '.id_token')

# Consultar asientos vendidos (usar idCatedra del evento)
EVENTO_ID_CATEDRA=4

curl -s "http://192.168.194.250:8080/api/endpoints/v1/evento/${EVENTO_ID_CATEDRA}/asientos-vendidos" \
  -H "Authorization: Bearer $TOKEN_CATEDRA" | jq .
```

### Ver Estado de Asientos en Redis Cátedra

```bash
redis-cli -h 192.168.194.250 -p 6379 GET evento_4
```

### Verificar Base de Datos H2

```bash
# Abrir: http://localhost:8081/h2-console
# JDBC URL: jdbc:h2:file:./target/h2db/db/finalProgramacion
# User: finalProgramacion
# Password: (vacío)
```

**Queries útiles:**

```sql
-- Ver todas las ventas
SELECT * FROM venta ORDER BY fecha_venta DESC;

-- Ver asientos vendidos de una venta
SELECT * FROM asiento_vendido WHERE venta_id = 1251;

-- Ver sesiones activas
SELECT * FROM sesion WHERE activa = TRUE;

-- Ver sesiones completadas
SELECT * FROM sesion WHERE estado = 'COMPLETADO' ORDER BY fecha_inicio DESC;

-- Ver eventos sincronizados
SELECT id, id_catedra, titulo, activo FROM evento WHERE id_catedra IS NOT NULL;
```

### Regenerar Token si Expiró

```bash
TOKEN=$(curl -s -X POST http://localhost:8081/api/authenticate \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}' | jq -r '.id_token')
```

### Ver Logs del Backend en Tiempo Real

```bash
# Si estás corriendo con ./mvnw
# Los logs aparecen en la terminal donde ejecutaste el comando

# Si necesitas filtrar solo errores:
# (En la terminal donde corre el backend, presionar Ctrl+C y reiniciar con:)
./mvnw spring-boot:run | grep ERROR
```

### Verificar Conectividad con Servidor Cátedra

```bash
# Ping al servidor
ping 192.168.194.250

# Verificar Redis cátedra
redis-cli -h 192.168.194.250 -p 6379 ping

# Verificar API cátedra
curl -s http://192.168.194.250:8080/management/health | jq .
```

---

## Solución Rápida de Errores Comunes

### Error: Token Expirado (401 Unauthorized)
```bash
TOKEN=$(curl -s -X POST http://localhost:8081/api/authenticate \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}' | jq -r '.id_token')
```

### Error: Sesión Expirada
```bash
# Iniciar nueva sesión
curl -s -X POST http://localhost:8081/api/compra/iniciar \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"eventoId\": ${EVENTO_ID}}" | jq .
```

### Error: Evento sin idCatedra
```bash
# Sincronizar eventos
curl -s -X POST http://localhost:8081/api/eventos/sincronizar-todo \
  -H "Authorization: Bearer $TOKEN" | jq .

# Elegir evento con idCatedra
curl -s http://localhost:8081/api/eventos \
  -H "Authorization: Bearer $TOKEN" | \
  jq '.[] | select(.idCatedra != null) | {id, titulo, idCatedra}'
```

### Error: Asiento No Disponible
```bash
# Ver disponibilidad actualizada
curl -s "http://localhost:8081/api/eventos/${EVENTO_ID}/asientos/disponibilidad" \
  -H "Authorization: Bearer $TOKEN" | \
  jq '.asientos[] | select(.estado == "DISPONIBLE")'
```

---

## Notas Importantes

- **Sesión:** Expira en 30 minutos de inactividad
- **Bloqueo de asientos:** Dura 5 minutos (se re-bloquea automáticamente al confirmar)
- **Máximo de asientos:** 4 por compra
- **Nombres:** Mínimo 3 caracteres
- **Token JWT:** Expira después de cierto tiempo (regenerar si es necesario)
- **Estados de sesión:**
  - `SELECCION_ASIENTOS` → Sesión creada, esperando selección
  - `CARGA_DATOS` → Asientos seleccionados, esperando nombres y confirmación
  - `COMPLETADO` → Compra finalizada exitosamente

---
