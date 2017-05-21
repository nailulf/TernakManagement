package com.nafaexample.ternakmanagement.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nafaexample.ternakmanagement.R;
import com.nafaexample.ternakmanagement.models.Cattle;
import com.nafaexample.ternakmanagement.models.Veteriner;
import com.squareup.picasso.Picasso;

import static com.nafaexample.ternakmanagement.R.id.card_ternak_thumb;

/**
 * Created by Nailul on 4/18/2017.
 */

public class VeterinerAdapter extends RecyclerView.ViewHolder {


    public TextView vetName, vetAdress, vetPhone;
    public ImageView vetThumb;
    public Button callBtn, messageBtn;

    public VeterinerAdapter(View view) {
        super(view);

        vetName =(TextView)view.findViewById(R.id.vet_name);
        vetPhone =(TextView)view.findViewById(R.id.vet_phone);
        vetAdress =(TextView)view.findViewById(R.id.vet_address);

        callBtn =(Button)view.findViewById(R.id.vet_call);
        messageBtn =(Button)view.findViewById(R.id.vet_message);

        vetThumb = (ImageView)view.findViewById(R.id.card_vet_thumb);


    }

    public void bindToVeteriner(Veteriner vet,View.OnClickListener callClickListener,View.OnClickListener messageClickListener ) {
        vetName.setText(vet.name);
        vetPhone.setText(vet.phone);
        vetAdress.setText(vet.adress);
        Picasso.with(itemView.getContext())
                .load(vet.image)
                .resize(80,80)
                .centerCrop()
                .into(vetThumb);

        callBtn.setOnClickListener(callClickListener);
        messageBtn.setOnClickListener(messageClickListener);
    }

}
