package be.howest.dylandeceulaer.places;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Dylan on 12/04/2015.
 */
public class MarkerInfo {

    private long Id;
    private String titel;
    private LatLng positie;
    private String adres;
    private Data.MARKER markerData;
    private String plaats;

    public MarkerInfo(String titel, LatLng positie, String adres, Data.MARKER markerData) {
        this.titel = titel;
        this.positie = positie;
        this.adres = adres;
        this.markerData = markerData;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public LatLng getPositie() {
        return positie;
    }

    public void setPositie(LatLng positie) {
        this.positie = positie;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public Data.MARKER getMarkerData() {
        return markerData;
    }

    public void setMarkerData(Data.MARKER markerData) {
        this.markerData = markerData;
    }

    public String getPlaats() {
        return plaats;
    }

    public void setPlaats(String plaats) {
        this.plaats = plaats;
    }
}
