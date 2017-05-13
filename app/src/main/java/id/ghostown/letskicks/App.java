package id.ghostown.letskicks;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.hawk.Hawk;

public final class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Hawk.init(this).build();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
