package com.nafaexample.ternakmanagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.Map;

public class CattleUpdateActivity extends AppCompatActivity {

    public static final String TAG ="Cattle Update";
    public static final int REQUEST_CODE = 1;
    public static String EXTRA_CATTLE_KEY ="ID Cattle";
    public static final int GALLERY_REQUEST =0;

    private String cattle_key;
    private Uri mImageUri;

    private long updateCount;
    private TextView cattleID;
    private EditText cattleBW, cattleBH, cattleBC,cattleColor,cattleAge;
    private ImageView dispPict;
    private Button updateCattle, medicHistory;
    private FloatingActionButton addPicture;

    private DatabaseReference mDatabase;
    private DatabaseReference mDbUser;
    private StorageReference mStorage;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cattle_update);
        Log.d(TAG, ": Activity Started!");

        cattle_key = getIntent().getStringExtra(EXTRA_CATTLE_KEY);
        if (cattle_key == null){
            throw new IllegalArgumentException("Must pass EXTRA_TERNAK_KEY");
        }else{
            Log.d(TAG,"Success get intent extra key" + cattle_key);
        }

        cattleID =(TextView)findViewById(R.id.idCattle_txt_up);
        cattleAge = (EditText)findViewById(R.id.ageCattle_et);
        cattleBW = (EditText) findViewById(R.id.bwCattle_et);
        cattleBH = (EditText) findViewById(R.id.bhCattle_et);
        cattleBC = (EditText) findViewById(R.id.bcCattle_et);
        cattleColor = (EditText) findViewById(R.id.colorCattle_et);

        dispPict =(ImageView) findViewById(R.id.cattlePict_Up);
        mProgressDialog = new ProgressDialog(this);
        updateCattle = (Button)findViewById(R.id.updateDetail_btn);
        addPicture =(FloatingActionButton)findViewById(R.id.add_picture);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("cattle").child(cattle_key).child("init-data");
        mDbUser = FirebaseDatabase.getInstance().getReference()
                .child("user-cattles").child(getUid()).child(cattle_key);
        Log.d(TAG, "onCreate: "+mDbUser);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Cattle cattle = dataSnapshot.getValue(Cattle.class);
                updateCount = cattle.updateCount;
                cattleID.setText(cattle.id);
                Log.d(TAG, "=> cattle ID : "+ cattle.id);
                Log.d(TAG, "=> get count : " + updateCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Toolbar toolbarUpdate =(Toolbar)findViewById(R.id.toolbar_update);
        toolbarUpdate.setTitle(TAG);
        setSupportActionBar(toolbarUpdate);

        toolbarUpdate.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbarUpdate.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                setResult(REQUEST_CODE, i);
                finish();
            }
        });

        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
                intentGallery.setType("image/*");
                startActivityForResult(intentGallery, GALLERY_REQUEST);
            }
        });

        updateCattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(cattleBW.getText().toString())
                        &&!TextUtils.isEmpty(cattleBC.getText().toString())
                        &&!TextUtils.isEmpty(cattleBH.getText().toString())
                        &&!TextUtils.isEmpty(cattleColor.getText().toString())
                        && !TextUtils.isEmpty(String.valueOf(mImageUri))) {
                    updateDataCattle(updateCount);
                }else{
                    Toast.makeText(CattleUpdateActivity.this, "Please fill all the blank",Toast.LENGTH_SHORT).show();
                }
                boolean success = true;
                if(success) {
                    Toast.makeText(CattleUpdateActivity.this,
                            "Success Update Data!",
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"Success Update!");
                    Intent i = new Intent();
                    setResult(REQUEST_CODE, i);
                    finish();
                }else {
                    Toast.makeText(CattleUpdateActivity.this,
                            "Cant Update!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateDataCattle(long updateCount){
        final long bwCattle = Long.parseLong(String.valueOf(cattleBW.getText()));
        final long bhCattle = Long.parseLong(String.valueOf(cattleBH.getText()));
        final long bcCattle = Long.parseLong(String.valueOf(cattleBC.getText()));
        final long ageCattle = Long.parseLong(String.valueOf(cattleAge.getText()));
        final String colorCattle = cattleColor.getText().toString().trim();
        final int index = (int) (updateCount +1);
        final boolean mhCattle = false;



        mProgressDialog.setMessage("Updating data...");
        mProgressDialog.show();
        StorageReference filePath = mStorage.child("cattle-image").child(cattle_key).child(mImageUri.getLastPathSegment());
        filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                final String imageUrl = downloadUrl.toString();

                DatabaseReference mUpdateRef = FirebaseDatabase.getInstance().getReference()
                        .child("cattle").child(cattle_key);

                Cattle cattle1 = new Cattle(colorCattle, imageUrl, bwCattle, bhCattle,
                        bcCattle, ageCattle, mhCattle);
                Map<String, Object> cattleValueUpdate = cattle1.toMapUp();

                Map<String, Object> upCount = new HashMap<>();
                upCount.put("/update-data-" + index, cattleValueUpdate);

                mUpdateRef.updateChildren(upCount);
                mDatabase.child("updateCount").setValue(index);
                mDbUser.updateChildren(cattleValueUpdate);
            }
        });
        mProgressDialog.dismiss();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

            mImageUri = data.getData();

            dispPict.setImageURI(mImageUri);
        }
    }


    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}
