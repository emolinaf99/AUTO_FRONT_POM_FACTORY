package steps;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import net.serenitybdd.annotations.Steps;

public class PurchaseStepDefinitions {

    @Steps
    private TicketPurchaseSteps ticketPurchaseSteps;

    @Dado("que existe un evento futuro con tickets disponibles preparado para la prueba")
    public void prepareFutureEvent() {
        ticketPurchaseSteps.prepareEventWithAvailableTickets();
    }

    @Y("el usuario navega a la vista pública de compra")
    public void openPurchaseView() {
        ticketPurchaseSteps.openPurchaseView();
    }

    @Cuando("selecciona el evento disponible e ingresa una cantidad válida, un email válido y un tiempo de expiración mayor a 0")
    public void submitValidPurchase() {
        ticketPurchaseSteps.selectEventAndSubmitValidPurchase();
    }

    @Entonces("el sistema avanza al paso Completar Pago")
    public void shouldAdvanceToCompletePayment() {
        ticketPurchaseSteps.shouldAdvanceToCompletePayment();
    }

    @Y("el usuario se encuentra en el formulario de compra del evento")
    public void openPreparedEventForm() {
        ticketPurchaseSteps.openPreparedEventForm();
    }

    @Cuando("intenta comprar tickets sin ingresar email")
    public void submitWithoutEmail() {
        ticketPurchaseSteps.submitPurchaseWithoutEmail();
    }

    @Entonces("el sistema muestra el mensaje El email es requerido")
    public void shouldSeeEmailRequiredMessage() {
        ticketPurchaseSteps.shouldSeeEmailRequiredMessage();
    }

    @Y("el usuario permanece en el formulario sin avanzar a Completar Pago")
    public void shouldRemainOnPurchaseForm() {
        ticketPurchaseSteps.shouldRemainOnPurchaseForm();
    }
}
