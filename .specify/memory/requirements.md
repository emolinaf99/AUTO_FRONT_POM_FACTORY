# Requirements — AUTO_FRONT_POM_FACTORY
## Feature: Autenticación de Usuarios — Formulario de Registro (HU1)

**Patrón**: POM + Page Factory (`@FindBy`)
**App bajo prueba**: FrontendTicketing en `http://localhost:5173`
**Contexto**: Ver TEST_CASES.md en authService/ para la especificación completa.

---

## Restricción de Scope

Este repositorio cubre **exclusivamente** los escenarios de **registro de usuario (HU1)**.
- Login / logout / cuenta bloqueada → AUTO_FRONT_SCREENPLAY
- Pruebas de API REST → AUTO_API_SCREENPLAY

---

## HU1: Registro de Nuevo Usuario Comprador

**Como** un nuevo visitante de la plataforma,
**Quiero** poder crear una cuenta de usuario,
**Para** tener una identidad única en el sistema.

### Ruta bajo prueba
`http://localhost:5173/register`

### Campos del formulario (todos obligatorios)
| Campo | ID/selector esperado | Tipo |
|-------|---------------------|------|
| Nombre | `#reg-firstName` | text |
| Apellido | `#reg-lastName` | text |
| Correo Electrónico | `#reg-email` | email |
| Contraseña | `#reg-password` | password |
| Confirmar Contraseña | `#reg-confirm` | password |
| Botón enviar | `button[type="submit"]` | button |

### Datos de prueba válidos
```
firstName:       "Carlos"
lastName:        "Gómez"
email:           "carlos.gomez@sofka.com"
password:        "SofkaTech2026!"
confirmPassword: "SofkaTech2026!"
```

---

## Casos de Prueba — Obligatorios

### CP-HU1-01: Registro exitoso
**Tipo**: Flujo Básico (Positivo) | **Prioridad**: Obligatorio

```gherkin
Dado que un nuevo visitante se encuentra en el formulario de registro de la plataforma
Cuando ingresa "Carlos" en el campo Nombre
  Y ingresa "Gómez" en el campo Apellido
  Y ingresa "carlos.gomez@dominio.com" en el campo Correo Electrónico
  Y ingresa "SofkaTech2026!" en el campo Contraseña
  Y ingresa "SofkaTech2026!" en el campo Confirmar Contraseña
  Y envía el formulario de registro
Entonces el sistema crea la cuenta de usuario correctamente
  Y muestra un mensaje de confirmación de registro exitoso en pantalla
  Y redirige automáticamente al usuario a la página de inicio de sesión
```

**Resultado esperado**: mensaje de éxito visible ~2 segundos → redirección a `/login`
**Precondición**: el email `carlos.gomez@dominio.com` no existe en el sistema

---

### CP-HU1-08: Registro con email duplicado
**Tipo**: Excepción (Negativo) | **Prioridad**: Obligatorio | **Regla de Negocio**: RN1

```gherkin
Dado que existe un usuario previamente registrado con el correo "usuario.existente@dominio.com"
  Y un nuevo visitante se encuentra en el formulario de registro de la plataforma
Cuando ingresa datos válidos en los campos de Nombre, Apellido y Contraseñas
  Pero ingresa "usuario.existente@dominio.com" en el campo Correo Electrónico
  Y envía el formulario de registro
Entonces el sistema responde con un error de conflicto HTTP 409
  Y muestra el mensaje informativo "Este correo electrónico ya está en uso. ¿Deseas iniciar sesión?"
```

**Resultado esperado**: mensaje de error junto al campo correo electrónico, sin redirección
**Precondición**: registrar previamente el usuario con `usuario.existente@dominio.com` vía API o setup

---

## Casos de Prueba — Recomendados (extras)

### CP-HU1-02: Campos vacíos
**Tipo**: Flujo Alterno (Negativo)

```gherkin
Dado que un nuevo visitante se encuentra en el formulario de registro de la plataforma
Cuando deja vacíos los campos de Nombre, Apellido, Correo Electrónico, Contraseña y Confirmar Contraseña
  Y envía el formulario de registro
Entonces el sistema impide el envío del formulario
  Y muestra un mensaje de error específico junto a cada campo indicando que es obligatorio
```

---

### CP-HU1-03: Formato de correo inválido
**Tipo**: Flujo Alterno (Negativo) | **Técnica**: Partición de Equivalencia

```gherkin
Dado que un nuevo visitante se encuentra en el formulario de registro de la plataforma
Cuando ingresa datos válidos en los campos de Nombre, Apellido y Contraseñas
  Pero ingresa el valor "carlos.gomez.dominio.com" en el campo Correo Electrónico
  Y envía el formulario de registro
Entonces el sistema impide el registro
  Y muestra un mensaje de error en el campo de correo indicando que el formato no es válido
```

---

### CP-HU1-04: Contraseñas no coinciden
**Tipo**: Flujo Alterno (Negativo)

```gherkin
Dado que un nuevo visitante se encuentra en el formulario de registro de la plataforma
Cuando ingresa datos válidos en los campos de Nombre, Apellido y Correo Electrónico
  Y ingresa "SofkaTech2026!" en el campo Contraseña
  Pero ingresa "Diferente2026*" en el campo Confirmar Contraseña
  Y envía el formulario de registro
Entonces el sistema impide el registro
  Y muestra un mensaje de error indicando que las contraseñas no coinciden
```

---

### CP-HU1-05: Contraseña por debajo del límite mínimo
**Tipo**: Flujo Alterno (Negativo) | **Técnica**: Análisis de Valores Límite — RN2 (mín. 8 chars)

```gherkin
Dado que un nuevo visitante se encuentra en el formulario de registro de la plataforma
Cuando ingresa datos válidos en los campos de Nombre, Apellido y Correo Electrónico
  Y ingresa una contraseña de 7 caracteres "Sofka1!" en el campo Contraseña y Confirmar Contraseña
  Y envía el formulario de registro
Entonces el sistema impide el registro
  Y muestra un mensaje de error indicando que la contraseña debe tener una longitud mínima de 8 caracteres
```

---

## Reglas de Negocio Validadas en UI

| ID | Regla | Validación |
|----|-------|------------|
| RN1 | Email único en BD | Solo backend — frontend muestra mensaje 409 |
| RN2 | Contraseña: mín. 8 chars, 1 mayúscula, 1 carácter especial | Cliente antes de enviar |
| — | Confirmación de contraseña coincide | Cliente antes de enviar |
| — | Campos obligatorios completos | Cliente antes de enviar |
| — | Formato de email válido | Cliente antes de enviar |

---

## Notas de Implementación

- La validación de campos vacíos y formato ocurre en el cliente (sin llamada al API).
- El mensaje de email duplicado aparece junto al campo correo, no como alerta global.
- La redirección tras registro exitoso ocurre automáticamente tras ~2000ms.
- `confirmPassword` se envía al API — el backend también valida coincidencia con `[Compare]`.
- Los campos de contraseña NO aplican `trim()` — los espacios son caracteres válidos.
- Los campos de texto (nombre, apellido, email) sí aplican `trim()` antes de enviar.
