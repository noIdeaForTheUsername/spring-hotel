package com.serwerigora.hotel.clients;

import com.serwerigora.hotel.clients.dto.UpdateClientRequest;
import com.serwerigora.hotel.clients.model.Client;
import com.serwerigora.hotel.clients.model.ClientsRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clients")
public class ClientsController {

    private ClientsRepository repository;

    ClientsController(ClientsRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public Client getClient(@PathVariable int id) {
       var client = repository.findById(id);
       if (client.isEmpty())
           throw new ResponseStatusException(HttpStatus.NOT_FOUND);
       return client.get();
    }

    @GetMapping
    public List<Client> getClients(Pageable pageable) {
        return repository.findAll(pageable).getContent();
    }

    @PostMapping
    public ResponseEntity<Void> createClient(@Valid @RequestBody Client requested) {
        var toCreate = new Client(null, requested.firstName(), requested.lastName(), requested.phoneNumber());
        var created = repository.save(toCreate);
        return ResponseEntity.created(URI.create("/clients/" + created.id())).build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateClient(@PathVariable int id, @Valid @RequestBody UpdateClientRequest requested) {
        Optional<Client> existing = repository.findById(id);
        if (existing.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        var toSave = new Client(id, requested.firstName(), requested.lastName(), requested.phoneNumber());
        repository.save(toSave);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable int id) {
        if (!repository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        repository.deleteById(id);
    }
}
