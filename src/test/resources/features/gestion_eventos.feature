Feature: Gestion de eventos desde el panel de administracion
  Como usuario administrador del sistema de ticketing
  Quiero crear y gestionar eventos
  Para que los compradores puedan adquirir sus entradas

  Scenario Outline: El administrador registra un nuevo evento exitosamente
    Given el administrador se encuentra en el panel de gestion de eventos
    When abre el formulario de creacion de un nuevo evento
    And ingresa el nombre "<nombre_evento>" y la fecha "<fecha_evento>"
    And confirma la creacion del evento
    Then el sistema registra el evento exitosamente
    And el evento "<nombre_evento>" es visible en el listado de eventos

    Examples:
      | nombre_evento             | fecha_evento        |
      | Festival Ticketing 2025   | 2025-12-01T20:00    |

  Scenario Outline: El administrador no puede crear un evento sin "<campo_requerido>"
    Given el administrador se encuentra en el panel de gestion de eventos
    When abre el formulario de creacion de un nuevo evento
    And intenta confirmar la creacion sin ingresar "<campo_requerido>"
    Then el sistema impide el registro del evento
    And muestra un mensaje indicando que "<campo_requerido>" es obligatorio

    Examples:
      | campo_requerido        |
      | el nombre del evento   |
