package com.eatclub.restaurant.deal.external;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiClientTest {

    @Autowired
    private ApiClient apiClient;

    @Test
    void testFetchRestaurants() {
        apiClient.getRestaurants()
                .doOnNext(r -> System.out.println("Restaurants: " + r.getRestaurants().size()))
                .block();
    }

}