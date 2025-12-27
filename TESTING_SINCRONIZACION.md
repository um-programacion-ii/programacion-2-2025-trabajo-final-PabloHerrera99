# 🧪 Guía de Testing - Sincronización de Eventos

## 📋 Índice

1. [Prerequisitos](#prerequisitos)
2. [Test 1: Estado Inicial de la BD](#test-1-estado-inicial-de-la-bd)
3. [Test 2: Conectividad con Servidor Cátedra](#test-2-conectividad-con-servidor-cátedra)
4. [Test 3: Primera Sincronización (Crear)](#test-3-primera-sincronización-crear)
5. [Test 4: Verificar Datos en BD](#test-4-verificar-datos-en-bd)
6. [Test 5: Segunda Sincronización (Update)](#test-5-segunda-sincronización-update)
7. [Test 6: Sincronización de Integrantes](#test-6-sincronización-de-integrantes)
8. [Test 7: Desactivación de Eventos](#test-7-desactivación-de-eventos-opcional)
9. [Test 8: Manejo de Errores](#test-8-manejo-de-errores-opcional)
10. [Checklist de Verificación](#checklist-de-verificación)

---

## Prerequisitos

### ✅ Servicios Requeridos

| Servicio | Puerto | Estado | Comando de Verificación |
|----------|--------|--------|-------------------------|
| Backend | 8081 | ✅ Debe estar corriendo | `curl http://localhost:8081/management/health` |
| H2 Database | - | ✅ Embebida en Backend | Automático |
| Servidor Cátedra | 8080 | ⚠️ Debe ser accesible | `curl -I http://192.168.194.250:8080` |

### 🚀 Iniciar Backend

```bash
cd Backend
./mvnw spring-boot:run
```

**Verificar en logs:**
```
Started BackendApp in X.XXX seconds
```

### 🌐 Acceder a H2 Console

1. **URL:** http://localhost:8081/h2-console
2. **Configuración:**
   - **JDBC URL:** `jdbc:h2:file:./target/h2db/db/finalProgramacion`
   - **User Name:** `finalProgramacion`
   - **Password:** *(dejar vacío)*
3. **Botón:** Connect

---

## Test 1: Estado Inicial de la BD

### 🎯 Objetivo
Verificar que la base de datos está vacía antes de la primera sincronización.

### 📝 Pasos

**En H2 Console, ejecutar:**

```sql
SELECT 'eventos' as tabla, COUNT(*) as cantidad FROM evento
UNION ALL
SELECT 'evento_tipo', COUNT(*) FROM evento_tipo
UNION ALL
SELECT 'integrantes', COUNT(*) FROM integrante;
```

### ✅ Resultado Esperado

```
tabla          | cantidad
---------------|----------
eventos        | 0
evento_tipo    | 0
integrantes    | 0
```

### ⚠️ Si hay datos previos

**Limpiar BD (opcional):**
```sql
DELETE FROM integrante;
DELETE FROM evento;
DELETE FROM evento_tipo;
```

---

## Test 2: Conectividad con Servidor Cátedra

### 🎯 Objetivo
Verificar que el servidor de cátedra está accesible.

### 📝 Pasos

**En terminal, ejecutar:**

```bash
curl -I http://192.168.194.250:8080/api/authenticate
```

### ✅ Resultado Esperado

```
HTTP/1.1 401 Unauthorized
o
HTTP/1.1 200 OK
```
*(Cualquiera indica que el servidor responde)*

### ❌ Si falla

**Posibles errores:**
```
curl: (7) Failed to connect: Connection refused
curl: (28) Connection timed out
```

**Verificar:**
- Red/VPN conectada
- Firewall no bloqueando puerto 8080
- Servidor de cátedra realmente disponible

---

## Test 3: Primera Sincronización (Crear)

### 🎯 Objetivo
Sincronizar eventos desde cátedra por primera vez (operación CREATE).

### 📝 Pasos

**En terminal, ejecutar:**

```bash
curl -X POST http://localhost:8081/api/eventos/sincronizar-todo \
  -H "Content-Type: application/json" \
  -w "\n\nHTTP Status: %{http_code}\nTime: %{time_total}s\n"
```

### 📊 Monitorear Logs del Backend

**Buscar en la terminal donde corre el Backend:**

```log
INFO  - === INICIANDO SINCRONIZACIÓN DE EVENTOS ===
INFO  - Consultando eventos desde API de cátedra...
DEBUG - Realizando login a: http://192.168.194.250:8080/api/authenticate
INFO  - ✓ Login exitoso, token obtenido: eyJhbG...
INFO  - Eventos obtenidos desde cátedra: 5
DEBUG - --- Procesando evento: idCatedra=123, titulo=Concierto de Rock
INFO  - Creando nuevo EventoTipo: Música
INFO  - ✓ Evento creado: ID=1, idCatedra=123, titulo='Concierto de Rock'
DEBUG -   → 3 integrantes sincronizados
...
INFO  - === SINCRONIZACIÓN COMPLETADA ===
INFO  - Resultados:
INFO  -   - Eventos creados: 5
INFO  -   - Eventos actualizados: 0
INFO  -   - Eventos desactivados: 0
INFO  -   - Errores: 0
```

### ✅ Resultado Esperado (Response JSON)

```json
{
  "created": 5,
  "updated": 0,
  "deactivated": 0,
  "errors": []
}

HTTP Status: 200
Time: 2.5s
```

### 📋 Análisis de Resultados

| Campo | Valor Esperado | Significado |
|-------|----------------|-------------|
| `created` | > 0 | Eventos nuevos creados exitosamente |
| `updated` | 0 | Primera vez, no hay nada que actualizar |
| `deactivated` | 0 | No hay eventos para desactivar |
| `errors` | [] | Sin errores |
| HTTP Status | 200 | Operación exitosa |

### ❌ Posibles Errores

**Error de autenticación:**
```json
{
  "created": 0,
  "updated": 0,
  "deactivated": 0,
  "errors": ["Error crítico durante sincronización: 401 Unauthorized"]
}
```
**Solución:** Verificar credenciales en `application-dev.yml`

**Servidor no accesible:**
```json
{
  "created": 0,
  "updated": 0,
  "deactivated": 0,
  "errors": ["Error crítico durante sincronización: Connection refused"]
}
```
**Solución:** Verificar conectividad (Test 2)

---

## Test 4: Verificar Datos en BD

### 🎯 Objetivo
Confirmar que los datos se guardaron correctamente en la base de datos.

### 📝 Query 1: Contadores Generales

**En H2 Console:**

```sql
SELECT 'eventos' as tabla, COUNT(*) as cantidad FROM evento
UNION ALL
SELECT 'evento_tipo', COUNT(*) FROM evento_tipo
UNION ALL
SELECT 'integrantes', COUNT(*) FROM integrante;
```

**Esperado:** Números > 0

### 📝 Query 2: Ver Eventos Creados

```sql
SELECT 
    id, 
    id_catedra, 
    titulo, 
    fecha,
    activo,
    precio_entrada,
    evento_tipo_id
FROM evento 
ORDER BY id;
```

### ✅ Verificaciones

- ✅ `id_catedra` tiene valores (no NULL)
- ✅ `titulo` tiene texto legible
- ✅ `activo` = TRUE
- ✅ `fecha` tiene timestamp válido
- ✅ `precio_entrada` > 0
- ✅ `evento_tipo_id` apunta a un tipo válido

### 📝 Query 3: Ver Tipos de Evento

```sql
SELECT id, nombre, descripcion 
FROM evento_tipo
ORDER BY nombre;
```

**Esperado:** Nombres lógicos como "Música", "Teatro", "Deportes", etc.

### 📝 Query 4: Ver Integrantes con sus Eventos

```sql
SELECT 
    i.id,
    i.nombre,
    i.apellido,
    i.identificacion,
    e.titulo as evento_titulo,
    e.id_catedra as evento_id_catedra
FROM integrante i
JOIN evento e ON i.evento_id = e.id
ORDER BY e.titulo, i.apellido;
```

### ✅ Verificaciones

- ✅ Integrantes tienen nombre y apellido
- ✅ Están asociados a eventos válidos
- ✅ Campo `identificacion` poblado

### 📝 Query 5: Verificar Integridad Referencial

```sql
-- Verificar que todos los eventos tienen tipo
SELECT COUNT(*) as eventos_sin_tipo
FROM evento 
WHERE evento_tipo_id IS NULL;

-- Verificar que todos los integrantes tienen evento
SELECT COUNT(*) as integrantes_sin_evento
FROM integrante 
WHERE evento_id IS NULL;
```

**Esperado:** Ambos contadores = 0 (sin registros huérfanos)

---

## Test 5: Segunda Sincronización (Update)

### 🎯 Objetivo
Verificar que la lógica de UPSERT funciona correctamente (actualización de eventos existentes).

### 📝 Pasos

**En terminal, ejecutar NUEVAMENTE:**

```bash
curl -X POST http://localhost:8081/api/eventos/sincronizar-todo \
  -H "Content-Type: application/json"
```

### ✅ Resultado Esperado

```json
{
  "created": 0,      // Ahora 0 porque ya existen
  "updated": 5,      // Todos fueron actualizados
  "deactivated": 0,
  "errors": []
}
```

### 📊 Logs Esperados

```log
DEBUG - Actualizando evento existente: idCatedra=123, titulo=...
DEBUG - ✓ Evento actualizado: ID=1, idCatedra=123
```

### 📝 Verificar en H2 Console

```sql
-- Los IDs (PK) NO deben cambiar
SELECT id, id_catedra, titulo 
FROM evento 
ORDER BY id;
```

### ✅ Verificaciones Críticas

- ✅ Los `id` (PK autogenerada) son los **mismos** que en Test 4
- ✅ Los datos pueden haber cambiado (si hubo modificaciones en cátedra)
- ✅ No se crearon registros duplicados

---

## Test 6: Sincronización de Integrantes

### 🎯 Objetivo
Verificar que la estrategia de integrantes (delete + re-insert) funciona correctamente.

### 📝 Antes de Segunda Sincronización

**En H2 Console:**

```sql
-- Anotar IDs de integrantes actuales
SELECT id, nombre, apellido, evento_id 
FROM integrante 
ORDER BY id;
```

**Guardar los IDs para comparar después.**

### 📝 Después de Segunda Sincronización (Test 5)

**Ejecutar nuevamente:**

```sql
SELECT id, nombre, apellido, evento_id 
FROM integrante 
ORDER BY id;
```

### ✅ Verificaciones

- ✅ Los `id` de integrantes **SÍ cambiaron** (porque se borran y recrean)
- ✅ Los nombres/apellidos son los correctos
- ✅ El total puede ser igual o diferente (depende de cambios en cátedra)
- ✅ Todos los integrantes tienen `evento_id` válido

### 📝 Verificar Que No Quedaron Integrantes Huérfanos

```sql
-- Contar integrantes
SELECT COUNT(*) as total_integrantes FROM integrante;

-- Verificar que cada evento tiene sus integrantes
SELECT 
    e.id,
    e.titulo,
    COUNT(i.id) as cantidad_integrantes
FROM evento e
LEFT JOIN integrante i ON e.id = i.evento_id
GROUP BY e.id, e.titulo
ORDER BY e.id;
```

---

## Test 7: Desactivación de Eventos (Opcional)

### 🎯 Objetivo
Verificar que eventos que desaparecen de cátedra se marcan como `activo=false`.

### ⚠️ Nota
Este test requiere crear un evento "fantasma" para simular uno que no existe en cátedra.

### 📝 Paso 1: Crear Evento Fantasma

**En H2 Console:**

```sql
-- Insertar evento de prueba que NO existe en cátedra
INSERT INTO evento (
    id, 
    id_catedra, 
    titulo, 
    descripcion, 
    fecha, 
    precio_entrada, 
    activo, 
    evento_tipo_id,
    fila_asientos,
    columna_asientos
) VALUES (
    999, 
    99999, 
    'Evento Fantasma de Prueba', 
    'Este evento será desactivado', 
    CURRENT_TIMESTAMP(), 
    100.00, 
    TRUE, 
    (SELECT MIN(id) FROM evento_tipo),
    10,
    10
);

-- Verificar que se creó
SELECT id, id_catedra, titulo, activo 
FROM evento 
WHERE id = 999;
```

**Esperado:** `activo = TRUE`

### 📝 Paso 2: Ejecutar Sincronización

```bash
curl -X POST http://localhost:8081/api/eventos/sincronizar-todo \
  -H "Content-Type: application/json"
```

### ✅ Resultado Esperado

```json
{
  "created": 0,
  "updated": 5,
  "deactivated": 1,   // El evento fantasma fue desactivado
  "errors": []
}
```

### 📊 Logs Esperados

```log
INFO  - Desactivando evento faltante: ID=999, idCatedra=99999, titulo='Evento Fantasma de Prueba'
INFO  - Total de eventos desactivados: 1
```

### 📝 Paso 3: Verificar en BD

```sql
-- El evento 999 debe estar ahora INACTIVO
SELECT id, id_catedra, titulo, activo 
FROM evento 
WHERE id = 999;
```

**Esperado:** `activo = FALSE`

### 📝 Paso 4: Limpiar (Opcional)

```sql
-- Eliminar evento de prueba
DELETE FROM evento WHERE id = 999;
```

---

## Test 8: Manejo de Errores (Opcional)

### 🎯 Objetivo
Verificar que el sistema maneja errores de forma robusta sin crashear.

### ⚠️ Nota
Este test requiere reiniciar el Backend con configuración incorrecta.

### 📝 Escenario 1: Servidor No Disponible

**Modificar configuración temporal:**

```bash
# En terminal, exportar variable de entorno con URL incorrecta
export CATEDRA_BASE_URL=http://localhost:9999  # Puerto que no existe

# Reiniciar Backend
cd Backend
./mvnw spring-boot:run
```

**Ejecutar sincronización:**

```bash
curl -X POST http://localhost:8081/api/eventos/sincronizar-todo
```

### ✅ Resultado Esperado

```json
{
  "created": 0,
  "updated": 0,
  "deactivated": 0,
  "errors": ["Error crítico durante sincronización: Connection refused"]
}
```

### 📊 Logs Esperados

```log
ERROR - ✗ Error en login a API de cátedra: Connection refused
ERROR - Error crítico durante sincronización: ...
```

### ✅ Verificaciones

- ✅ Backend NO crashea
- ✅ Retorna HTTP 200 con errores en JSON
- ✅ BD queda en estado consistente (sin datos corruptos)

### 📝 Restaurar Configuración

```bash
# Eliminar variable de entorno
unset CATEDRA_BASE_URL

# Reiniciar Backend normalmente
./mvnw spring-boot:run
```

---

## Checklist de Verificación

### ✅ Funcionalidad Básica

- [ ] Backend inicia sin errores
- [ ] Endpoint `/api/eventos/sincronizar-todo` responde
- [ ] Autenticación con cátedra funciona (token JWT obtenido)
- [ ] Eventos se descargan correctamente

### ✅ Operaciones CRUD

- [ ] Eventos nuevos se **crean** (`created > 0`)
- [ ] Eventos existentes se **actualizan** (`updated > 0`)
- [ ] Eventos faltantes se **desactivan** (`deactivated > 0`)

### ✅ Integridad de Datos

- [ ] EventoTipo se crea automáticamente si no existe
- [ ] Relaciones `evento.evento_tipo_id` son válidas
- [ ] Relaciones `integrante.evento_id` son válidas
- [ ] Campos obligatorios están poblados (titulo, fecha, precio)
- [ ] Campo `activo` = TRUE para eventos sincronizados
- [ ] Campo `activo` = FALSE para eventos desactivados

### ✅ Sincronización de Integrantes

- [ ] Integrantes se crean correctamente
- [ ] Integrantes viejos se borran en re-sincronización
- [ ] Nuevos integrantes se insertan
- [ ] No quedan integrantes huérfanos

### ✅ Manejo de Errores

- [ ] Errores se capturan (no crash del Backend)
- [ ] Errores se reportan en JSON response
- [ ] Logs muestran información de debug útil
- [ ] BD queda en estado consistente tras errores

### ✅ Performance

- [ ] Sincronización completa < 5 segundos (depende de cantidad)
- [ ] Sin memory leaks (Backend estable tras múltiples syncs)
- [ ] Logs no excesivos (nivel apropiado)

---

## 📊 Resumen de Comandos Útiles

### Sincronización Manual

```bash
curl -X POST http://localhost:8081/api/eventos/sincronizar-todo \
  -H "Content-Type: application/json"
```

### Ver Logs en Tiempo Real

```bash
tail -f Backend/target/spring-boot-dev.log | grep -i -E "(sync|evento|error)"
```

### Limpiar BD Completamente

```sql
-- En H2 Console
DELETE FROM integrante;
DELETE FROM evento;
DELETE FROM evento_tipo;
```

### Verificación Rápida de Estado

```sql
-- En H2 Console
SELECT 
    (SELECT COUNT(*) FROM evento) as total_eventos,
    (SELECT COUNT(*) FROM evento WHERE activo = true) as eventos_activos,
    (SELECT COUNT(*) FROM evento_tipo) as total_tipos,
    (SELECT COUNT(*) FROM integrante) as total_integrantes;
```

---

## 🔗 Próximos Pasos

### Integración con Kafka + Proxy

Una vez completado el testing manual, continuar con:

1. **Iniciar Kafka + Zookeeper**
2. **Iniciar Proxy** (puerto 8082)
3. **Verificar que Proxy escucha topic Kafka:** `eventos-actualizacion`
4. **Simular mensaje Kafka** → Proxy llama a Backend → Backend sincroniza
5. **Verificar flujo end-to-end completo**

Ver documentación: `TESTING_KAFKA_INTEGRATION.md` (próximamente)

---

## 📚 Referencias

- **Arquitectura:** Ver `README.md` y `plan.md`
- **Configuración:** `Backend/src/main/resources/config/application-dev.yml`
- **Código fuente:**
  - `EventoSyncService.java` - Lógica de sincronización
  - `EventoSyncResource.java` - Endpoint REST
  - `CatedraApiClient.java` - Cliente HTTP
  - `CatedraAuthService.java` - Autenticación JWT

---

## ❓ Troubleshooting


### Problema: "Database not found"

**Solución:** Verificar JDBC URL: `jdbc:h2:file:./target/h2db/db/finalProgramacion` (con P mayúscula)

### Problema: 401 Unauthorized en cátedra

**Solución:** Verificar credenciales en `application-dev.yml`:
```yaml
catedra:
  api:
    username: pablo.herrera
    password: password123
```
### Borrar base de datos completamente

```sql
DELETE FROM asiento_vendido;
DELETE FROM venta;
DELETE FROM asiento_seleccionado;
DELETE FROM sesion;
DELETE FROM integrante;
DELETE FROM evento;
DELETE FROM evento_tipo;
```