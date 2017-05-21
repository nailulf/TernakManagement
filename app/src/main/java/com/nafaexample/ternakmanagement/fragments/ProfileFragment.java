package com.nafaexample.ternakmanagement.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nafaexample.ternakmanagement.CattleDetailActivity;
import com.nafaexample.ternakmanagement.MainActivity;
import com.nafaexample.ternakmanagement.ProfileUpdateActivity;
import com.nafaexample.ternakmanagement.R;
import com.nafaexample.ternakmanagement.models.User;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    public static final String TAG ="Profile";
    public static final int REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST = 0;
    public DatabaseReference mDatabase;
    private TextView nameTxt, farmTxt, phoneTxt, emailTxt, cattleNumTxt;
    private ImageView profilePict;
    private Button editBtn, logoutBtn;
    private ProgressDialog mProgressDialog;

    public ProfileFragment(){
        //empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(getUid());
        Log.d(TAG, "onCreateView: "+mDatabase);
        mProgressDialog = new ProgressDialog(getContext());
        nameTxt =(TextView) rootView.findViewById(R.id.farmerName);
        farmTxt =(TextView) rootView.findViewById(R.id.farmerFarm);
        phoneTxt =(TextView) rootView.findViewById(R.id.farmerPhone);
        emailTxt =(TextView) rootView.findViewById(R.id.farmerEmail);
        cattleNumTxt =(TextView) rootView.findViewById(R.id.farmerCattles);
        profilePict =(ImageView) rootView.findViewById(R.id.profilePhoto);

        logoutBtn =(Button) rootView.findViewById(R.id.logoutProfileBtn);
        editBtn =(Button) rootView.findViewById(R.id.editProfileBtn);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent edit = new Intent(getActivity(), ProfileUpdateActivity.class);
                edit.putExtra("ID User",String.valueOf(getUid()));
                startActivityForResult(edit, GALLERY_REQUEST);
            }
        });

        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProgressDialog.setMessage("Retrieveing profile");
        mProgressDialog.show();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);;
                nameTxt.setText(user.user);
                farmTxt.setText(user.farm);
                phoneTxt.setText(user.phone);
                emailTxt.setText(user.email);
                getCattleNum();
                Picasso.with(getContext()).load(user.image).into(profilePict);
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(getActivity(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        });

    }

    private void getCattleNum() {
        DatabaseReference mUsercattleRef = FirebaseDatabase.getInstance().getReference()
                .child("user-cattles").child(getUid());
        mUsercattleRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();
                cattleNumTxt.setText(String.valueOf(count));
                Log.d(TAG, "onDataChange => count : "+count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
