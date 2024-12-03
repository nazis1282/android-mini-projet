package com.example.mini_projet;

public class Restaurant {
    private String id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private double rating;

    // Default Constructor (required for Firebase deserialization)
    public Restaurant() {
        // Firebase needs an empty constructor to deserialize
    }

    // Parameterized Constructor
    public Restaurant(String id , String name, String address, double latitude, double longitude, double rating) {
        this.id=id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
    }
    public  Restaurant(String name, String address,double rating){
        this.name=name;
        this.address=address;
        this.rating=rating;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    // Getters and Setters
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
