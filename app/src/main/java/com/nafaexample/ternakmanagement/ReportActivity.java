package com.nafaexample.ternakmanagement;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.nafaexample.ternakmanagement.models.Cattle;

import java.util.concurrent.Semaphore;

public class ReportActivity extends AppCompatActivity{
    public static String EXTRA_CATTLE_KEY ="ID Cattle";
    public static final String TAG ="Report Activity";
    private static String cattlekey;
    private int dd;
    private int update;
    private long timeStamp;
    private double age,bc,bh,bw;

    private final Handler mHandler1 = new Handler();
    private final Handler mHandler2 = new Handler();

    private TextView bcAvView, bcMaxView, bcMinView;
    private TextView bhAvView, bhMaxView, bhMinView;
    private TextView bwAvView, bwMaxView, bwMinView;

    private GraphView graphBw, graphBh, graphBc;

    private LineGraphSeries<DataPoint> bws;
    private LineGraphSeries<DataPoint> bhs;
    private LineGraphSeries<DataPoint> bcs;

    private DatabaseReference mDatabase;
    private DatabaseReference mRef;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        cattlekey = getIntent().getStringExtra(EXTRA_CATTLE_KEY);
        if (cattlekey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_TERNAK_KEY");
        }else{
            Log.d(TAG,"Success get intent extra key");
        }

        graphBw = (GraphView) findViewById(R.id.bw_graph);
        graphBh = (GraphView) findViewById(R.id.bh_graph);
        graphBc = (GraphView) findViewById(R.id.bc_graph);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("cattle").child(cattlekey).child("init-data");
        mProgressDialog = new ProgressDialog(ReportActivity.this);
        mProgressDialog.setMessage("Retrieving data ...");
        mProgressDialog.show();
        getUpdateCount();
        mHandler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "handler 1 => update : "+update);
                getData(update);
            }
        },3000);
        mHandler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "handler 2 => getData : "+ bws +" ; "+bhs+" ; "+bcs);
                drawGraph();
            }
        },6000);

    }

    public int getUpdateCount(){
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Cattle cattle = dataSnapshot.getValue(Cattle.class);
                update = cattle.updateCount;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.d(TAG, "getUpdateCount: "+update);
        return update;
    }

    public void getData(final int max){

        final LineGraphSeries<DataPoint> vbw = new LineGraphSeries<>();
        final LineGraphSeries<DataPoint> vbh = new LineGraphSeries<>();
        final LineGraphSeries<DataPoint> vbc = new LineGraphSeries<>();
        mRef = FirebaseDatabase.getInstance().getReference().child("cattle").child(cattlekey);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int i= 0;i<=max;i++) {
                    Cattle cattle = dataSnapshot.child("update-data-"+i).getValue(Cattle.class);
                    bw = cattle.bw;
                    Log.d(TAG, "onDataChange bw: " + bw);
                    bh = cattle.bh;
                    Log.d(TAG, "onDataChange bh: " + bh);
                    bc = cattle.bc;
                    Log.d(TAG, "onDataChange bc: " + bc);
                    //append data to series
                    vbw.appendData(new DataPoint(i, bw), true, max+1);
                    vbh.appendData(new DataPoint(i, bh), true, max+1);
                    vbc.appendData(new DataPoint(i, bc), true, max+1);

                    Log.d(TAG, "Success getData => vbw: "+ vbw +"vbh: "+ vbh +"vbh: "+ vbc);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        bws = vbw;
        bhs = vbh;
        bcs = vbc;
    }

    public void drawGraph(){
        Log.d(TAG, "drawGraph: start!");
        graphBw.addSeries(bws);
        graphBh.addSeries(bhs);
        graphBc.addSeries(bcs);
        bws.setColor(Color.RED);
        bws.setDrawBackground(true);
        bws.setDrawDataPoints(true);
        bws.setBackgroundColor(Color.argb(60, 100, 200, 200));

        bhs.setColor(Color.RED);
        bhs.setDrawBackground(true);
        bhs.setDrawDataPoints(true);
        bhs.setBackgroundColor(Color.argb(60, 100, 200, 200));

        bcs.setColor(Color.RED);
        bcs.setDrawBackground(true);
        bcs.setDrawDataPoints(true);
        bcs.setBackgroundColor(Color.argb(60, 100, 200, 200));

        graphBw.getViewport().setMinX(0);
        graphBh.getViewport().setMinX(0);
        graphBc.getViewport().setMinX(0);
        graphBw.getViewport().setMaxX(update);
        graphBh.getViewport().setMaxX(update);
        graphBc.getViewport().setMaxX(update);
        graphBw.getViewport().setXAxisBoundsManual(true);
        graphBh.getViewport().setXAxisBoundsManual(true);
        graphBc.getViewport().setXAxisBoundsManual(true);

        mProgressDialog.dismiss();
    }

}

