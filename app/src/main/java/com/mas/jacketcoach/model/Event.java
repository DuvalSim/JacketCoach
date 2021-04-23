package com.mas.jacketcoach.model;

import com.google.firebase.database.DataSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Event implements Serializable {
    private String id;
    private String idOrganizer;
    private String name;
    private String sport;
    private String date;
    private double latitude;
    private double longitude;
    private ArrayList<String> players;
    private String description;
    private int maxplayers;

    // Empty constructor required for reading database into an object using ValueEventListener
    public Event() {

    }

    public Event(String id, String idOrganizer, String name, String sport, String date, double latitude, double longitude, ArrayList<String> players, String description, int maxplayers) {
        this.id = id;
        this.idOrganizer = idOrganizer;
        this.name = name;
        this.sport = sport;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.players = players;
        this.description = description;
        this.maxplayers = maxplayers;
    }

    public static Event fromDataSnapshot(DataSnapshot eventSnapshot){
        String id = eventSnapshot.child("id").getValue().toString();
        String idOrganizer = eventSnapshot.child("idOrganizer").getValue().toString();
        String name = eventSnapshot.child("name").getValue().toString();
        String sport = eventSnapshot.child("sport").getValue().toString();
        String date = eventSnapshot.child("date").getValue().toString();
        String description = eventSnapshot.child("description").getValue().toString();
        int maxplayers = Integer.parseInt(eventSnapshot.child("maxplayers").getValue().toString());
        double latitude = Double.parseDouble(eventSnapshot.child("latitude").getValue().toString());
        double longitude = Double.parseDouble(eventSnapshot.child("longitude").getValue().toString());
        ArrayList<String> players = new ArrayList<>();
        for (DataSnapshot player : eventSnapshot.child("players").getChildren()) {
            players.add(player.getValue().toString());
        }
        return new Event(id, idOrganizer, name, sport, date, latitude, longitude, players, description, maxplayers);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxplayers() {
        return maxplayers;
    }

    public void setMaxplayers(int maxplayers) {
        this.maxplayers = maxplayers;
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
                ", description='" + description + '\'' +
                ", maxplayers=" + maxplayers +
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
                description.equals(event.description) &&
                maxplayers==event.maxplayers &&
                Objects.equals(players, event.players);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idOrganizer, name, sport, date, latitude, longitude, players, description, maxplayers);
    }


}
