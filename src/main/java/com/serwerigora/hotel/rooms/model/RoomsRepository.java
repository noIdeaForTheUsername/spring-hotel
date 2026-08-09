package com.serwerigora.hotel.rooms.model;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

public interface RoomsRepository extends ListCrudRepository<Room, Integer> {
    @Modifying
    @Query("INSERT INTO rooms (number, max_persons, class) VALUES(:number, :maxPersons, :roomClass);")
    void insert(
            @Param("number") Integer number,
            @Param("maxPersons") Integer maxPersons,
            @Param("roomClass") RoomClass roomClass);

    @Modifying
    @Query("UPDATE rooms SET max_persons = :maxPersons, class = :roomClass WHERE number = :number;")
    int update(
            @Param("number") Integer number,
            @Param("maxPersons") Integer maxPersons,
            @Param("roomClass") RoomClass roomClass);
}