package steps;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import org.assertj.core.api.Assertions;
import pages.BuyPage;
import pages.BuyerEventPage;
import support.TestDataSetup;
import support.TestDataSetup.PreparedEvent;

public class TicketPurchaseSteps {

    private final TestDataSetup testDataSetup = new TestDataSetup();

    private BuyPage buyPage;
    private BuyerEventPage buyerEventPage;
    private PreparedEvent preparedEvent;

    @Step("Preparar un evento futuro con tickets disponibles")
    public void prepareEventWithAvailableTickets() {
        preparedEvent = testDataSetup.prepareFutureEventWithAvailableTickets();
    }

    @Step("Navegar a la vista pública de compra")
    public void openPurchaseView() {
        buyPage.openPurchaseListing();
        buyPage.waitUntilReady();
    }

    @Step("Seleccionar el evento preparado y completar una compra válida")
    public void selectEventAndSubmitValidPurchase() {
        buyPage.openPreparedEvent(preparedEvent.id(), preparedEvent.name());
        buyerEventPage.waitUntilPurchaseFormVisible();
        buyerEventPage.setQuantity("1");
        buyerEventPage.setEmail(buyerEmail());
        buyerEventPage.setExpiration(expirationSeconds());
        buyerEventPage.submitPurchase();
    }

    @Step("Validar que el sistema avanza al paso Completar Pago")
    public void shouldAdvanceToCompletePayment() {
        buyerEventPage.waitUntilPaymentStepVisible();
        Assertions.assertThat(buyerEventPage.isPaymentStepVisible()).isTrue();
    }

    @Step("Abrir el formulario de compra del evento preparado")
    public void openPreparedEventForm() {
        buyerEventPage.openEventDetail(preparedEvent.id());
        buyerEventPage.waitUntilPurchaseFormVisible();
    }

    @Step("Intentar comprar tickets sin ingresar email")
    public void submitPurchaseWithoutEmail() {
        buyerEventPage.setQuantity("1");
        buyerEventPage.clearEmail();
        buyerEventPage.setExpiration(expirationSeconds());
        buyerEventPage.submitPurchase();
    }

    @Step("Validar el mensaje El email es requerido")
    public void shouldSeeEmailRequiredMessage() {
        buyerEventPage.waitUntilEmailRequiredMessageVisible();
    }

    @Step("Validar que el usuario permanece en el formulario de compra")
    public void shouldRemainOnPurchaseForm() {
        Assertions.assertThat(buyerEventPage.isPurchaseFormVisible()).isTrue();
        Assertions.assertThat(buyerEventPage.isPaymentStepVisible()).isFalse();
    }

    private String buyerEmail() {
        String email = EnvironmentSpecificConfiguration.from(ConfiguredEnvironment.getEnvironmentVariables())
            .getProperty("testdata.buyer.email");
        return (email == null || email.isBlank()) ? "comprador@ejemplo.com" : email;
    }

    private String expirationSeconds() {
        String expiration = EnvironmentSpecificConfiguration.from(ConfiguredEnvironment.getEnvironmentVariables())
            .getProperty("testdata.reservation.expiresInSeconds");
        return (expiration == null || expiration.isBlank()) ? "300" : expiration;
    }
}
