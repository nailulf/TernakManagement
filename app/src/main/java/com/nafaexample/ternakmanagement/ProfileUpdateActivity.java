package com.nafaexample.ternakmanagement;

import android.content.Intent;
import android.net.Uri;
import android.os.RecoverySystem;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nafaexample.ternakmanagement.models.User;

import java.util.Map;

public class ProfileUpdateActivity extends AppCompatActivity {

    private static final String EXTRA_USER_ID = "ID User";
    private static final String TAG = "Profile Update Activity";
    private static final int GALLERY_REQUEST = 0;

    private Uri mImageUri;
    private String user_Id;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    private EditText nameEdit, farmEdit, phoneEdit, emailEdit;
    private FloatingActionButton selectProfPict;
    private Button saveBtn;
    private ImageView profilePict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        user_Id = getIntent().getStringExtra(EXTRA_USER_ID);
        if (user_Id == null){
            throw new IllegalArgumentException("Must pass EXTRA_TERNAK_KEY");
        }else{
            Log.d(TAG,"Success get intent extra key" + user_Id);
        }
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(user_Id);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                nameEdit.setText(user.user);
                phoneEdit.setText(user.phone);
                emailEdit.setText(user.email);
                farmEdit.setText(user.farm);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        nameEdit =(EditText) findViewById(R.id.farmerNameET);
        phoneEdit =(EditText) findViewById(R.id.farmerPhoneET);
        emailEdit =(EditText) findViewById(R.id.farmerEmailET);
        farmEdit =(EditText) findViewById(R.id.farmerFarmET);

        selectProfPict =(FloatingActionButton)findViewById(R.id.selectProfPict);
        saveBtn =(Button)findViewById(R.id.saveBtn);

        profilePict =(ImageView)findViewById(R.id.profilePhotoEdit);

        selectProfPict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
                intentGallery.setType("image/*");
                startActivityForResult(intentGallery, GALLERY_REQUEST);

            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfile();
                boolean success = true;
                if(success) {
                    Toast.makeText(ProfileUpdateActivity.this,
                            "Success Update Profile!",
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"Success Update!");
                    finish();
                }else {
                    Toast.makeText(ProfileUpdateActivity.this,
                            "Can't Update!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void saveProfile() {
        final String name = nameEdit.getText().toString().trim();
        final String farm = farmEdit.getText().toString().trim();
        final String phone = phoneEdit.getText().toString().trim();
        final String email = emailEdit.getText().toString().trim();

        if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(phone)&&!TextUtils.isEmpty(farm)
                &&!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(String.valueOf(mImageUri))){
            StorageReference filePath = mStorage.child("user-image").child(user_Id).child(mImageUri.getLastPathSegment());
            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    final String imageUrl = downloadUrl.toString();

                    User user = new User(name, farm, phone, email, imageUrl);
                    Map<String, Object> valueUpdate = user.toMapUp();
                    mDatabase.updateChildren(valueUpdate);

                }
            });
        }else{
            Toast.makeText(this, "Please fill all the blank",Toast.LENGTH_SHORT).show();
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

            mImageUri = data.getData();

            profilePict.setImageURI(mImageUri);
        }
    }
}
