package com.example.moodnote2;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.moodnote2.ui.home.CalendarViewModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

public class FireBaseConnection {
    private FirebaseDatabase db;
    private DatabaseReference dbr;
    private FirebaseAuth mAuth;
    private ProfileMood prm;
    private ArrayList<String> moods;
    private ArrayList<String> states;

    public FireBaseConnection() {

    }

    private FirebaseUser cUser;
    private DayMood dmood;
    private void init(){
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance();
        dbr=db.getReference();
        cUser=mAuth.getCurrentUser();
        moods=new ArrayList<String>();
        states=new ArrayList<String>();
    }
    public void SaveMood(String date, String day,String month,String year,String moodId ){
        init();
        dmood=new DayMood(day,month,year,moodId);
        dbr.child("moods").child(cUser.getUid()).child(date).setValue(dmood);
    }
    public ArrayList<String> GetMoods(){
        init();
        moods.clear();
        if (cUser!=null) {
            dbr.child("users_mood").child(cUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        ProfileMood prm=ds.getValue(ProfileMood.class);
                        moods.add(prm.colorMood);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        return moods;
    }
    public ArrayList<String> GetUserState(String date){
        states.clear();
        dbr.child("moods").child(cUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    DayMood dm=ds.getValue(DayMood.class);
                    states.add(dm.day+" "+dm.month+" "+dm.year+" "+dm.moodId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return states;
    }


}
