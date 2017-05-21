package com.nafaexample.ternakmanagement;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nafaexample.ternakmanagement.models.Cattle;
import com.nafaexample.ternakmanagement.models.User;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nailul on 4/11/2017.
 */

public class NewCattleActivity extends AppCompatActivity{

    private static final String TAG = "New Cattle Activity";
    private static final String REQUIRED = "Required!";
    private static final int GALLERY_REQUEST =1;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private EditText mCattleID, mCattleBW, mCattleBH, mCattleBC, mCattleAge, mCattleDD, mCattleColor, mCattleSpec;
    private long mCattleUpDate;
    private Uri mImageUri = null;
    private Boolean mMedHistory = false;
    private Button mAddNewBtn;
    private FloatingActionButton mAddCatPict;
    private ImageView mCattlePict;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cattle);
        Log.d(TAG,": Activity Start!");
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        Log.d(TAG,": " + currentDateTimeString);

        mCattleID =(EditText)findViewById(R.id.idCattle_et);
        mCattleBW =(EditText)findViewById(R.id.bwCattle_et);
        mCattleBH =(EditText)findViewById(R.id.bhCattle_et);
        mCattleBC =(EditText)findViewById(R.id.bcCattle_et);
        mCattleAge =(EditText)findViewById(R.id.ageCattle_et);
        mCattleColor =(EditText)findViewById(R.id.colorCattle_et);
        mCattleSpec =(EditText)findViewById(R.id.specCattle_et);

        mCattlePict =(ImageView)findViewById(R.id.cattlePict);

        mAddNewBtn =(Button)findViewById(R.id.newCattle_Btn);
        mAddCatPict =(FloatingActionButton) findViewById(R.id.add_cattle_pict);

        mAddCatPict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
                intentGallery.setType("image/*");
                startActivityForResult(intentGallery, GALLERY_REQUEST);
            }
        });
        mAddNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitCattle();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

            mImageUri = data.getData();

            mCattlePict.setImageURI(mImageUri);
        }
    }

    private void submitCattle() {
        final String idCattle = mCattleID.getText().toString();
        final long bwCattle = Long.parseLong(String.valueOf(mCattleBW.getText().toString()));
        final long bhCattle = Long.parseLong(String.valueOf(mCattleBH.getText().toString()));
        final long bcCattle = Long.parseLong(String.valueOf(mCattleBC.getText().toString()));
        final long ageCattle = Long.parseLong(String.valueOf(mCattleAge.getText().toString()));
        final String specCattle = mCattleSpec.getText().toString();
        final String colorCattle = mCattleColor.getText().toString();
        final long day = Long.valueOf(0);
        final int update = 0;
        mMedHistory = false;

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, "Adding...", Toast.LENGTH_SHORT).show();

        // [START single_value_read]
        final String userId = getUid();
        if(!TextUtils.isEmpty(String.valueOf(bwCattle))&&!TextUtils.isEmpty(String.valueOf(bcCattle))
                &&!TextUtils.isEmpty(String.valueOf(bhCattle))&&!TextUtils.isEmpty(colorCattle)
                &&!TextUtils.isEmpty(String.valueOf(ageCattle))&&!TextUtils.isEmpty(specCattle)
                && !TextUtils.isEmpty(String.valueOf(mImageUri))) {
            mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            User user = dataSnapshot.getValue(User.class);

                            // [START_EXCLUDE]
                            if (user == null) {
                                // User is null, error out
                                Log.e(TAG, "User " + userId + " is unexpectedly null");
                                Toast.makeText(NewCattleActivity.this,
                                        "Error: could not fetch user.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                // Write new post
                                startPostCattle(userId, idCattle, specCattle, colorCattle, bwCattle,
                                        bhCattle, bcCattle, ageCattle, day, mMedHistory, update);
                            }

                            // Finish this Activity, back to the stream
                            setEditingEnabled(true);
                            finish();
                            // [END_EXCLUDE]
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                            // [START_EXCLUDE]
                            setEditingEnabled(true);
                            // [END_EXCLUDE]
                        }
                    });
            // [END single_value_read]
        }else{
            Toast.makeText(this, "Please fill all the blank",Toast.LENGTH_SHORT).show();
        }
    }

    private void setEditingEnabled(boolean enabled) {
        mCattleID.setEnabled(enabled);
        mCattleBW.setEnabled(enabled);
        if (enabled) {
            mAddNewBtn.setVisibility(View.VISIBLE);
        } else {
            mAddNewBtn.setVisibility(View.GONE);
        }
    }
    private void startPostCattle(final String userId, final String idCattle, final String specCattle, final String colorCattle,
                                 final long bwCattle, final long bhCattle, final long bcCattle, final long ageCattle, final long day,
                                 final boolean mhCattle, final int update){
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        StorageReference filePath = mStorage.child("cattle-image").child(mImageUri.getLastPathSegment());
        filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                final String imageUrl = downloadUrl.toString();

                String key = mDatabase.child("cattle").push().getKey();
                Cattle cattle = new Cattle(userId, idCattle, specCattle, colorCattle, imageUrl, bwCattle, bhCattle,
                        bcCattle, ageCattle, day, mhCattle, update);
                Cattle cattle1 = new Cattle(colorCattle, imageUrl, bwCattle, bhCattle,
                        bcCattle, ageCattle, mhCattle);
                Map<String, Object> cattleValue = cattle.toMap();
                Map<String, Object> cattleValueUpdate = cattle1.toMapUp();

                Log.d(TAG, "newCattle: " + cattleValueUpdate);

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/cattle/" + key + "/init-data/", cattleValue);
                childUpdates.put("/cattle/" + key + "/update-data-0/", cattleValueUpdate);
                childUpdates.put("/user-cattles/" + userId + "/" + key, cattleValue);
                Log.d(TAG, "newCattle: " + childUpdates);
                mDatabase.updateChildren(childUpdates);
            }
        });

    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
