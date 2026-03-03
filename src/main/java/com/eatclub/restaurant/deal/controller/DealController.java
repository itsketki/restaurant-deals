package com.eatclub.restaurant.deal.controller;

import com.eatclub.restaurant.deal.response.ActiveDealResponse;
import com.eatclub.restaurant.deal.service.DealService;
import com.eatclub.restaurant.deal.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/restaurant/deals")
public class DealController {
    private static final Logger logger = LoggerFactory.getLogger(DealController.class);

    private final DealService dealService;

    public DealController(DealService dealService) {
        this.dealService = dealService;
    }

    @GetMapping("/active")
    public Mono<List<ActiveDealResponse>> getActiveDeals(@RequestParam("timeOfDay") String time) {
        LocalTime requestTime = TimeUtil.parseTime(time);

        return dealService.getActiveDeals(requestTime);

    }



}
