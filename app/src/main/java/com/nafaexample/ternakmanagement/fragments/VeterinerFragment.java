package com.nafaexample.ternakmanagement.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nafaexample.ternakmanagement.R;
import com.nafaexample.ternakmanagement.adapters.VeterinerAdapter;
import com.nafaexample.ternakmanagement.models.Veteriner;
import com.nafaexample.ternakmanagement.utils.FirebaseUtils;

public class VeterinerFragment extends Fragment {

    private final String TAG="Veteriner Fragment";
    private RecyclerView recyclerVetView;

    private LinearLayoutManager layoutManager;
    private ProgressDialog mProgressDialog;
    private FirebaseRecyclerAdapter<Veteriner, VeterinerAdapter> vetsAdapter;
    private String phoneNumber;

    public VeterinerFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){
        View rootView= inflater.inflate(R.layout.fragment_veteriner, container, false);
        recyclerVetView = (RecyclerView) rootView.findViewById(R.id.veteriner_recyclerview);
        recyclerVetView.setHasFixedSize(true);

        mProgressDialog = new ProgressDialog(getContext());

        return rootView;

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, ":Start Fragment!");

        // Set up Layout Manager, reverse layout
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerVetView.setLayoutManager(layoutManager);
        mProgressDialog.setMessage("Loading data...");
        mProgressDialog.show();
        //set up query
        final Query vetQuery = FirebaseUtils.getVetQuery();
        vetsAdapter = new FirebaseRecyclerAdapter<Veteriner, VeterinerAdapter>(Veteriner.class,
                R.layout.vet_cardview_item, VeterinerAdapter.class, vetQuery) {
            @Override
            protected void populateViewHolder(VeterinerAdapter viewHolder, Veteriner model, int position) {
                final DatabaseReference vetRef = getRef(position);
                final String vetKey = vetRef.getKey();
                Log.d(TAG, "populateViewHolder: "+vetKey);
                vetQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Veteriner vet = dataSnapshot.child(vetKey).getValue(Veteriner.class);
                        phoneNumber = vet.phone;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                viewHolder.bindToVeteriner(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callVet(phoneNumber);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        messageVet(phoneNumber);
                    }
                });
                mProgressDialog.dismiss();
            }

        };
        recyclerVetView.setAdapter(vetsAdapter);

    }
    private void callVet(String phoneNum) {
        Log.d(TAG, "callVet: "+phoneNum);
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" +phoneNum));
        startActivity(callIntent);
    }

    private void messageVet(String phoneNum) {
        Log.d(TAG, "MessageVet: "+phoneNum);
        Intent messageIntent = new Intent(Intent.ACTION_VIEW);
        messageIntent.setData(Uri.fromParts("sms", phoneNum, null));
        startActivity(messageIntent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (vetsAdapter != null) {
            vetsAdapter.cleanup();
        }

    }

}
