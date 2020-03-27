package com.pprcan.rezervacije;

public class Rezervacija {

    public int pin, broj_osoba;
    public String restoran, datum, vrijeme, ime;

    public Rezervacija(int pin, int broj_osoba, String restoran, String datum, String vrijeme, String ime) {
        this.pin = pin;
        this.broj_osoba = broj_osoba;
        this.restoran = restoran;
        this.datum = datum;
        this.vrijeme = vrijeme;
        this.ime = ime;
    }

    public int getPin() {
        return pin;
    }

    public int getBroj_osoba() {
        return broj_osoba;
    }

    public String getRestoran() {
        return restoran;
    }

    public String getDatum() {
        return datum;
    }

    public String getVrijeme() {
        return vrijeme;
    }

    public String getIme() {
        return ime;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public void setBroj_osoba(int broj_osoba) {
        this.broj_osoba = broj_osoba;
    }

    public void setRestoran(String restoran) {
        this.restoran = restoran;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public void setVrijeme(String vrijeme) {
        this.vrijeme = vrijeme;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }
}
