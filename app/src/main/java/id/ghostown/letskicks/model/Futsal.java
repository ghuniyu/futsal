package id.ghostown.letskicks.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by iamnubs on 5/14/17.
 */

@IgnoreExtraProperties
public class Futsal {
    public Futsal(){
    }

    public String name;
    public String phone;
    public String coordinate;
    public Lapangan lapangan;

    public Futsal(String name, String phone, String coordinate) {
        this.name = name;
        this.phone = phone;
        this.coordinate = coordinate;
        lapangan = new Lapangan();
    }

    public Futsal(String name, String phone, String coordinate, Lapangan lapangan) {
        this.name = name;
        this.phone = phone;
        this.coordinate = coordinate;
        this.lapangan = lapangan;
    }
}
