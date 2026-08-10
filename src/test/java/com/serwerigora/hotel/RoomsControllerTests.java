package com.serwerigora.hotel;

import com.serwerigora.hotel.rooms.dto.UpdateRoomRequest;
import com.serwerigora.hotel.rooms.model.Room;
import com.serwerigora.hotel.rooms.model.RoomClass;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import static org.assertj.core.api.Assertions.assertThat;


@Sql(
        scripts = "/testData.sql",
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD
)
class RoomsControllerTests extends RestE2ETest {

    private final UpdateRoomRequest PUT_BODY = new UpdateRoomRequest(55, RoomClass.PREMIUM);

    @Test
    void shouldGetRoom() {
        getValidRoom(101);
    }

    @Test
    void shouldGetArrayOfRooms() {
        var rooms = rest.get()
                .uri("/rooms")
                .exchangeSuccessfully()
                .expectBody(Room[].class)
                .returnResult()
                .getResponseBody();
        assertThat(rooms).isNotEmpty();
    }

    @Test
    void shouldCreateRoom() {
        rest.post()
                .uri("/rooms")
                .body(new Room(300, 5, RoomClass.STANDARD))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED);
        getValidRoom(300);
    }

    @Test
    void shouldNotUpdateRoomOnPost() {
        rest.post()
                .uri("/rooms")
                .body(new Room(101, 999, RoomClass.STANDARD))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);

        var room = rest.get()
                .uri("/rooms/101")
                .exchange()
                .expectBody(Room.class)
                .returnResult()
                .getResponseBody();
        assertThat(room.maxPersons()).isNotEqualTo(999);
    }

    @Test
    void shouldUpdateRoom() {
        rest.put()
                .uri("/rooms/201")
                .body(PUT_BODY)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NO_CONTENT);
        Room updatedRoom = getValidRoom(201);
        assertThat(updatedRoom).isEqualTo(new Room(201, PUT_BODY.maxPersons(), PUT_BODY.roomClass()));
    }

    @Test
    void shouldNotCreateRoomOnPut() {
        rest.put()
                .uri("/rooms/9876")
                .body(PUT_BODY)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
        rest.get()
                .uri("/rooms/9876")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldDeleteRoom() {
        rest.delete()
                .uri("/rooms/202")
                .exchangeSuccessfully();
        rest.get()
                .uri("/rooms/202")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void endpointsShouldReturnNotFoundOnNonExistingRoom() {
        String uri = "/rooms/5678";
        var requests = new RestTestClient.RequestHeadersSpec[] {
                rest.get().uri(uri),
                rest.delete().uri(uri),
                rest.put().uri(uri).body(PUT_BODY),
        };
        for (var request : requests) {
            request.exchange()
                    .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    private Room getValidRoom(Integer number) {
        var result = rest.get()
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
