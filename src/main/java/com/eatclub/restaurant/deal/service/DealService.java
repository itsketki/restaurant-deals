package com.eatclub.restaurant.deal.service;

import com.eatclub.restaurant.deal.external.ApiClient;
import com.eatclub.restaurant.deal.external.DealDto;
import com.eatclub.restaurant.deal.external.RestaurantDto;
import com.eatclub.restaurant.deal.response.ActiveDealResponse;
import com.eatclub.restaurant.deal.response.PeakTimeResponse;
import com.eatclub.restaurant.deal.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalTime;
import java.util.*;


@Service
public class DealService {

    private static final Logger logger = LoggerFactory.getLogger(DealService.class);

    private final ApiClient apiClient;

    public DealService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public Mono<List<ActiveDealResponse>> getActiveDeals(LocalTime requestTime) {
        return apiClient.getRestaurants()
                .flatMapMany(response -> Flux.fromIterable(
                        Optional.ofNullable(response.getRestaurants())
                                .orElse(Collections.emptyList())))
                .flatMap(restaurant -> Flux.fromIterable(
                                Optional.ofNullable(restaurant.getDeals())
                                        .orElse(Collections.emptyList()))
                        .filter(deal -> isDealActive(deal, restaurant, requestTime))
                        .map(deal -> mapToActiveDealResponse(restaurant, deal))
                )
                .doOnNext(deal -> logger.info("Active deal: {} at restaurant: {}",
                        deal.getDealObjectId(), deal.getRestaurantName()))
                .collectList()
                .doOnNext(list -> logger.info("Total active deals found: {}", list.size()));
    }


    private boolean isDealActive(DealDto deal, RestaurantDto restaurant, LocalTime time) {
        String startTime = getDealOpenTime(deal, restaurant);
        String endTime = getDealCloseTime(deal, restaurant);

        //Skip deal
        if (startTime == null || endTime == null) {
            return false;
        }
        try {
            LocalTime dealOpen = TimeUtil.parseTime(startTime);
            LocalTime dealClose = TimeUtil.parseTime(endTime);

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

    public Mono<PeakTimeResponse> calculatePeakTime() {
        return apiClient.getRestaurants()
                .flatMapMany(resp -> Flux.fromIterable(
                        Optional.ofNullable(resp.getRestaurants()).orElse(Collections.emptyList())))
                .flatMap(restaurant -> Flux.fromIterable(
                                Optional.ofNullable(restaurant.getDeals()).orElse(Collections.emptyList()))
                        .map(deal -> new String[]{getDealOpenTime(deal, restaurant),
                                getDealCloseTime(deal, restaurant)}))
                .map(times -> {
                    try {
                        LocalTime start = TimeUtil.parseTime(times[0]);
                        LocalTime end = TimeUtil.parseTime(times[1]);
                        return new LocalTime[]{start, end};
                    } catch (Exception e) {
                        return null; // skip invalid times
                    }
                })
                .filter(obj -> true)
                .collectList()
                // Use 1-hour sliding window
                .map(list -> findPeakTimeWindowSliding(list, 60))
                .doOnNext(peak -> logger.info("Sliding window peak: {} - {}", peak.getPeakTimeStart(), peak.getPeakTimeEnd()));
    }

    private PeakTimeResponse findPeakTimeWindowSliding(List<LocalTime[]> dealTimes, int windowMinutes) {
        // Convert all deal times into minutes of day
        List<int[]> minuteRanges = dealTimes.stream()
                .map(times -> new int[]{
                        times[0].getHour() * 60 + times[0].getMinute(),
                        times[1].getHour() * 60 + times[1].getMinute()
                })
                .toList();

        int maxDeals = 0;
        int peakStartMinute = 0;

        // Slide window over the day, by 5 min
        for (int start = 0; start <= 1440 - windowMinutes; start += 5) {
            int end = start + windowMinutes;
            int count = 0;

            for (int[] range : minuteRanges) {
                if (range[0] < end && range[1] > start) {
                    count++;
                }
            }

            if (count > maxDeals) {
                maxDeals = count;
                peakStartMinute = start;
            }
        }

        LocalTime peakStart = LocalTime.of(peakStartMinute / 60, peakStartMinute % 60);
        LocalTime peakEnd = peakStart.plusMinutes(windowMinutes);

        logger.info("Peak window: {} - {} with {} active deals", peakStart, peakEnd, maxDeals);
        return new PeakTimeResponse(peakStart.toString(), peakEnd.toString());
    }
}
