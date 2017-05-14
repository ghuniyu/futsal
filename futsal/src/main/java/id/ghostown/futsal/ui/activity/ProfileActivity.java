package id.ghostown.futsal.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.hawk.Hawk;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.ghostown.futsal.Constants;
import id.ghostown.futsal.R;
import id.ghostown.futsal.model.Futsal;

/**
 * Created by iamnubs on 5/14/17.
 */

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    @BindView(R.id.lokasi)
    Button lokasi;
    @BindView(R.id.latlng)
    EditText latlng;
    @BindView(R.id.nama)
    EditText nama;
    @BindView(R.id.kontak)
    EditText phone;
    @BindView(R.id.save)
    Button save;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    ProgressDialog progressDialog;
    private String USER;

    final static String ERROR = "Field Belum diisi";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        if (Hawk.contains(Constants.SESSION)) {
            Futsal futsal = Hawk.get(Constants.SESSION);
            latlng.setText(futsal.coordinate);
            phone.setText(futsal.phone);
        }

        lokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent intent = null;
                try {
                    intent = builder.build(ProfileActivity.this);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, 1708);
            }
        });
    }

    @OnClick(R.id.save)
    void save() {
        if (latlng.getText().toString().equals("")) {
            latlng.setError(ERROR);
        } else if (nama.getText().toString().equals("")) {
            nama.setError(ERROR);
        } else if (phone.getText().toString().equals("")) {
            phone.setError(ERROR);
        } else {
            updateProfile();
        }
    }

    private void updateProfile() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Menghubungkan ke Server");
        progressDialog.show();

        USER = nama.getText().toString();

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference(Constants.APPS);
        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(USER)){
                    mFirebaseDatabase.child(USER).setValue(new Futsal(
                            phone.getText().toString(),
                            latlng.getText().toString())
                    );
                    valueListener();
                }else {
                    mFirebaseDatabase.child(USER).setValue(new Futsal(
                            phone.getText().toString(),
                            latlng.getText().toString())
                    );
                    valueListener();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void valueListener(){
        mFirebaseDatabase.child(USER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Futsal futsal = dataSnapshot.getValue(Futsal.class);
                Hawk.put(Constants.SESSION, futsal);
                Toast.makeText(ProfileActivity.this, "Berhasil", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        progressDialog.dismiss();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1708)
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                LatLng latLng = place.getLatLng();
                latlng.setText(String.format("%f, %f", latLng.latitude, latLng.longitude));
            }
    }
}
