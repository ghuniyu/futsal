package id.ghostown.letskicks.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.orhanobut.hawk.Hawk;

import butterknife.ButterKnife;

abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutRes());

        Hawk.init(this).build();
        ButterKnife.bind(this);
    }

    abstract int getLayoutRes();
}
