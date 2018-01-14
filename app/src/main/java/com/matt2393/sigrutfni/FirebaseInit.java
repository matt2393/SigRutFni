package com.matt2393.sigrutfni;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by matt2393 on 09-01-18.
 * Clase para manejar peticiones a Firebase
 */

public class FirebaseInit {
    private static FirebaseDatabase firebaseDatabase;
    private static FirebaseStorage firebaseStorage;
    private static FirebaseAuth firebaseAuth;

    public static void init(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
    }
    public static void persistenciaDatos(){
        firebaseDatabase.setPersistenceEnabled(true);
    }

    public static DatabaseReference refRoot(){
        return firebaseDatabase.getReference();
    }

    public static StorageReference refStorage(String url){
        return firebaseStorage.getReferenceFromUrl(url);
    }
    public static FirebaseUser user(){
        return firebaseAuth.getCurrentUser();
    }
    public static FirebaseAuth getAuth(){
        return firebaseAuth;
    }
}
