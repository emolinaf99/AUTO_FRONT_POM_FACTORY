#language: es
Característica: Compra pública de tickets
  Como comprador de tickets
  Quiero iniciar o bloquear una compra según la validez del formulario
  Para avanzar correctamente al pago o recibir validaciones claras

  @happy-path @critico
  Escenario: El usuario inicia la compra con datos válidos
    Dado que existe un evento futuro con tickets disponibles preparado para la prueba
    Y el usuario navega a la vista pública de compra
    Cuando selecciona el evento disponible e ingresa una cantidad válida, un email válido y un tiempo de expiración mayor a 0
    Entonces el sistema avanza al paso Completar Pago

  @error-path
  Escenario: El sistema bloquea la compra cuando falta el email
    Dado que existe un evento futuro con tickets disponibles preparado para la prueba
    Y el usuario se encuentra en el formulario de compra del evento
    Cuando intenta comprar tickets sin ingresar email
    Entonces el sistema muestra el mensaje El email es requerido
    Y el usuario permanece en el formulario sin avanzar a Completar Pago
