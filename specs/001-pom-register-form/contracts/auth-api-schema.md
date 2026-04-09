# Contrato API: AuthService — Registro de Usuario

**Servicio**: AuthService  
**Base URL**: `http://localhost:8003`  
**Propósito en esta HU**: Crear el usuario de prueba en el hook `@Before` de CP-HU1-08

---

## POST /api/auth/register

### Request

**Method**: `POST`  
**Path**: `/api/auth/register`  
**Content-Type**: `application/json`

**Body**:

```json
{
  "firstName": "string (requerido)",
  "lastName":  "string (requerido)",
  "email":     "string (requerido, formato email, único)",
  "password":  "string (requerido)",
  "roles":     "array (puede ser vacío [])"
}
```

**Ejemplo (precondición CP-HU1-08)**:

```json
{
  "firstName": "Test",
  "lastName":  "User",
  "email":     "test.a1b2c3d4@testmail.com",
  "password":  "Test1234!",
  "roles":     []
}
```

### Responses

| Código | Condición | Body |
|--------|-----------|------|
| `201 Created` | Usuario creado exitosamente | `{ "message": "...", "userId": "..." }` o similar |
| `200 OK` | Variante aceptada según implementación | Idem |
| `409 Conflict` | El email ya existe en el sistema | `{ "error": "...", "message": "Email already registered" }` o similar |
| `400 Bad Request` | Datos inválidos (campos vacíos, email mal formado) | `{ "errors": [...] }` |

### Comportamiento esperado observado en las pruebas

| Escenario | Acción en hook `@Before` | Respuesta esperada del AuthService |
|-----------|--------------------------|-----------------------------------|
| CP-HU1-08 — precondición | `POST /api/auth/register` con email UUID único | `2xx` (usuario creado) |
| CP-HU1-08 — ejecución UI | El formulario Vue envía el mismo email | `409 Conflict` → frontend muestra `emailError` |

### Notas de implementación para `ApiHelper`

- Usar `java.net.http.HttpClient` (Java 21 built-in).
- El `Content-Type` del request debe ser `application/json`.
- Si la respuesta no es `2xx`, lanzar `RuntimeException` con el código recibido para fallar explícitamente el escenario en el hook.
- No leer ni parsear el body de respuesta — solo verificar el código HTTP.

```java
// Fragmento de referencia (no normativo)
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("http://localhost:8003/api/auth/register"))
    .header("Content-Type", "application/json")
    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
    .build();
HttpResponse<String> response = HttpClient.newHttpClient()
    .send(request, HttpResponse.BodyHandlers.ofString());
if (response.statusCode() < 200 || response.statusCode() >= 300) {
    throw new RuntimeException("Fallo al crear usuario de prueba. HTTP " + response.statusCode());
}
```
