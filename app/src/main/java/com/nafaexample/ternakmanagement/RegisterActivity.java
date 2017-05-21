package com.nafaexample.ternakmanagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nafaexample.ternakmanagement.models.User;

/**
 * Created by Nailul on 4/11/2017.
 */

public class RegisterActivity extends AppCompatActivity{

    private ProgressDialog mProgressDialog;
    private static final String TAG = "Sign Up Activity";
    private static final String REQUIRE = "This field can't be empty";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    private Button signUpBtn;
    private EditText mName, mEmail, mPhone, mPassword, mRe_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        signUpBtn = (Button)findViewById(R.id.signupBtn);
        mName = (EditText)findViewById(R.id.nameTxt);
        mEmail = (EditText)findViewById(R.id.emailTxt);
        mPhone = (EditText)findViewById(R.id.phoneTxt);
        mPassword =(EditText)findViewById(R.id.passwordTxt);
        mRe_password =(EditText)findViewById(R.id.re_passwordTxt);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

    }

    private void register(){

        showProgressDialog();

        final String nameStore = mName.getText().toString().trim();
        final String emailStore = mEmail.getText().toString().trim();
        final String phoneStore = mPhone.getText().toString().trim();
        String passwordStore = mPassword.getText().toString().trim();
        String rePasswordStore = mRe_password.getText().toString().trim();
        int x = 6;
        int y = passwordStore.length();
        if (!validateForm(nameStore, emailStore, passwordStore)){
            return;
        }
        // password must more than 6 character
        if (x >= y){
            Toast.makeText(this, "password must be more than 6 characters", Toast.LENGTH_SHORT).show();
        }
        //check if password and re-type password is the same
        if(passwordStore.equals(rePasswordStore)){
            mAuth.createUserWithEmailAndPassword(emailStore, passwordStore)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            final String userId = getUid();
                            writeNewUser(nameStore, emailStore, phoneStore," ", userId);

                            hideProgressDialog();
                            Toast.makeText(RegisterActivity.this, R.string.auth_success,
                                    Toast.LENGTH_SHORT).show();

                            Intent main = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(main);
                            finish();
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, R.string.auth_failed,
                                        Toast.LENGTH_SHORT).show();
                                hideProgressDialog();
                            }

                            // ...
                        }
                    });

        }
        else {
            Toast.makeText(this, "your password is not match", Toast.LENGTH_SHORT).show();
            hideProgressDialog();
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public boolean validateForm(String name, String email, String password){
        boolean validate = true;
        if(TextUtils.isEmpty(name)){
            mName.setError(REQUIRE);
            validate = true;
        }

        if(TextUtils.isEmpty(email)){
            mEmail.setError(REQUIRE);
            validate = true;
        }
        if(TextUtils.isEmpty(password)){
            mPassword.setError(REQUIRE);
            validate = true;
        }

        return validate;
    }

    // [START basic_write]
    private void writeNewUser(String name, String email, String phone, String farm, String userId) {
        String imageUrl = "";
        User user = new User(name, email, phone, farm, userId, imageUrl, 0);

        mDatabase.child("users").child(userId).setValue(user);
    }
    // [END basic_write]

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
