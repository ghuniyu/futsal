package id.ghostown.letskicks.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by iamnubs on 5/14/17.
 */

@IgnoreExtraProperties
public class Lapangan {
    public String name;
    public String price;
    public boolean status;
    public String time;

    public Lapangan() {
    }
}
