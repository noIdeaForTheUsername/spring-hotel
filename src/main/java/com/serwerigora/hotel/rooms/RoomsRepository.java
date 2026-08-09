package com.serwerigora.hotel.rooms;

import com.serwerigora.hotel.rooms.model.Room;
import org.springframework.data.repository.ListCrudRepository;

public interface RoomsRepository extends ListCrudRepository<Room, Integer> {


}