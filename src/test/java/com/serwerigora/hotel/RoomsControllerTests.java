package com.serwerigora.hotel;

import com.serwerigora.hotel.rooms.dto.UpdateRoomRequest;
import com.serwerigora.hotel.rooms.model.Room;
import com.serwerigora.hotel.rooms.model.RoomClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(
        scripts = "/sql/RoomsControllerTests.sql",
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD
)
class RoomsControllerTests {

    @LocalServerPort
    int port;

    RestTestClient client;
    private final UpdateRoomRequest PUT_BODY = new UpdateRoomRequest(55, RoomClass.PREMIUM);

    @BeforeEach
    void setup() {
        client = RestTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    @Test
    void shouldGetRoom() {
        expectSuccessfulGetRoom(101);
    }

    @Test
    void shouldGetArrayOfRooms() {
        var rooms = client.get()
                .uri("/rooms")
                .exchangeSuccessfully()
                .expectBody(Room[].class)
                .returnResult()
                .getResponseBody();
        assertThat(rooms).isNotEmpty();
    }

    @Test
    void shouldCreateRoom() {
        client.post()
                .uri("/rooms")
                .body(new Room(300, 5, RoomClass.STANDARD))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED);
        expectSuccessfulGetRoom(300);
    }

    @Test
    void shouldNotUpdateRoomOnPost() {
        client.post()
                .uri("/rooms")
                .body(new Room(101, 999, RoomClass.STANDARD))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);

        var room = client.get()
                .uri("/rooms/101")
                .exchange()
                .expectBody(Room.class)
                .returnResult()
                .getResponseBody();
        assertThat(room.maxPersons()).isNotEqualTo(999);
    }

    @Test
    void shouldUpdateRoom() {
        client.put()
                .uri("/rooms/201")
                .body(PUT_BODY)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NO_CONTENT);
        Room updatedRoom = expectSuccessfulGetRoom(201);
        assertThat(updatedRoom).isEqualTo(new Room(201, PUT_BODY.maxPersons(), PUT_BODY.roomClass()));
    }

    @Test
    void shouldNotCreateRoomOnPut() {
        client.put()
                .uri("/rooms/9876")
                .body(PUT_BODY)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
        client.get()
                .uri("/rooms/9876")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldDeleteRoom() {
        client.delete()
                .uri("/rooms/202")
                .exchangeSuccessfully();
        client.get()
                .uri("/rooms/202")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void endpointsShouldReturnNotFoundOnNonExistingRoom() {
        String uri = "/rooms/5678";
        var requests = new RestTestClient.RequestHeadersSpec[] {
                client.get().uri(uri),
                client.delete().uri(uri),
                client.put().uri(uri).body(PUT_BODY),
        };
        for (var request : requests) {
            request.exchange()
                    .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    private Room expectSuccessfulGetRoom(Integer number) {
        var result = client.get()
                .uri("/rooms/" + number)
                .exchangeSuccessfully()
                .expectBody(Room.class)
                .returnResult();
        var room = result.getResponseBody();
        assertThat(room).isNotNull();
        assertThat(room.number()).isEqualTo(number);
        return room;
    }
}
