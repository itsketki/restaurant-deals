package com.eatclub.restaurant.deal.response;


public class ActiveDealResponse {

    private final String restaurantObjectId;
    private final String restaurantName;
    private final String restaurantAddress1;
    private final String restaurantSuburb;
    private final String restaurantOpen;
    private final String restaurantClose;
    private final String dealObjectId;
    private final String discount;
    private final String dineIn;
    private final String lightning;
    private final String qtyLeft;

    public ActiveDealResponse(String restaurantObjectId, String restaurantName, String restaurantAddress1, String restaurantSuburb, String restaurantOpen,
                       String restaurantClose, String dealObjectId, String discount, String dineIn, String lightning, String qtyLeft) {
        this.restaurantObjectId = restaurantObjectId;
        this.restaurantName = restaurantName;
        this.restaurantAddress1 = restaurantAddress1;
        this.restaurantSuburb = restaurantSuburb;
        this.restaurantOpen = restaurantOpen;
        this.restaurantClose = restaurantClose;
        this.dealObjectId = dealObjectId;
        this.discount = discount;
        this.dineIn = dineIn;
        this.lightning = lightning;
        this.qtyLeft = qtyLeft;
    }

    public String getRestaurantObjectId() {
        return restaurantObjectId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getRestaurantAddress1() {
        return restaurantAddress1;
    }

    public String getRestaurantSuburb() {
        return restaurantSuburb;
    }

    public String getRestaurantOpen() {
        return restaurantOpen;
    }

    public String getRestaurantClose() {
        return restaurantClose;
    }

    public String getDealObjectId() {
        return dealObjectId;
    }

    public String getDiscount() {
        return discount;
    }

    public String isDineIn() {
        return dineIn;
    }

    public String isLightning() {
        return lightning;
    }

    public String getQtyLeft() {
        return qtyLeft;
    }
}
