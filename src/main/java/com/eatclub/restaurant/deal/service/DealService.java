package com.eatclub.restaurant.deal.service;

import com.eatclub.restaurant.deal.external.ApiClient;
import com.eatclub.restaurant.deal.external.DealDto;
import com.eatclub.restaurant.deal.external.RestaurantDto;
import com.eatclub.restaurant.deal.response.ActiveDealResponse;
import com.eatclub.restaurant.deal.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
public class DealService {

    private static final Logger logger = LoggerFactory.getLogger(DealService.class);

    private final ApiClient apiClient;

    public DealService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }


    public Mono<List<ActiveDealResponse>> getActiveDeals(LocalTime requestTime) {
        return apiClient.getRestaurants()
                // Flatten restaurants; handle null
                .flatMapMany(response -> Flux.fromIterable(
                        Optional.ofNullable(response.getRestaurants())
                                .orElse(Collections.emptyList())))
                // Flatten deals; filter active
                .flatMap(restaurant -> Flux.fromIterable(
                                Optional.ofNullable(restaurant.getDeals())
                                        .orElse(Collections.emptyList()))
                        .filter(deal -> isDealActive(deal, restaurant, requestTime))
                        .map(deal -> mapToActiveDealResponse(restaurant, deal))
                )
                // Log each active deal
                .doOnNext(deal -> logger.info("Active deal: {} at restaurant: {}",
                        deal.getDealObjectId(), deal.getRestaurantName()))
                // Collect into list
                .collectList()
                // Log total active deals
                .doOnNext(list -> logger.info("Total active deals found: {}", list.size()));
    }


    private boolean isDealActive(DealDto deal, RestaurantDto restaurant, LocalTime time) {
        String startTime = getDealOpenTime(deal, restaurant);
        String endTime = getDealCloseTime(deal, restaurant);

        if (startTime == null || endTime == null) {
            return false;
        }

        try {

            LocalTime dealOpen = TimeUtil.parseTime(startTime);
            LocalTime dealClose = TimeUtil.parseTime(endTime);

            logger.info("Deal open:{} close: {}", dealOpen, dealClose);

            //Active if time is within deal timings
            return (!time.isAfter(dealClose) && !time.isBefore(dealOpen));

        } catch (IllegalArgumentException e) {
            logger.warn("Skipping deal {} due to invalid time format: {}", deal.getObjectId(), e.getMessage());
            return false;
        }

    }

    //For Deal timing, use Deal open/close or start/end otherwise assume restaurant timing
    private String getDealOpenTime(DealDto deal, RestaurantDto restaurant) {
        return Optional.ofNullable(deal.getOpen())
                .or(() -> Optional.ofNullable(deal.getStart()))
                .orElse(restaurant.getOpen());
    }

    private String getDealCloseTime(DealDto deal, RestaurantDto restaurant) {
        return Optional.ofNullable(deal.getClose())
                .or(() -> Optional.ofNullable(deal.getEnd()))
                .orElse(restaurant.getClose());
    }

    private ActiveDealResponse mapToActiveDealResponse(RestaurantDto restaurant, DealDto deal) {

        logger.info("mapToActiveDealResponse {} {}", restaurant.getName(), deal.getObjectId());
        return new ActiveDealResponse(
                restaurant.getObjectId(),
                restaurant.getName(),
                restaurant.getAddress1(),
                restaurant.getSuburb(),
                restaurant.getOpen(),
                restaurant.getClose(),
                deal.getObjectId(),
                deal.getDiscount(),
                deal.getDineIn(),
                deal.getLightning(),
                deal.getQtyLeft()
        );
    }

}
