package id.ghostown.futsal.ui.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.hawk.Hawk;

import java.util.List;

import id.ghostown.futsal.Constants;
import id.ghostown.futsal.R;
import id.ghostown.futsal.model.Futsal;
import id.ghostown.futsal.model.Lapangan;

/**
 * Created by iamnubs on 5/15/17.
 */

public class LapanganAdapter extends BaseQuickAdapter<Lapangan, BaseViewHolder> {
    public LapanganAdapter(@LayoutRes int layoutResId, @Nullable List<Lapangan> data) {
        super(layoutResId, data);
    }

    private FirebaseDatabase mFirebaseInstance;

    @Override
    protected void convert(BaseViewHolder helper, final Lapangan item) {
        helper.setText(R.id.lapangan, item.name);
        helper.setText(R.id.price, item.price);

        if (item.status) {
            helper.setVisible(R.id.available, true);
        } else
            helper.setVisible(R.id.unavailable, true);

        Button delete = helper.getView(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Futsal futsal = Hawk.get(Constants.SESSION);
                mFirebaseInstance = FirebaseDatabase.getInstance();
                mFirebaseInstance.getReference(Constants.APPS).child(futsal.name).child(Constants.LAPANGAN + item.name).removeValue();
                Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
