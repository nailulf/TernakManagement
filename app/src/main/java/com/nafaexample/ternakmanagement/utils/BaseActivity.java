package com.nafaexample.ternakmanagement.utils;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.nafaexample.ternakmanagement.R;

/**
 * Created by Nailul on 7/5/2017.
 */

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    static TextView timerTxt;

    Handler customHandler = new Handler();
    long startTime=0L, timeInMiliseconds=0L;
    Runnable updateTimeThread =  new Runnable() {
        @Override
        public void run() {
            timeInMiliseconds = System.currentTimeMillis()-startTime;
            int secs = (int)(timeInMiliseconds/1000);
            int mins = secs/60;
            int miliseconds = (int) (timeInMiliseconds%1000);
            timerTxt.setText(""+mins+":"+String.format("%2d",secs)+":"
                    +String.format("%3d", miliseconds));
            Time.time = (String) timerTxt.getText();
            //Log.d("Start", "run: "+Time.time);
            customHandler.postDelayed(this, 0);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timerTxt = new TextView(this);
    }

    protected void startTimer(){
        startTime = System.currentTimeMillis();
        customHandler.postDelayed(updateTimeThread, 0);
    }
    protected void stopTimer(){
        customHandler.removeCallbacks(updateTimeThread);
    }

    protected void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    protected void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
