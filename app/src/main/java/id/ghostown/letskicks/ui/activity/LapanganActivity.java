package id.ghostown.letskicks.ui.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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
import id.ghostown.letskicks.Constants;
import id.ghostown.letskicks.R;
import id.ghostown.letskicks.model.Futsal;
import id.ghostown.letskicks.model.Lapangan;
import id.ghostown.letskicks.ui.adapter.LapanganAdapter;

/**
 * Created by iamnubs on 5/15/17.
 */

public class LapanganActivity extends BaseActivity {
    @BindView(R.id.recycler)
    RecyclerView recycler;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    ProgressDialog progressDialog;

    Futsal futsal;
    List<Lapangan> lapanganList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Menghubungkan ke Server");
        progressDialog.show();

        lapanganList = new ArrayList<>();

        Hawk.put(Constants.SESSION, getIntent().getStringExtra(Constants.SESSION));
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference(Constants.APPS).child(Hawk.get(Constants.SESSION).toString());

        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                futsal = dataSnapshot.getValue(Futsal.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lapanganList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey().contains(Constants.LAPANGAN)) {
                        Log.e("X", child.getKey());
                        lapanganList.add(child.getValue(Lapangan.class));
                    }
                }
                recycler.setLayoutManager(new LinearLayoutManager(LapanganActivity.this));
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
    int getLayoutRes() {
        return R.layout.activity_lapangan;
    }

    @OnClick(R.id.call)
    void call() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + futsal.phone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }
}
