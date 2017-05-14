package id.ghostown.futsal.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by iamnubs on 5/14/17.
 */

@IgnoreExtraProperties
public class Futsal {
    public Futsal() {
    }

    public String phone;
    public String coordinate;
    public Lapangan lapangan;

    public Futsal(String phone, String coordinate) {
        this.phone = phone;
        this.coordinate = coordinate;
        lapangan = new Lapangan();
    }

    public Futsal(String phone, String coordinate, Lapangan lapangan) {
        this.phone = phone;
        this.coordinate = coordinate;
        this.lapangan = lapangan;
    }
}
