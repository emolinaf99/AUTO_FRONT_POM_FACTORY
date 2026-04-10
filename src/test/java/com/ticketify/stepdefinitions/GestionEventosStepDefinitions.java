package com.ticketify.stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class GestionEventosStepDefinitions {

    @Given("el administrador se encuentra en el panel de gestion de eventos")
    public void elAdministradorSeEncuentraEnElPanelDeGestionDeEventos() {
    }

    @When("abre el formulario de creacion de un nuevo evento")
    public void abreElFormularioDeCreacionDeUnNuevoEvento() {
    }

    @And("ingresa el nombre {string} y la fecha {string}")
    public void ingresaElNombreYLaFecha(String nombreEvento, String fechaEvento) {
    }

    @And("confirma la creacion del evento")
    public void confirmaLaCreacionDelEvento() {
    }

    @Then("el sistema registra el evento exitosamente")
    public void elSistemaRegistraElEventoExitosamente() {
    }

    @And("el evento {string} es visible en el listado de eventos")
    public void elEventoEsVisibleEnElListadoDeEventos(String nombreEvento) {
    }

    @And("intenta confirmar la creacion sin ingresar {string}")
    public void intentaConfirmarLaCreacionSinIngresar(String campoRequerido) {
    }

    @Then("el sistema impide el registro del evento")
    public void elSistemaImpideElRegistroDelEvento() {
    }

    @And("muestra un mensaje indicando que {string} es obligatorio")
    public void muestraUnMensajeIndicandoQueEsObligatorio(String campoRequerido) {
    }
}
