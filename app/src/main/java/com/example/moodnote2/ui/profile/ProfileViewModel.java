package com.example.moodnote2.ui.profile;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moodnote2.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileViewModel extends ViewModel {



    private MutableLiveData<String> name;
    private MutableLiveData<String> email;
    private MutableLiveData<String> description;
    private MutableLiveData<String> uri;


    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private DatabaseReference dbr;
    private Profile pr;
    FirebaseUser cUser;

    public ProfileViewModel() {

        mAuth=FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance();
        dbr=db.getReference();
        cUser=mAuth.getCurrentUser();
        email=new MutableLiveData<>();
        name=new MutableLiveData<>();
        description=new MutableLiveData<>();
        uri=new MutableLiveData<>();
        if (cUser!=null){
            email.setValue(cUser.getEmail()+"");
        }else{
            email.setValue("something is wrong");
        }
        dbr.child("user").child(cUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pr=dataSnapshot.getValue(Profile.class);
                Log.i("tagg",String.valueOf(pr)+" ");
                name.setValue(String.valueOf(pr.name)+"");
                description.setValue(String.valueOf(pr.description)+"");
                uri.setValue(pr.uri);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("error",databaseError.getMessage()+"");
                name.setValue("something is wrong");
                description.setValue("something is wrong");

            }
        });
        //Log.i("key",pr.name+"");

    }
    public void exitbut(){
        mAuth.signOut();
        //todo
    }

    public LiveData<String> getName() {
        return name;
    }
    public LiveData<String> getEmail() {
        return email;
    }
    public LiveData<String> getDescription() {
        return description;
    }
    public LiveData<String> getUri() {
        return uri;
    }
}