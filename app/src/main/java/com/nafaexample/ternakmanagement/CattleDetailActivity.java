package com.nafaexample.ternakmanagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nafaexample.ternakmanagement.models.Cattle;
import com.squareup.picasso.Picasso;

/**
 * Created by Nailul on 4/11/2017.
 */

public class CattleDetailActivity extends AppCompatActivity{

    public static final String TAG ="Cattle Detail";
    public static final int REQUEST_CODE = 1;
    public static final String EXTRA_CATTLE_KEY = "ID Cattle";

    private String cattlekey;
    private int dd;

    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private ValueEventListener mCattleListener;

    private TextView cattleIDView,
            cattleBWView,
            cattleBHView,
            cattleBCView,
            cattleSpesView,
            cattleAgeView,
            cattleDDView,
            cattleColorView;
    private Button updateBtn, reportBtn;
    private ImageView cattlePict;
    private ProgressDialog mProgressDialog;
    private DatabaseReference mRefUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cattle_detail);

        cattlekey = getIntent().getStringExtra(EXTRA_CATTLE_KEY);
        if (cattlekey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_TERNAK_KEY");
        }else{
            Log.d(TAG,"Success get intent extra key");
        }

        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("cattle").child(cattlekey);
        mStorage = FirebaseStorage.getInstance().getReference().child("cattle-images");

        Toolbar toolbarDetail =(Toolbar)findViewById(R.id.toolbar_detail);
        toolbarDetail.setTitle(TAG);
        setSupportActionBar(toolbarDetail);

        toolbarDetail.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbarDetail.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CattleDetailActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        cattleIDView = (TextView)findViewById(R.id.idCattle_txt);
        cattleBWView = (TextView)findViewById(R.id.bwCattle_txt);
        cattleBHView = (TextView)findViewById(R.id.bhCattle_txt);
        cattleBCView = (TextView)findViewById(R.id.bcCattle_txt);
        cattleSpesView = (TextView)findViewById(R.id.spesCattle_txt);
        cattleDDView = (TextView)findViewById(R.id.ddCattle_txt);
        cattleAgeView = (TextView)findViewById(R.id.ageCattle_txt);
        cattleColorView = (TextView)findViewById(R.id.colorCattle_txt);

        cattlePict =(ImageView)findViewById(R.id.ternak_pict);
        updateBtn = (Button)findViewById(R.id.updateDetail_btn);
        reportBtn = (Button)findViewById(R.id.reportDetail_btn);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent v = new Intent(CattleDetailActivity.this, CattleUpdateActivity.class);
                v.putExtra("ID Cattle",String.valueOf(cattlekey));
                startActivityForResult(v, REQUEST_CODE);
            }
        });

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent report = new Intent(CattleDetailActivity.this, ReportActivity.class);
                report.putExtra(ReportActivity.EXTRA_CATTLE_KEY, cattlekey);
                startActivity(report);
            }
        });

        getData();
    }

    public void getData(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading data...");
        mProgressDialog.show();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Cattle cattle1 = dataSnapshot.child("init-data").getValue(Cattle.class);
                final int i = cattle1.updateCount;
                cattleSpesView.setText(cattle1.spes);
                cattleDDView.setText(String.valueOf(getDayCount(cattle1.timestamp)));
                cattleIDView.setText(cattle1.id);
                Cattle cattle = dataSnapshot.child("update-data-"+i).getValue(Cattle.class);
                // [START_EXCLUDE]
                cattleBWView.setText(String.valueOf(cattle.bw));
                cattleBHView.setText(String.valueOf(cattle.bh));
                cattleBCView.setText(String.valueOf(cattle.bc));
                cattleAgeView.setText(String.valueOf(cattle.age));
                cattleColorView.setText(cattle.color);
                Picasso.with(getApplicationContext()).load(cattle.image).into(cattlePict);
                // [END_EXCLUDE]
                mProgressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(CattleDetailActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        // Remove post value event listener
        if (mCattleListener != null) {
            mDatabase.removeEventListener(mCattleListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                getData();
            }
        }
    }
    public int getDayCount(long timeStamp)
    {
        long pDate = timeStamp;
        long cDate = System.currentTimeMillis();

        // Calculate difference in milliseconds
        long diff = Math.abs(cDate - pDate);
        System.out.println("Day Count : "+ diff / (24 * 60 * 60 * 1000));
        dd = (int)(diff / (24 * 60 * 60 * 1000));
        return dd;
    }
}
