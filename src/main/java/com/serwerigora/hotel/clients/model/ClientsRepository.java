package com.serwerigora.hotel.clients.model;


import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ClientsRepository extends ListCrudRepository<Client, Integer>,
        PagingAndSortingRepository<Client, Integer> {

}
