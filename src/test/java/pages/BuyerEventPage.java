package pages;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import org.openqa.selenium.support.FindBy;

import java.time.Duration;

public class BuyerEventPage extends PageObject {

    @FindBy(xpath = "//h2[normalize-space()='Compra de Tickets']")
    private WebElementFacade purchaseHeading;

    @FindBy(id = "quantity")
    private WebElementFacade quantityInput;

    @FindBy(id = "email")
    private WebElementFacade emailInput;

    @FindBy(id = "expires")
    private WebElementFacade expiresInput;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElementFacade submitButton;

    @FindBy(xpath = "//h2[normalize-space()='Completar Pago']")
    private WebElementFacade completePaymentHeading;

    @FindBy(xpath = "//*[contains(normalize-space(.), 'El email es requerido')]")
    private WebElementFacade emailRequiredMessage;

    public void openEventDetail(int eventId) {
        openUrl(baseUrl() + "/buy/" + eventId);
    }

    public void waitUntilPurchaseFormVisible() {
        withTimeoutOf(Duration.ofSeconds(20)).waitFor(purchaseHeading);
    }

    public void setQuantity(String quantity) {
        quantityInput.clear();
        quantityInput.type(quantity);
    }

    public void setEmail(String email) {
        emailInput.clear();
        emailInput.type(email);
    }

    public void clearEmail() {
        emailInput.clear();
    }

    public void setExpiration(String expiration) {
        expiresInput.clear();
        expiresInput.type(expiration);
    }

    public void submitPurchase() {
        submitButton.click();
    }

    public void waitUntilPaymentStepVisible() {
        withTimeoutOf(Duration.ofSeconds(30)).waitFor(completePaymentHeading);
    }

    public void waitUntilEmailRequiredMessageVisible() {
        withTimeoutOf(Duration.ofSeconds(10)).waitFor(emailRequiredMessage);
    }

    public boolean isPurchaseFormVisible() {
        return purchaseHeading.isVisible();
    }

    public boolean isPaymentStepVisible() {
        return completePaymentHeading.isCurrentlyVisible();
    }

    private String baseUrl() {
        return EnvironmentSpecificConfiguration.from(ConfiguredEnvironment.getEnvironmentVariables())
                .getProperty("webdriver.base.url");
    }
}
