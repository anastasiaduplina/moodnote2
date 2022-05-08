package com.example.moodnote2.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GalleryViewModel extends ViewModel {


    private MutableLiveData<String> email;

    private FirebaseAuth mAuth;

    public GalleryViewModel() {
        email=new MutableLiveData<>();
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser cUser=mAuth.getCurrentUser();
        if (cUser!=null){
            email.setValue(cUser.getEmail()+"");
        }else{
            email.setValue("something is wrong");
        }

    }

    public LiveData<String> getText() {
        return email;
    }
}