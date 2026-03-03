package com.eatclub.restaurant.deal.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Component
public class ApiClient {
    private final WebClient webClient;
    private final String url;

    public ApiClient(WebClient webClient, @Value("${external.api.path}") String url) {
        this.webClient = webClient;
        this.url = url;

    }

    public Mono<RestaurantApiResponse> getRestaurants() {

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(RestaurantApiResponse.class);

    }
}
