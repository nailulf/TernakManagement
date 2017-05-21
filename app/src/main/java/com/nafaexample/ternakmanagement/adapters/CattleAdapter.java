package com.nafaexample.ternakmanagement.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nafaexample.ternakmanagement.models.Cattle;
import com.nafaexample.ternakmanagement.R;
import com.squareup.picasso.Picasso;

import static com.nafaexample.ternakmanagement.R.id.card_ternak_thumb;

/**
 * Created by Nailul on 4/14/2017.
 */

public class CattleAdapter extends RecyclerView.ViewHolder {

    public TextView cattleId, cattleBW, cattleSpes, cattleLastUp;
    public ImageView cattleThumb;

    public CattleAdapter(View view) {
        super(view);

        cattleId = (TextView) view.findViewById(R.id.ternak_id);
        cattleBW = (TextView) view.findViewById(R.id.ternak_bb);
        cattleSpes = (TextView) view.findViewById(R.id.ternak_jenis);
        cattleLastUp = (TextView) view.findViewById(R.id.ternak_tgl_update);
        cattleThumb = (ImageView)view.findViewById(card_ternak_thumb);


    }

    public void bindToCattle(Cattle cattle ) {
        cattleId.setText(cattle.id);
        cattleBW.setText(String.valueOf(cattle.bw));
        cattleSpes.setText(cattle.spes);
        cattleLastUp.setText(DateFormat.format("dd-MM-yyyy",
                cattle.timestamp));
        Picasso.with(itemView.getContext())
                .load(cattle.image)
                .resize(80,80)
                .centerCrop()
                .into(cattleThumb);

    }

}
