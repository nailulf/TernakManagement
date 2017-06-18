package com.nafaexample.ternakmanagement.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Nailul on 4/21/2017.
 */

public class FirebaseUtils {

    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public static String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    public static DatabaseReference getCattleRef(){
        return FirebaseDatabase.getInstance()
                .getReference(Constant.POST_CATTLES);
    }
    public static DatabaseReference getUserCattleRef(){
        return FirebaseDatabase.getInstance()
                .getReference(Constant.POST_USER_CATTLES);
    }
    public static DatabaseReference getCurrentUserCattleRef(){
        return FirebaseDatabase.getInstance()
                .getReference(Constant.POST_USER_CATTLES)
                .child(getUid());
    }
    public static DatabaseReference getVetRef(){
        return FirebaseDatabase.getInstance()
                .getReference(Constant.POST_VETERINERS);
    }
    public static DatabaseReference getUserRef(){
        return FirebaseDatabase.getInstance()
                .getReference(Constant.POST_USERS);
    }
    public static Query getCattleQuery() {
        return FirebaseUtils.getUserCattleRef()
                .child(getUid());
    }
    public static Query getVetQuery() {
        return FirebaseUtils.getVetRef();
    }
    public static StorageReference getImageCattleRef() {
        return FirebaseStorage.getInstance().getReference(Constant.POST_IMAGES_CATTLE);
    }
    public static StorageReference getImageUserRef() {
        return FirebaseStorage.getInstance().getReference(Constant.POST_IMAGES_USER);
    }
}