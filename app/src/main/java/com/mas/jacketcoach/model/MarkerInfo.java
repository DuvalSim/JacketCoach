package com.mas.jacketcoach.model;

// NOTE: CLASS DEPRECATED. Entire Events object is passed in as MarkerInfo since we need more data

@Deprecated
public class MarkerInfo {
    public String nom;
    public String sport;
    public String date;

    public MarkerInfo(String nom, String sport, String date) {
        this.nom = nom;
        this.sport = sport;
        this.date = date;
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
}