package pages;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import org.openqa.selenium.support.FindBy;

import java.time.Duration;
import java.util.List;

public class BuyPage extends PageObject {

    @FindBy(xpath = "//h1[normalize-space()='Compra de Tickets']")
    private WebElementFacade heading;

    @FindBy(xpath = "//a[starts-with(@href,'/buy/')]")
    private List<WebElementFacade> eventLinks;

    public void openPurchaseListing() {
        openUrl(baseUrl() + "/buy");
    }

    public void waitUntilReady() {
        withTimeoutOf(Duration.ofSeconds(20)).waitFor(heading);
    }

    public void openPreparedEvent(int eventId, String eventName) {
        String eventHref = "/buy/" + eventId;
        waitForTextToAppear(eventName);
        WebElementFacade matchingLink = eventLinks.stream()
                .filter(link -> eventHref.equals(link.getAttribute("href").replace(baseUrl(), "")))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No se encontró el evento preparado en la lista de compra: " + eventName + " (id=" + eventId + ")"));
        matchingLink.click();
    }

    private String baseUrl() {
        return EnvironmentSpecificConfiguration.from(ConfiguredEnvironment.getEnvironmentVariables())
                .getProperty("webdriver.base.url");
    }
}
