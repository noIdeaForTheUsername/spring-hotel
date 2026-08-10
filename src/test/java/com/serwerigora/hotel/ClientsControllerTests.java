package com.serwerigora.hotel;

import com.serwerigora.hotel.clients.dto.UpdateClientRequest;
import com.serwerigora.hotel.clients.model.Client;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.*;

@Sql(
        scripts = "/testData.sql"
)
public class ClientsControllerTests extends RestE2ETest {

    private static final UpdateClientRequest PUT_BODY =
            new UpdateClientRequest("Client", "New", "+48555999888");

    @Test
    void shouldGetClient() {
        getValidClient(1);
    }

    @Test
    void shouldGetArrayOfClients() {
        rest.get().uri("/clients")
                .exchangeSuccessfully()
                .expectBody(Client[].class);
    }

    @Test
    void shouldCreateClient() {
        var location = rest.post().uri("/clients")
                .body(new Client(null, "Nice", "Name", "+48765765765"))
                .exchangeSuccessfully()
                .expectStatus().isEqualTo(HttpStatus.CREATED)
                .returnResult()
                .getResponseHeaders()
                .getLocation();

        assertThat(location).isNotNull();
        var created = rest.get()
                .uri(location)
                .exchangeSuccessfully()
                .expectBody(Client.class)
                .returnResult()
                .getResponseBody();
        assertThat(created.id()).isNotNull();
        assertThat(created.firstName()).isEqualTo("Nice");
        assertThat(created.lastName()).isEqualTo("Name");
        assertThat(created.phoneNumber()).isEqualTo("+48765765765");
    }

    @Test
    void shouldUpdateExistingClient() {
        rest.put().uri("/clients/2")
                .body(PUT_BODY)
                .exchangeSuccessfully()
                .expectStatus().isEqualTo(HttpStatus.NO_CONTENT);

        var updated = getValidClient(2);
        assertThat(updated.firstName()).isEqualTo(PUT_BODY.firstName());
        assertThat(updated.lastName()).isEqualTo(PUT_BODY.lastName());
        assertThat(updated.phoneNumber()).isEqualTo(PUT_BODY.phoneNumber());
    }

    @Test
    void putShouldNotCreateClient() {
        rest.put().uri("clients/2222")
                .body(PUT_BODY)
                .exchange();
        rest.get().uri("clients/2222")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldDeleteExistingClient() {
        rest.delete().uri("/clients/3")
                .exchangeSuccessfully();

        rest.get().uri("/clients/3")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void endpointsShouldProperlyReturn404() {
        test404OnNonExistentClient(rest.get());
        test404OnNonExistentClient(rest.delete());
       rest.put().uri("/clients/123456")
                .body(PUT_BODY)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    private Client getValidClient(int id) {
        var client = rest.get().uri("/clients/" + id)
                .exchangeSuccessfully()
                .expectBody(Client.class)
                .returnResult()
                .getResponseBody();
        assertThat(client).isNotNull();
        assertThat(client.id()).isEqualTo(id);
        return client;
    }

    private void test404OnNonExistentClient(RestTestClient.RequestHeadersUriSpec request) {
        request.uri("/clients/123456")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }
}
