package id.ghostown.letskicks.ui.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.hawk.Hawk;

import java.util.List;

import id.ghostown.letskicks.Constants;
import id.ghostown.letskicks.R;
import id.ghostown.letskicks.model.Lapangan;

/**
 * Created by iamnubs on 5/15/17.
 */

public class LapanganAdapter extends BaseQuickAdapter<Lapangan, BaseViewHolder> {
    public LapanganAdapter(@LayoutRes int layoutResId, @Nullable List<Lapangan> data) {
        super(layoutResId, data);
    }

    private FirebaseDatabase mFirebaseInstance;

    @Override
    protected void convert(final BaseViewHolder helper, final Lapangan item) {
        helper.setText(R.id.lapangan, item.name);
        helper.setText(R.id.price, item.price);

        if (item.status) {
            helper.setVisible(R.id.available, true);
        } else
            helper.setVisible(R.id.unavailable, true);

        Button book = helper.getView(R.id.book);
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.status) {
                    mFirebaseInstance = FirebaseDatabase.getInstance();
                    mFirebaseInstance
                            .getReference(Constants.APPS)
                            .child(Hawk.get(Constants.SESSION).toString())
                            .child(Constants.LAPANGAN + item.name).child("status")
                            .setValue(false);
                } else {
                    Toast.makeText(mContext, "Lapangan Sudah di Booking", Toast.LENGTH_SHORT).show();
                }

            }
        });

        final EditText jam = helper.getView(R.id.pick);
        jam.addTextChangedListener(new TextWatcher() {
            int len = 0;

            @Override
            public void afterTextChanged(Editable s) {
                String str = jam.getText().toString();
                if (str.length() == 2 && len < str.length()) {
                    jam.append(":");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                String str = jam.getText().toString();
                len = str.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }


        });

    }
}
