package com.example.olxapp.helper;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {

    private static DatabaseReference referenciaFirebase;
    private static FirebaseAuth referenciaAuntenticacao;
    private static StorageReference referenciaStorage;

    public static String getIdUsuario(){
        FirebaseAuth auth = getFirebaseAuth();
        return auth.getCurrentUser().getUid();
    }

    // Retornar referencia do Database
    public static DatabaseReference getFirebaseDatabase(){
        if (referenciaFirebase == null){
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        }

        return referenciaFirebase;
    }

    // Retornar referencia do Auth
    public static FirebaseAuth getFirebaseAuth(){
        if (referenciaAuntenticacao == null){
            referenciaAuntenticacao = FirebaseAuth.getInstance();
        }

        return referenciaAuntenticacao;
    }

    // Retornar referencia do Storage
    public static StorageReference getFirebaseStorage(){
        if (referenciaStorage == null){
            referenciaStorage = FirebaseStorage.getInstance().getReference();
        }

        return referenciaStorage;
    }

}
