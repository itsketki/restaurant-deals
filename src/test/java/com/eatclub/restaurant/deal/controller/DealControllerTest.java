package com.eatclub.restaurant.deal.controller;

import com.eatclub.restaurant.deal.service.DealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.reactive.server.WebTestClient;

public class DealControllerTest {

    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        DealService dealService = Mockito.mock(DealService.class);
        DealController controller = new DealController(dealService);

        webTestClient = WebTestClient.bindToController(controller)
                .build();
    }

    @Test
    void test_NoDealsReturned() {
        webTestClient.get()
                .uri("/active")
                .exchange()
                .expectStatus().isNotFound();
    }

}
