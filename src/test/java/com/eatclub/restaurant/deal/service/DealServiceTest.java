package com.eatclub.restaurant.deal.service;

import com.eatclub.restaurant.deal.external.ApiClient;
import com.eatclub.restaurant.deal.external.DealDto;
import com.eatclub.restaurant.deal.external.RestaurantApiResponse;
import com.eatclub.restaurant.deal.external.RestaurantDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DealServiceTest {

    @Mock
    private ApiClient apiClient;

    @InjectMocks
    private DealService dealService;

    private RestaurantDto restaurant;
    private DealDto deal1;
    private DealDto deal2;
    private RestaurantApiResponse apiResponse;

    @BeforeEach
    void setup() {
        deal1 = new DealDto();
        deal1.setObjectId("D1");
        deal1.setOpen("10:00am");
        deal1.setClose("11:00am");
        deal1.setDiscount("50");
        deal1.setDineIn("true");
        deal1.setLightning("false");
        deal1.setQtyLeft("5");

        deal2 = new DealDto();
        deal2.setObjectId("D2");
        deal2.setOpen("10:30am");
        deal2.setClose("11:30am");
        deal2.setDiscount("40");
        deal2.setDineIn("false");
        deal2.setLightning("true");
        deal2.setQtyLeft("2");

        restaurant = new RestaurantDto();
        restaurant.setObjectId("R1");
        restaurant.setName("Test Restaurant");
        restaurant.setAddress1("555 Street");
        restaurant.setSuburb("Suburb");
        restaurant.setOpen("10:00am");
        restaurant.setClose("12:00pm");
        restaurant.setDeals(Arrays.asList(deal1, deal2));

        apiResponse = new RestaurantApiResponse();
        apiResponse.setRestaurants(Collections.singletonList(restaurant));
    }

    @Test
    void testGetActiveDeals_returnActiveDeals() {
        when(apiClient.getRestaurants()).thenReturn(Mono.just(apiResponse));

        LocalTime requestTime = LocalTime.of(10, 30); // 10:30am

        StepVerifier.create(dealService.getActiveDeals(requestTime))
                .expectNextMatches(activeDeals ->
                        activeDeals.size() == 2 &&
                                activeDeals.stream().anyMatch(d -> d.getDealObjectId().equals("D1")) &&
                                activeDeals.stream().anyMatch(d -> d.getDealObjectId().equals("D2"))
                )
                .verifyComplete();
    }

    @Test
    void testGetActiveDeals_noActiveDeals() {
        when(apiClient.getRestaurants()).thenReturn(Mono.just(apiResponse));

        LocalTime requestTime = LocalTime.of(12, 30); // after all deals

        StepVerifier.create(dealService.getActiveDeals(requestTime))
                .expectNextMatches(List::isEmpty)
                .verifyComplete();
    }


    @Test
    void testCalculatePeakTime_returnsPeakWindow() {
        when(apiClient.getRestaurants()).thenReturn(Mono.just(apiResponse));

        StepVerifier.create(dealService.calculatePeakTime())
                .expectNextMatches(peak ->
                        peak.getPeakTimeStart() != null && peak.getPeakTimeEnd() != null
                )
                .verifyComplete();
    }

}