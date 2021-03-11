package com.mas.jacketcoach.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class Event {
    private int id;
    private String nom;
    private String sport;
    private String date;
    private double latitude;
    private double longitude;

    public Event(int id, String nom, String sport, String date, double latitude, double longitude) {
        this.id = id;
        this.nom = nom;
        this.sport = sport;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id == event.id &&
                Double.compare(event.latitude, latitude) == 0 &&
                Double.compare(event.longitude, longitude) == 0 &&
                Objects.equals(nom, event.nom) &&
                Objects.equals(sport, event.sport) &&
                Objects.equals(date, event.date);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(id, nom, sport, date, latitude, longitude);
    }
}
