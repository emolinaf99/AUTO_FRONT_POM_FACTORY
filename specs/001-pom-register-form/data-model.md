# Data Model: POM + Page Factory — Registro de Usuario (HU1)

**Feature**: `001-pom-register-form` | **Date**: 2026-04-08

---

## Entidades de dominio de prueba

### RegistroRequest

Datos enviados al formulario de registro (entrada de la prueba).

| Campo | Tipo | Restricciones | Notas |
|-------|------|---------------|-------|
| `firstName` | `String` | Requerido, no vacío | Nombre del usuario |
| `lastName` | `String` | Requerido, no vacío | Apellido del usuario |
| `email` | `String` | Requerido, formato email válido, único en el sistema | Para CP-HU1-08: debe ser un email ya registrado |
| `password` | `String` | Requerido, no vacío | Contraseña |
| `confirmPassword` | `String` | Requerido, debe ser igual a `password` | Confirmación de contraseña |

**Variantes por escenario**:

| Escenario | Valor de `email` | Unicidad |
|-----------|-----------------|----------|
| CP-HU1-01 | `"usuario." + UUID[0..7] + "@test.com"` | Único por ejecución |
| CP-HU1-08 | Generado en `@Before` con UUID, registrado vía API | Único por ejecución, ya existe en el sistema al momento del test |

---

### RegisterPage (Page Object)

Representación del formulario de registro en el DOM. Encapsula los localizadores y las acciones posibles.

| Elemento | Localizador `@FindBy` | Tipo WebElement | Acción expuesta |
|----------|-----------------------|-----------------|-----------------|
| Campo firstName | `@FindBy(id = "reg-firstName")` | `WebElement` | `completarNombre(String)` |
| Campo lastName | `@FindBy(id = "reg-lastName")` | `WebElement` | `completarApellido(String)` |
| Campo email | `@FindBy(id = "reg-email")` | `WebElement` | `completarEmail(String)` |
| Campo password | `@FindBy(id = "reg-password")` | `WebElement` | `completarPassword(String)` |
| Campo confirmPassword | `@FindBy(id = "reg-confirm")` | `WebElement` | `completarConfirmacion(String)` |
| Botón submit | `@FindBy(css = "button[type='submit']")` | `WebElement` | `enviarFormulario()` |
| Mensaje de confirmación | `@FindBy(css = "div.text-green-400")` | `WebElement` | `obtenerMensajeConfirmacion()` |
| Error campo email | `@FindBy(css = "#reg-email ~ p.text-red-400")` | `WebElement` | `obtenerErrorEmail()` |

**Notas de fragilidad**:
- `div.text-green-400` y `#reg-email ~ p.text-red-400` dependen de clases utilitarias de Tailwind CSS. Son funcionales con el diseño actual pero frágiles ante cambios de paleta. Mejora futura: añadir `data-testid` a estos elementos en `RegisterForm.vue`.

---

### TestContext

Objeto de contexto compartido entre el hook `@Before` y los step definitions, gestionado por PicoContainer.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `emailDuplicado` | `String` | Email generado en el hook `@Before` de CP-HU1-08 y consumido en el step definition del mismo escenario |

---

### ApiHelper (utilitario)

Encapsula la llamada HTTP para registro de usuario de prueba. Usado exclusivamente por `AutenticacionHooks`.

| Método | Firma | Comportamiento |
|--------|-------|----------------|
| `registrarUsuario` | `static void registrarUsuario(String email, String password)` | Invoca `POST http://localhost:8003/api/auth/register` con cuerpo JSON `{firstName, lastName, email, password, roles:[]}`. Lanza `RuntimeException` si la respuesta HTTP no es 2xx. |

**Payload enviado por `ApiHelper`**:

```json
{
  "firstName": "Test",
  "lastName": "User",
  "email": "<email-generado-con-uuid>",
  "password": "Test1234!",
  "roles": []
}
```

---

## Estados del sistema observados por las pruebas

| Estado | Condición | Elemento DOM verificado |
|--------|-----------|------------------------|
| Registro exitoso | AuthService responde 2xx y frontend renderiza `showSuccess = true` | `div.text-green-400` visible |
| Redirección post-registro | URL cambia a `/login` | URL actual contiene `/login` |
| Email duplicado | AuthService responde 409 y frontend renderiza `emailError` | `#reg-email ~ p.text-red-400` visible con texto no vacío |
| Sin redirección (error) | URL permanece en `/register` | URL actual no contiene `/login` |

---

## Flujo de datos entre componentes

```
@Before (AutenticacionHooks)
  │  genera email UUID
  │  llama ApiHelper.registrarUsuario(email, "Test1234!")
  │  guarda email en TestContext.emailDuplicado
  ▼
AutenticacionStepDefinitions
  │  lee TestContext.emailDuplicado (CP-HU1-08)
  │  llama RegisterPage.completar*(...)
  │  llama RegisterPage.enviarFormulario()
  ▼
RegisterPage
  │  interactúa con DOM via @FindBy locators
  │  expone obtenerMensajeConfirmacion() / obtenerErrorEmail()
  ▼
Aserciones en AutenticacionStepDefinitions
  │  verifica visibilidad del mensaje esperado
  │  verifica URL actual
```
