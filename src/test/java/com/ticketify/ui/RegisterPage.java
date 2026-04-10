package com.ticketify.ui;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RegisterPage {

    private final WebDriver driver;
    private static final int TIMEOUT_SEGUNDOS = 10;

    @FindBy(id = "reg-firstName")
    private WebElement campoNombre;

    @FindBy(id = "reg-lastName")
    private WebElement campoApellido;

    @FindBy(id = "reg-email")
    private WebElement campoEmail;

    @FindBy(id = "reg-password")
    private WebElement campoPassword;

    @FindBy(id = "reg-confirm")
    private WebElement campoConfirmacion;

    @FindBy(css = "button[type='submit']")
    private WebElement botonEnviar;

    @FindBy(css = "div.text-green-400")
    private WebElement mensajeConfirmacion;

    @FindBy(css = "#reg-email + p")
    private WebElement errorEmail;

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void completarNombre(String nombre) {
        campoNombre.clear();
        campoNombre.sendKeys(nombre);
    }

    public void completarApellido(String apellido) {
        campoApellido.clear();
        campoApellido.sendKeys(apellido);
    }

    public void completarEmail(String email) {
        campoEmail.clear();
        campoEmail.sendKeys(email);
    }

    public void completarPassword(String password) {
        campoPassword.clear();
        campoPassword.sendKeys(password);
    }

    public void completarConfirmacion(String confirmacion) {
        campoConfirmacion.clear();
        campoConfirmacion.sendKeys(confirmacion);
    }

    public void enviarFormulario() {
        botonEnviar.click();
    }

    public WebElement obtenerMensajeConfirmacion() {
        WebDriverWait espera = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SEGUNDOS));
        return espera.until(ExpectedConditions.visibilityOf(mensajeConfirmacion));
    }

    public WebElement obtenerErrorEmail() {
        WebDriverWait espera = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SEGUNDOS));
        return espera.until(ExpectedConditions.visibilityOf(errorEmail));
    }

    public void esperarRedireccionA(String urlFragment) {
        WebDriverWait espera = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SEGUNDOS));
        espera.until(ExpectedConditions.urlContains(urlFragment));
    }
}
