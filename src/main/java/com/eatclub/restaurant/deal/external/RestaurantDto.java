package com.eatclub.restaurant.deal.external;

import java.util.List;

public class RestaurantDto {

    private String objectId;
    private String name;
    private String address1;
    private String suburb;
    private List<String> cuisines;
    private String imageLink;
    private String open;
    private String close;

    private List<DealDto> deals;


    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public List<String> getCuisines() {
        return cuisines;
    }

    public void setCuisines(List<String> cuisines) {
        this.cuisines = cuisines;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public List<DealDto> getDeals() {
        return deals;
    }

    public void setDeals(List<DealDto> deals) {
        this.deals = deals;
    }


}
