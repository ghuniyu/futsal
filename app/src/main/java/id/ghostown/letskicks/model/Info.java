package id.ghostown.letskicks.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by iamnubs on 5/15/17.
 */

public class Info {
    public String nama;
    public LatLng location;
    public String key;

    public Info(String nama, String latlng, String key) {
        this.key = key;
        this.nama = nama;
        String[] tmp = latlng.split(",");
        this.location = new LatLng(Double.parseDouble(tmp[0]), Double.parseDouble(tmp[1]));
    }
}
