package com.ticketing.hooks;

import com.ticketing.util.ApiHelper;
import com.ticketing.util.TestContext;
import io.cucumber.java.Before;

import java.util.UUID;

public class AutenticacionHooks {

    @Before("@CP-HU1-08")
    public void crearUsuarioDePrueba() {
        String emailUnico = "test." + UUID.randomUUID().toString().substring(0, 8) + "@testmail.com";
        ApiHelper.registrarUsuario(emailUnico, "Test1234!");
        TestContext.setEmailDuplicado(emailUnico);
    }
}

