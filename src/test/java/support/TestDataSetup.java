package support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.thucydides.model.util.EnvironmentVariables;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class TestDataSetup {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final EnvironmentVariables environmentVariables;
    private final HttpClient httpClient;

    public TestDataSetup() {
        this.environmentVariables = ConfiguredEnvironment.getEnvironmentVariables();
        this.httpClient = HttpClient.newHttpClient();
    }

    public PreparedEvent prepareFutureEventWithAvailableTickets() {
        String crudUrl = requiredProperty("api.crud.url");
        int ticketQuantity = Integer.parseInt(propertyOrDefault("testdata.tickets.quantity", "3"));

        try {
            CreatedEvent event = createEvent(crudUrl);
            createTickets(crudUrl, event.id(), ticketQuantity);
            waitUntilEventIsAvailable(crudUrl, event.id(), ticketQuantity);
            return new PreparedEvent(event.id(), event.name());
        } catch (IOException | InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("No fue posible preparar datos de prueba para el escenario", exception);
        }
    }

    private CreatedEvent createEvent(String crudUrl) throws IOException, InterruptedException {
        String eventName = "Evento Automatizado " + UUID.randomUUID();
        String startsAt = OffsetDateTime.now(ZoneOffset.UTC)
                .plusDays(7)
                .withNano(0)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        String payload = OBJECT_MAPPER.writeValueAsString(new CreateEventRequest(eventName, startsAt));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(crudUrl + "/api/events"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        validateResponse(response, 200, 201);

        JsonNode json = OBJECT_MAPPER.readTree(response.body());
        return new CreatedEvent(json.get("id").asInt(), json.get("name").asText());
    }

    private void createTickets(String crudUrl, int eventId, int quantity) throws IOException, InterruptedException {
        String payload = OBJECT_MAPPER.writeValueAsString(new CreateTicketsRequest(eventId, quantity));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(crudUrl + "/api/tickets/bulk"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        validateResponse(response, 200, 201);
    }

    private void waitUntilEventIsAvailable(String crudUrl, int eventId, int expectedTickets) throws IOException, InterruptedException {
        for (int attempt = 0; attempt < 10; attempt++) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(crudUrl + "/api/events/" + eventId))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            validateResponse(response, 200);

            JsonNode json = OBJECT_MAPPER.readTree(response.body());
            int availableTickets = json.path("availableTickets").asInt();
            if (availableTickets >= expectedTickets) {
                return;
            }

            Thread.sleep(1000);
        }

        throw new IllegalStateException("El evento preparado no publicó tickets disponibles a tiempo");
    }

    private void validateResponse(HttpResponse<String> response, int... expectedStatuses) {
        for (int expectedStatus : expectedStatuses) {
            if (response.statusCode() == expectedStatus) {
                return;
            }
        }
        throw new IllegalStateException("Respuesta inesperada del backend: " + response.statusCode() + " -> " + response.body());
    }

    private String requiredProperty(String key) {
        String value = EnvironmentSpecificConfiguration.from(environmentVariables).getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("No se encontró la propiedad requerida: " + key);
        }
        return value;
    }

    private String propertyOrDefault(String key, String defaultValue) {
        String value = EnvironmentSpecificConfiguration.from(environmentVariables).getProperty(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    public record PreparedEvent(int id, String name) {
    }

    private record CreatedEvent(int id, String name) {
    }

    private record CreateEventRequest(String name, String startsAt) {
    }

    private record CreateTicketsRequest(int eventId, int quantity) {
    }
}
