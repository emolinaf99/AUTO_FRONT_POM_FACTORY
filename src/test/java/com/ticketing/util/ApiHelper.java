package com.ticketing.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiHelper {

    private static final String AUTH_SERVICE_URL = "http://localhost:8003";

    public static void registrarUsuario(String email, String password) {
        String jsonBody = String.format(
                "{\"firstName\":\"Test\",\"lastName\":\"User\",\"email\":\"%s\",\"password\":\"%s\",\"confirmPassword\":\"%s\",\"roles\":[]}",
                email, password, password
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AUTH_SERVICE_URL + "/api/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException(
                        "Fallo al crear usuario de prueba en el AuthService. HTTP " + response.statusCode()
                                + ". Body: " + response.body()
                );
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con el AuthService en " + AUTH_SERVICE_URL, e);
        }
    }
}
