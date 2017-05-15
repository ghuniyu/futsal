package id.ghostown.futsal.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.ghostown.futsal.Constants;
import id.ghostown.futsal.R;
import id.ghostown.futsal.model.Futsal;
import id.ghostown.futsal.model.Lapangan;
import id.ghostown.futsal.ui.adapter.LapanganAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.lapangan)
    EditText lapangan;
    @BindView(R.id.price)
    EditText price;


    @BindView(R.id.tambah)
    Button tambah;
    @BindView(R.id.recycler)
    RecyclerView recycler;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    ProgressDialog progressDialog;


    GoogleApiClient mGoogleApiClient;

    Futsal futsal;
    List<Lapangan> lapanganList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Menghubungkan ke Server");
        progressDialog.show();

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.google_web_client_id))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .enableAutoManage(this, this)
                .build();

        mGoogleApiClient.connect();

        ButterKnife.bind(this);

        lapanganList = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        TextView name = (TextView) header.findViewById(R.id.name);
        TextView email = (TextView) header.findViewById(R.id.email);
        email.setText(Hawk.get(Constants.EMAIL, "invalid email"));
        name.setText(Hawk.get(Constants.USERS, "invalid name"));

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference(Constants.APPS);

        mFirebaseDatabase.child(Hawk.get(Constants.USERS).toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Hawk.put(Constants.SESSION, dataSnapshot.getValue(Futsal.class));

                futsal = Hawk.get(Constants.SESSION);
                if (futsal == null) {
                    lapangan.setEnabled(false);
                    price.setEnabled(false);
                    tambah.setEnabled(false);
                    Toast.makeText(MainActivity.this, "Anda belum Harus Mengisi Profile", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        valueListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseDatabase.child(Hawk.get(Constants.USERS).toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Hawk.put(Constants.SESSION, dataSnapshot.getValue(Futsal.class));
                futsal = Hawk.get(Constants.SESSION);
                if (futsal == null) {
                    lapangan.setEnabled(false);
                    price.setEnabled(false);
                    tambah.setEnabled(false);
                    Toast.makeText(MainActivity.this, "Anda belum Harus Mengisi Profile", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                }else {
                    lapangan.setEnabled(true);
                    price.setEnabled(true);
                    tambah.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            startActivity(new Intent(this, LoginActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            );
            Hawk.deleteAll();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            FirebaseAuth.getInstance().signOut();
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hai, Saya Menggunakan Aplikasi Let's Kick");
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick(R.id.tambah)
    void tambah() {
        progressDialog.show();

        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mFirebaseDatabase.child(Hawk.get(Constants.USERS).toString()).child(Constants.LAPANGAN + lapangan.getText().toString()).setValue(new Lapangan(
                        lapangan.getText().toString(),
                        price.getText().toString()
                ));
                valueListener();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    void valueListener() {
        mFirebaseDatabase.child(Hawk.get(Constants.USERS).toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lapanganList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey().contains(Constants.LAPANGAN)) {
                        Log.e("X", child.getKey());
                        lapanganList.add(child.getValue(Lapangan.class));
                    }
                }
                recycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                recycler.setAdapter(new LapanganAdapter(R.layout.item_lapangan, lapanganList));

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("Activity", connectionResult.getErrorMessage());
    }
}
