package com.alejandrocaralt.icarus.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Event implements Serializable {

    @Expose
    @SerializedName("id")
    private String id ;


    @Expose
    @SerializedName("routeName")
    private String routeName ;

    @Expose
    @SerializedName("title")
    private String title ;

    @Expose
    @SerializedName("km")
    private Double km ;

    @Expose
    @SerializedName("min")
    private Integer min ;

    @Expose
    @SerializedName("origin")
    private List<Double> origin ;

    @Expose
    @SerializedName("destination")
    private List<Double> destination ;

    @Expose
    @SerializedName("owner")
    private String owner ;

    @Expose
    @SerializedName("bikers")
    private List<String> bikers ;

    public Event() {

    }

    public Event(String id, String routeName, String title, Double km, Integer min, List<Double> origin, List<Double> destination, String owner, List<String> bikers) {
        this.id = id;
        this.routeName = routeName;
        this.title = title;
        this.km = km;
        this.min = min;
        this.origin = origin;
        this.destination = destination;
        this.owner = owner;
        this.bikers = bikers;
    }

    public Event(String routeName, String title, Double km, Integer min, List<Double> origin, List<Double> destination, String owner, List<String> bikers) {
        this.routeName = routeName;
        this.title = title;
        this.km = km;
        this.min = min;
        this.origin = origin;
        this.destination = destination;
        this.owner = owner;
        this.bikers = bikers;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public List<Double> getOrigin() {
        return origin;
    }

    public void setOrigin(List<Double> origin) {
        this.origin = origin;
    }

    public List<Double> getDestination() {
        return destination;
    }

    public void setDestination(List<Double> destination) {
        this.destination = destination;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getKm() {
        return km;
    }

    public void setKm(Double km) {
        this.km = km;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<String> getBikers() {
        return bikers;
    }

    public void setBikers(List<String> bikers) {
        this.bikers = bikers;
    }
}
