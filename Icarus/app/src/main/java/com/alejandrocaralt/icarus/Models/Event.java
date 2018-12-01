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
    @SerializedName("name")
    private String name ;

    @Expose
    @SerializedName("city")
    private String city ;

    @Expose
    @SerializedName("km")
    private Double km ;

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

    public Event(String id, String name, String city, Double km, List<Double> origin, List<Double> destination, String owner, List<String> bikers) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.km = km;
        this.origin = origin;
        this.destination = destination;
        this.owner = owner;
        this.bikers = bikers;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
