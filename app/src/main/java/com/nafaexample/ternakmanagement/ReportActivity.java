package com.nafaexample.ternakmanagement;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.nafaexample.ternakmanagement.models.Cattle;
import com.squareup.picasso.Picasso;

public class ReportActivity extends AppCompatActivity{
    public static String EXTRA_CATTLE_KEY ="ID Cattle";
    public static final String TAG ="Report Activity";
    private static String cattlekey;
    private int update, dayCount;
    private long timeStamp;
    private double age,bc,bh,bw;
    private String imageUrl;
    public String[] imageResources = new String[5];

    private final Handler mHandler1 = new Handler();
    private final Handler mHandler2 = new Handler();

    private TextView bcAvView, bcMaxView, bcMinView;
    private TextView bhAvView, bhMaxView, bhMinView;
    private TextView bwAvView, bwMaxView, bwMinView;

    private GraphView graphBw, graphBh, graphBc;

    private TextView idView, ageView, dayView, colorView;

    private LineGraphSeries<DataPoint> bws;
    private LineGraphSeries<DataPoint> bhs;
    private LineGraphSeries<DataPoint> bcs;

    private DatabaseReference mDatabase;
    private DatabaseReference mRef;
    private ProgressDialog mProgressDialog;
    private View img;
    private LinearLayout imageLayout;

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

        imageLayout = (LinearLayout) findViewById(R.id._linearLayout);

        bwMaxView = (TextView) findViewById(R.id.bw_max_view);
        bwMinView =(TextView) findViewById(R.id.bw_min_view);
        bwAvView =(TextView) findViewById(R.id.bw_average_view);
        bhMaxView = (TextView) findViewById(R.id.bh_max_view);
        bhMinView =(TextView) findViewById(R.id.bh_min_view);
        bhAvView =(TextView) findViewById(R.id.bh_average_view);
        bcMaxView = (TextView) findViewById(R.id.bc_max_view);
        bcMinView =(TextView) findViewById(R.id.bc_min_view);
        bcAvView =(TextView) findViewById(R.id.bc_average_view);

        idView =(TextView) findViewById(R.id.id_report);
        ageView =(TextView) findViewById(R.id.age_report);
        dayView  =(TextView) findViewById(R.id.daycount_report);
        colorView =(TextView) findViewById(R.id.color_report);

        graphBw = (GraphView) findViewById(R.id.bw_graph);
        graphBh = (GraphView) findViewById(R.id.bh_graph);
        graphBc = (GraphView) findViewById(R.id.bc_graph);

        imageLayout.removeAllViews();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("cattle").child(cattlekey).child("init-data");
        mProgressDialog = new ProgressDialog(ReportActivity.this);
        mProgressDialog.setMessage("Retrieving data ...");
        mProgressDialog.show();
        getReportData();
        mHandler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "handler 1 => update : "+update);
                getDataGraph(update);
            }
        },3000);
        mHandler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "handler 2 => getDataGraph : "+ bws +" ; "+bhs+" ; "+bcs);
                drawGraph();
                getDataPict();
            }
        },6000);
    }
    public int getReportData(){
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Cattle cattle = dataSnapshot.getValue(Cattle.class);
                update = cattle.updateCount;
                timeStamp = cattle.timestamp;
                dayCount = getDayCount(timeStamp);
                dayView.setText(String.valueOf(dayCount));
                idView.setText(cattle.id);
                ageView.setText(String.valueOf(cattle.age));
                colorView.setText(String.valueOf(cattle.color));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.d(TAG, "getUpdateCount: "+update);
        return update;
    }
    public void getDataGraph(final int max){
        final LineGraphSeries<DataPoint> vbw = new LineGraphSeries<>();
        final LineGraphSeries<DataPoint> vbh = new LineGraphSeries<>();
        final LineGraphSeries<DataPoint> vbc = new LineGraphSeries<>();
        mRef = FirebaseDatabase.getInstance().getReference().child("cattle").child(cattlekey);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double[] tempBw = new double[max+1];
                double[] tempBh = new double[max+1];
                double[] tempBc = new double[max+1];
                imageResources = new String[max+1];
                for (int i= 0;i<=max;i++) {
                    Cattle cattle = dataSnapshot.child("update-data-"+i).getValue(Cattle.class);
                    bw = cattle.bw;
                    bh = cattle.bh;
                    bc = cattle.bc;
                    imageUrl = cattle.image;
                    Log.d(TAG, "get data bc: " + bc+"; bw: "+bw+"; bh: "+bh);
                    //append data to series
                    vbw.appendData(new DataPoint(i, bw), true, max+1);
                    vbh.appendData(new DataPoint(i, bh), true, max+1);
                    vbc.appendData(new DataPoint(i, bc), true, max+1);
                    Log.d(TAG, "Success getDataGraph => vbw: "+ vbw +"vbh: "+ vbh +"vbh: "+ vbc);
                    //append data to temp array
                    tempBw[i] = bw;
                    tempBh[i] = bh;
                    tempBc[i] = bc;
                    imageResources[i] = imageUrl;
                }
                //find maximum value
                bwMaxView.setText(String.valueOf(getMax(tempBw)));
                bhMaxView.setText(String.valueOf(getMax(tempBh)));
                bcMaxView.setText(String.valueOf(getMax(tempBc)));
                //find minimum value
                bwMinView.setText(String.valueOf(getMin(tempBw)));
                bhMinView.setText(String.valueOf(getMin(tempBh)));
                bcMinView.setText(String.valueOf(getMin(tempBc)));
                //find average value
                bwAvView.setText(String.valueOf(getAvr(tempBw)));
                bhAvView.setText(String.valueOf(getAvr(tempBh)));
                bcAvView.setText(String.valueOf(getAvr(tempBc)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        bws = vbw;
        bhs = vbh;
        bcs = vbc;
    }
    public void getDataPict(){
        for (int i = 0; i < imageResources.length; i++) {
            Log.d(TAG, "onCreate: add view img");
            Log.d(TAG, "getDataPict: "+imageResources[i]);
            img = getLayoutInflater().inflate(R.layout.pager_image_item, null);
            final ImageView imageView = (ImageView) img.findViewById(R.id.image_view);
            final TextView imageName = (TextView) img.findViewById(R.id.image_name);
            imageView.setId(i);
            imageName.setText("Image "+ (i+1));
            Picasso.with(getApplicationContext()).load(imageResources[i]).into(imageView);
            imageLayout.addView(img);
        }
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
    public double getMax(double[] arg){
        double maxVal = arg[0];
        for (int m = 0; m<= arg.length-1;m++){
            if(arg[m]>maxVal){
                maxVal=arg[m];
            }
        }
        return maxVal;
    }
    public double getMin(double[] arg){
        double minVal = arg[0];
        for (int m = 0; m<= arg.length-1;m++){
            if(minVal>arg[m]){
                minVal=arg[m];
            }
        }
        return minVal;
    }
    public double getAvr(double[] arg) {
        double avr;
        if (dayCount >= 1) {
            avr = arg[0] + arg[arg.length - 1] / dayCount;
        }else{
            avr = 0;
        }

        return avr;
    }
    public int getDayCount(long pDate){
        long cDate = System.currentTimeMillis();

        // Calculate difference in milliseconds
        long diff = Math.abs(cDate - pDate);
        System.out.println("Day Count : "+ diff / (24 * 60 * 60 * 1000));
        return (int)(diff / (24 * 60 * 60 * 1000));
    }
}


