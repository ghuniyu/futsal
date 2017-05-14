package id.ghostown.futsal.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by iamnubs on 5/14/17.
 */

@IgnoreExtraProperties
public class Lapangan {
    public String price;
    public boolean status;

    public Lapangan() {
    }

    public Lapangan(String price) {
        this.price = price;
        status = true;
    }
}
