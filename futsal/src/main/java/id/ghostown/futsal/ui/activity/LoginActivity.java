package id.ghostown.futsal.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.orhanobut.hawk.Hawk;

import butterknife.OnClick;
import id.ghostown.futsal.Constants;
import id.ghostown.futsal.R;

public final class LoginActivity extends BaseActivity implements FirebaseAuth.AuthStateListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private FirebaseAuth mFirebaseAuth;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Hawk.put(Constants.USERS, currentUser.getDisplayName().replace("\\s++", ""));

            startActivity(intent);
        } else {
            Log.i("Activity", "User logout.");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("Activity", connectionResult.getErrorMessage());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE_LOGIN_VIA_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                mFirebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(result.getSignInAccount().getIdToken(), null));
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("Activity", result.getStatus().toString());
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.google_web_client_id))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .enableAutoManage(this, this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mFirebaseAuth.addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mFirebaseAuth.removeAuthStateListener(this);
    }

    @Override
    int getLayoutRes() {
        return R.layout.activity_login;
    }

    @OnClick(R.id.action_login)
    void onLoginClick() {
        startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient), Constants.REQUEST_CODE_LOGIN_VIA_GOOGLE);
    }
}
