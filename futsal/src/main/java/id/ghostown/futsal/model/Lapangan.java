package id.ghostown.futsal.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by iamnubs on 5/14/17.
 */

@IgnoreExtraProperties
public class Lapangan {
    public String price;
    public boolean status;
    public String name;

    public Lapangan() {
    }

    public Lapangan(String name, String price) {
        this.name = name;
        this.price = price;
        status = true;
    }
}
