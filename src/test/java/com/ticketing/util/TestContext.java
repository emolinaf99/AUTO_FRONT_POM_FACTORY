package com.ticketing.util;

public class TestContext {

    private static final ThreadLocal<String> emailDuplicadoHolder = new ThreadLocal<>();

    public static void setEmailDuplicado(String email) {
        emailDuplicadoHolder.set(email);
    }

    public static String getEmailDuplicado() {
        return emailDuplicadoHolder.get();
    }

    public static void limpiar() {
        emailDuplicadoHolder.remove();
    }
}
