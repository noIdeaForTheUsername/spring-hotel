package com.serwerigora.hotel;

import com.serwerigora.hotel.reservations.model.Reservation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "/testData.sql")
public class ReservationsControllerTests extends RestE2ETest {

    private static final Reservation PUT_BODY =
            new Reservation(
                    null,
                    2,
                    101,
                    LocalDate.of(2026, 9, 10),
                    LocalDate.of(2026, 9, 15)
            );

    @Test
    void shouldGetReservation() {
        var reservation = getValidReservation(1);

        assertThat(reservation.clientId()).isEqualTo(1);
        assertThat(reservation.roomNumber()).isEqualTo(101);
        assertThat(reservation.firstDay()).isEqualTo(LocalDate.of(2026, 8, 10));
        assertThat(reservation.lastDay()).isEqualTo(LocalDate.of(2026, 8, 15));
    }

    @Test
    void shouldGetArrayOfReservations() {
        rest.get().uri("/reservations")
                .exchangeSuccessfully()
                .expectBody(Reservation[].class);
    }

    @Test
    void shouldCreateReservation() {
        var location = rest.post().uri("/reservations")
                .body(new Reservation(
                        null,
                        2,
                        102,
                        LocalDate.of(2026, 9, 1),
                        LocalDate.of(2026, 9, 5)
                ))
                .exchangeSuccessfully()
                .expectStatus().isEqualTo(HttpStatus.CREATED)
                .returnResult()
                .getResponseHeaders()
                .getLocation();

        assertThat(location).isNotNull();

        var created = rest.get()
                .uri(location)
                .exchangeSuccessfully()
                .expectBody(Reservation.class)
                .returnResult()
                .getResponseBody();

        assertThat(created).isNotNull();
        assertThat(created.clientId()).isEqualTo(2);
        assertThat(created.roomNumber()).isEqualTo(102);
        assertThat(created.firstDay()).isEqualTo(LocalDate.of(2026, 9, 1));
        assertThat(created.lastDay()).isEqualTo(LocalDate.of(2026, 9, 5));
    }

    @Test
    void shouldUpdateExistingReservation() {
        rest.put().uri("/reservations/2")
                .body(PUT_BODY)
                .exchangeSuccessfully()
                .expectStatus().isEqualTo(HttpStatus.NO_CONTENT);

        var updated = getValidReservation(2);

        assertThat(updated.clientId()).isEqualTo(PUT_BODY.clientId());
        assertThat(updated.roomNumber()).isEqualTo(PUT_BODY.roomNumber());
        assertThat(updated.firstDay()).isEqualTo(PUT_BODY.firstDay());
        assertThat(updated.lastDay()).isEqualTo(PUT_BODY.lastDay());
    }

    @Test
    void putShouldNotCreateReservation() {
        rest.put().uri("/reservations/2222")
                .body(PUT_BODY)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);

        rest.get().uri("/reservations/2222")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getShouldReturn404ForNonExistentReservation() {
        rest.get().uri("/reservations/123456")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldRejectReservationForNonExistentClient() {
        rest.post().uri("/reservations")
                .body(new Reservation(
                        null,
                        123456,
                        102,
                        LocalDate.of(2026, 9, 1),
                        LocalDate.of(2026, 9, 5)
                ))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldRejectReservationForNonExistentRoom() {
        rest.post().uri("/reservations")
                .body(new Reservation(
                        null,
                        1,
                        999,
                        LocalDate.of(2026, 9, 1),
                        LocalDate.of(2026, 9, 5)
                ))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldRejectOverlappingReservation() {
        rest.post().uri("/reservations")
                .body(new Reservation(
                        null,
                        3,
                        101,
                        LocalDate.of(2026, 8, 12),
                        LocalDate.of(2026, 8, 20)
                ))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void shouldAllowReservationStartingOnPreviousReservationLastDay() {
        rest.post().uri("/reservations")
                .body(new Reservation(
                        null,
                        3,
                        101,
                        LocalDate.of(2026, 8, 15),
                        LocalDate.of(2026, 8, 20)
                ))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void shouldRejectReservationWhenLastDayIsNotAfterFirstDay() {
        rest.post().uri("/reservations")
                .body(new Reservation(
                        null,
                        3,
                        102,
                        LocalDate.of(2026, 9, 10),
                        LocalDate.of(2026, 9, 10)
                ))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldRejectReservationWhenLastDayIsBeforeFirstDay() {
        rest.post().uri("/reservations")
                .body(new Reservation(
                        null,
                        3,
                        102,
                        LocalDate.of(2026, 9, 15),
                        LocalDate.of(2026, 9, 10)
                ))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldAllowUpdatingReservationWithoutConflictingWithItself() {
        rest.put().uri("/reservations/1")
                .body(new Reservation(
                        null,
                        1,
                        101,
                        LocalDate.of(2026, 8, 10),
                        LocalDate.of(2026, 8, 15)
                ))
                .exchangeSuccessfully()
                .expectStatus().isEqualTo(HttpStatus.NO_CONTENT);

        getValidReservation(1);
    }

    @ParameterizedTest
    @MethodSource("overlappingDateRanges")
    void shouldRejectUpdateWhenNewDatesOverlapAnotherReservation(LocalDate firstDay, LocalDate lastDay) {
        rest.put().uri("/reservations/1")
            .body(new Reservation(
                null,
                1,
                101,
                firstDay,
                lastDay
            ))
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }
    private static Stream<Arguments> overlappingDateRanges() {
        return Stream.of(
                Arguments.of(
                        LocalDate.of(2026, 8, 13),
                        LocalDate.of(2026, 8, 21)
                ),
                Arguments.of(
                        LocalDate.of(2026, 8, 24),
                        LocalDate.of(2026, 8, 25)
                ),
                Arguments.of(
                        LocalDate.of(2026, 8, 22),
                        LocalDate.of(2026, 8, 24)
                )
        );
    }

    private Reservation getValidReservation(int id) {
        var reservation = rest.get()
                .uri("/reservations/" + id)
                .exchangeSuccessfully()
                .expectBody(Reservation.class)
                .returnResult()
                .getResponseBody();

        assertThat(reservation).isNotNull();
        assertThat(reservation.id()).isEqualTo(id);

        return reservation;
    }

}

