package com.serwerigora.hotel;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class RestE2ETest {
    @LocalServerPort
    int port;

    RestTestClient rest;

    @BeforeEach
    void setup() {
        rest = RestTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }
}
