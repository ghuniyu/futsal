package id.ghostown.letskicks.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import id.ghostown.letskicks.Constants;
import id.ghostown.letskicks.R;
import id.ghostown.letskicks.model.Futsal;
import id.ghostown.letskicks.model.Info;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private static final long LOCATION_REFRESH_DISTANCE = 10;
    private static final long LOCATION_REFRESH_TIME = 1000 * 60 * 1;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private ProgressDialog progressDialog;

    List<Info> latLngList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Connecting to Server");
        progressDialog.setTitle("Loading...");
        progressDialog.show();
        Hawk.init(this).build();
        latLngList = new ArrayList<>();

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Toast.makeText(MapsActivity.this, "Masuk", Toast.LENGTH_SHORT).show();
                Hawk.put(Constants.LOCATION, new LatLng(location.getLatitude(), location.getLongitude()));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, mLocationListener);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference(Constants.APPS);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                latLngList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Futsal futsal = data.getValue(Futsal.class);
                    Log.e("X", data.getKey());
                    latLngList.add(new Info(futsal.name, futsal.coordinate, data.getKey()));
                    Log.e("X", futsal.coordinate);
                }
                for (Info i : latLngList) {
                    Marker marker = mMap.addMarker(new MarkerOptions().position(i.location).title(i.nama + " Futsal").snippet(i.key));
                    marker.showInfoWindow();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                startActivity(new Intent(MapsActivity.this, LapanganActivity.class)
                        .putExtra(Constants.SESSION, marker.getSnippet()));
                return false;
            }
        });
    }
}
