package com.nafaexample.ternakmanagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.opengl.EGLSurface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nafaexample.ternakmanagement.utils.FirebaseUtils;

import java.sql.Time;

/**
 * Created by Nailul on 4/11/2017.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG ="Login Activity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Button loginBtn;
    private EditText mEmail, mPassword;
    private TextView mSignUp;
    public  double timeStart;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //setup firebase authentification
        mAuth = FirebaseAuth.getInstance();
        loginCheck();

        mEmail =(EditText)findViewById(R.id.emailTxt);
        mPassword =(EditText)findViewById(R.id.passwordTxt);
        mSignUp =(TextView)findViewById(R.id.signupTxt);

        loginBtn =(Button)findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
                timeStart = System.currentTimeMillis();
            }
        });

        mSignUp.setOnClickListener(this);
    }

    /** Do user had already logged in, check here*/
    public void loginCheck(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Intent main = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(main);
                    finish();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    //
    private void userLogin(){
        String emailStore = mEmail.getText().toString().trim();
        String passwordStore = mPassword.getText().toString().trim();

        //final ProgressDialog progressDialog = new ProgressDialog(this);
        mAuth.signInWithEmailAndPassword(emailStore,passwordStore)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //progressDialog.dismiss();
                        if (task.isSuccessful()){
                            finish();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View view) {
        Intent signup = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(signup);
    }
}
