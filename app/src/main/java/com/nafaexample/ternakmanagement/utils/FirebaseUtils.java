package com.nafaexample.ternakmanagement.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by Nailul on 4/21/2017.
 */

public interface FirebaseUtils {

    Query getQuery(DatabaseReference databaseReference);

}
