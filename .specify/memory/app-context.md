# Contexto de la Aplicación Bajo Prueba

## Nombre del Sistema
TicketRush — Sistema de compra y reserva de tickets para eventos

## URL Base
`http://localhost:3000` (frontend Next.js)

## Servicios del Backend
| Servicio | Puerto | Responsabilidad |
|---|---|---|
| CRUD Service | `http://localhost:8002` | Gestión de eventos y tickets (GET/POST/PUT/DELETE) |
| Producer Service | `http://localhost:8001` | Reserva de tickets y procesamiento de pagos (asíncrono, RabbitMQ) |

## Páginas Disponibles

| Ruta | Descripción | Elementos clave visibles |
|---|---|---|
| `/buy` | Lista de eventos disponibles para comprar tickets | Heading "Compra de Tickets", tarjetas de eventos, mensaje "No hay eventos disponibles" si lista vacía |
| `/buy/[eventId]` | Detalle de un evento con lista de tickets reservables | Nombre del evento, botón "Reservar" por ticket |
| `/events/[eventId]` | Vista de detalle del evento | Información del evento |
| `/admin` | Panel de administración | Crear eventos, crear tickets, editar eventos |

## NO existe en la aplicación
- Pantalla de login / autenticación
- Registro de usuarios
- Perfil de usuario
- Roles con contraseña

## Flujo de Reserva de Ticket (flujo principal del negocio)
1. El usuario navega a `/buy`
2. Ve la lista de eventos disponibles
3. Hace clic en un evento → va a `/buy/[eventId]`
4. Ve los tickets disponibles y hace clic en "Reservar"
5. Se abre un dialog con formulario: campo **Email del comprador** (requerido) + campo tiempo de expiración (default 300s)
6. Al enviar sin email → toast de error: **"El email es requerido"**
7. Al enviar con email válido → dialog muestra estado "Procesando reserva..." → luego "Reserva encolada ✓" o "Reserva confirmada"

## Validaciones de UI verificables
| Acción | Resultado esperado observable |
|---|---|
| Navegar a `/buy` con eventos disponibles | Heading "Compra de Tickets" visible + tarjetas de eventos |
| Navegar a `/buy` sin eventos en BD | Mensaje "No hay eventos disponibles" visible |
| Abrir dialog de reserva y enviar sin email | Toast: "El email es requerido" |
| Abrir dialog de reserva y enviar con email válido | Mensaje "Reserva encolada ✓" o "Reserva confirmada" visible |

## Datos de Prueba Necesarios
- Para el flujo positivo de visualización: mínimo 1 evento en la BD
- Para el flujo de reserva: mínimo 1 evento con 1 ticket en estado `available`
- No se requieren credenciales de usuario (sin autenticación)
