package com.ticketing.stepdefinitions;

import com.ticketing.ui.RegisterPage;
import com.ticketing.util.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.UUID;

public class AutenticacionStepDefinitions {

    private WebDriver driver;
    private RegisterPage registerPage;

    // ─── Paso compartido HU1 y HU2 ────────────────────────────────────────

    @Given("que el usuario se encuentra en la página de registro")
    public void elUsuarioSeEncuentraEnLaPaginaDeRegistro() {
        driver = ThucydidesWebDriverSupport.getDriver();
        driver.get("http://localhost:5173/register");
        registerPage = new RegisterPage(driver);
    }

    // ─── CP-HU1-01: Registro exitoso ───────────────────────────────────────

    @When("completa el formulario con datos válidos")
    public void completaElFormularioConDatosValidos() {
        String emailUnico = "usuario." + UUID.randomUUID().toString().substring(0, 8) + "@test.com";
        registerPage.completarNombre("Test");
        registerPage.completarApellido("Usuario");
        registerPage.completarEmail(emailUnico);
        registerPage.completarPassword("Test1234!");
        registerPage.completarConfirmacion("Test1234!");
    }

    @And("envía el formulario de registro")
    public void enviaElFormularioDeRegistro() {
        registerPage.enviarFormulario();
    }

    @Then("el sistema muestra el mensaje de confirmación de registro exitoso")
    public void elSistemaMuestraElMensajeDeConfirmacionDeRegistroExitoso() {
        WebElement mensaje = registerPage.obtenerMensajeConfirmacion();
        Assert.assertTrue(
                "El mensaje de confirmación no está visible",
                mensaje.isDisplayed()
        );
    }

    @And("el usuario es redirigido a la página de inicio de sesión")
    public void elUsuarioEsRedirigidoALaPaginaDeInicioDeSesion() {
        registerPage.esperarRedireccionA("/login");
        Assert.assertTrue(
                "La URL no contiene '/login'. URL actual: " + driver.getCurrentUrl(),
                driver.getCurrentUrl().contains("/login")
        );
    }

    // ─── CP-HU1-08: Email duplicado ──────────────────────────────────────

    @And("existe un usuario registrado con el mismo email en el sistema")
    public void existeUnUsuarioRegistradoConElMismoEmailEnElSistema() {
        // No-op: el hook @Before ya creó el usuario vía POST /api/auth/register.
        // Este paso documenta la precondición del escenario.
    }

    @When("completa el formulario usando el email ya registrado")
    public void completaElFormularioUsandoElEmailYaRegistrado() {
        registerPage.completarNombre("Test");
        registerPage.completarApellido("Duplicado");
        registerPage.completarEmail(TestContext.getEmailDuplicado());
        registerPage.completarPassword("Test1234!");
        registerPage.completarConfirmacion("Test1234!");
    }

    @Then("el sistema muestra un mensaje de error junto al campo correo")
    public void elSistemaMuestraUnMensajeDeErrorJuntoAlCampoCorreo() {
        WebElement errorElement = registerPage.obtenerErrorEmail();
        Assert.assertTrue(
                "El mensaje de error de email no está visible",
                errorElement.isDisplayed()
        );
        String textoError = errorElement.getText();
        Assert.assertTrue(
                "El mensaje de error no contiene el texto esperado. Texto visible: \"" + textoError + "\"",
                textoError.contains("Este correo electrónico ya está en uso. ¿Deseas iniciar sesión?")
        );
    }

    @And("el usuario no es redirigido a otra página")
    public void elUsuarioNoEsRedirigidoAOtraPagina() {
        Assert.assertFalse(
                "La URL no debería contener '/login', pero contiene: " + driver.getCurrentUrl(),
                driver.getCurrentUrl().contains("/login")
        );
    }
}
