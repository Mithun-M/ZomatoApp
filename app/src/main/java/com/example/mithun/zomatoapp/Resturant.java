package com.example.mithun.zomatoapp;

/**
 * Created by Mithun on 18-04-2018.
 */

public class Resturant {

    private String name;
    private String address;
    private String cuisine;
    private String imageurl;
    private float rating;
    private double lat;
    private double lon;

    public Resturant(String name, String address, String cuisine, String imageurl, float rating, double lat, double lon) {
        this.name = name;
        this.address = address;
        this.cuisine = cuisine;
        this.imageurl = imageurl;
        this.rating = rating;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}