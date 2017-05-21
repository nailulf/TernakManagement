package com.nafaexample.ternakmanagement.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nafaexample.ternakmanagement.adapters.CattleAdapter;
import com.nafaexample.ternakmanagement.models.Cattle;
import com.nafaexample.ternakmanagement.NewCattleActivity;
import com.nafaexample.ternakmanagement.R;
import com.nafaexample.ternakmanagement.CattleDetailActivity;

public class CattleFragment extends Fragment{

    private final String TAG = "Cattle Fragment";
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Cattle, CattleAdapter> cattlesAdapter;

    protected LinearLayoutManager layoutManager;
    private FloatingActionButton addCattleBtn;

    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private ProgressDialog mProgressDialog;

    public CattleFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_cattle, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.ternak_recycleview);
        recyclerView.setHasFixedSize(true);

        addCattleBtn =(FloatingActionButton)rootView.findViewById(R.id.addCattleBtn);
        addCattleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newTernak = new Intent(getActivity(), NewCattleActivity.class);
                startActivity(newTernak);
            }
        });
        mProgressDialog = new ProgressDialog(getContext());
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, ":Start Fragment!");
        Log.d(TAG, "onCreate: "+ mDatabase);
        Log.d(TAG, "Uid: "+ getUid());

        // Set up Layout Manager, reverse layout
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        mProgressDialog.setMessage("Loading data...");
        mProgressDialog.show();
        //set up query
        Query cattleQuery = getQuery(mDatabase);
        cattlesAdapter = new FirebaseRecyclerAdapter<Cattle, CattleAdapter>(Cattle.class,
                R.layout.ternak_cardview_item,CattleAdapter.class, cattleQuery) {
            @Override
            protected void populateViewHolder(CattleAdapter viewHolder, Cattle model,
                                              int position) {
                final DatabaseReference cattleRef = getRef(position);
                final String cattleKey = cattleRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("Cattle clicked :", String.valueOf(cattleKey));
                        Intent i = new Intent(getActivity(), CattleDetailActivity.class);
                        i.putExtra(CattleDetailActivity.EXTRA_CATTLE_KEY, cattleKey);
                        startActivity(i);
                    }
                });
                viewHolder.bindToCattle(model);
                mProgressDialog.dismiss();
            }
        };
        recyclerView.setAdapter(cattlesAdapter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cattlesAdapter != null) {
            cattlesAdapter.cleanup();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("user-cattles")
                .child(getUid());
    }

}
