package com.eatclub.restaurant.deal.external;

import java.util.List;

public class RestaurantApiResponse {

    private List<RestaurantDto> restaurants;

    public List<RestaurantDto> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(List<RestaurantDto> restaurants) {
        this.restaurants = restaurants;
    }
}
