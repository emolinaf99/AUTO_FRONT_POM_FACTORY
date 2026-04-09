# language: es

@autenticacion
Característica: Registro de usuario en FrontendTicketing (HU1)
  Como usuario nuevo del sistema
  Quiero poder registrarme con mis datos personales
  Para acceder a las funcionalidades del sistema de ticketing

  @CP-HU1-01 @positivo
  Escenario: CP-HU1-01 - Registro exitoso con datos válidos
    Dado que el usuario se encuentra en la página de registro
    Cuando completa el formulario con datos válidos
    Y envía el formulario de registro
    Entonces el sistema muestra el mensaje de confirmación de registro exitoso
    Y el usuario es redirigido a la página de inicio de sesión

  @CP-HU1-08 @negativo @email-duplicado
  Escenario: CP-HU1-08 - Registro rechazado por email duplicado
    Dado que el usuario se encuentra en la página de registro
    Y existe un usuario registrado con el mismo email en el sistema
    Cuando completa el formulario usando el email ya registrado
    Y envía el formulario de registro
    Entonces el sistema muestra un mensaje de error junto al campo correo
    Y el usuario no es redirigido a otra página
