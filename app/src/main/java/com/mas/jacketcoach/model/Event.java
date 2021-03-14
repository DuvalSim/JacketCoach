package com.mas.jacketcoach.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Event {
    private int id;
    private String idOrganizer;
    private String name;
    private String sport;
    private String date;
    private double latitude;
    private double longitude;
    private ArrayList<String> players;

    public Event(int id, String idOrganizer, String name, String sport, String date, double latitude, double longitude, ArrayList<String> players) {
        this.id = id;
        this.idOrganizer = idOrganizer;
        this.name = name;
        this.sport = sport;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.players = players;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdOrganizer() {
        return idOrganizer;
    }

    public void setIdOrganizer(String idOrganizer) {
        this.idOrganizer = idOrganizer;
    }

    public String getName() {
        return name;
    }

    public void setName(String nom) {
        this.name = nom;
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

    public ArrayList<String> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<String> players) {
        this.players = players;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", idOrganizer=" + idOrganizer +
                ", name='" + name + '\'' +
                ", sport='" + sport + '\'' +
                ", date='" + date + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", players=" + players +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id == event.id &&
                idOrganizer.equals(event.idOrganizer) &&
                Double.compare(event.latitude, latitude) == 0 &&
                Double.compare(event.longitude, longitude) == 0 &&
                name.equals(event.name) &&
                sport.equals(event.sport) &&
                date.equals(event.date) &&
                Objects.equals(players, event.players);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idOrganizer, name, sport, date, latitude, longitude, players);
    }
}
