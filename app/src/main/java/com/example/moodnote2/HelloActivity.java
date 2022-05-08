package com.example.moodnote2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class HelloActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public EditText name;
    public EditText description;
    public Button save;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference();
        name=(EditText) findViewById(R.id.editName);
        description=(EditText) findViewById(R.id.editDescription);
        save=(Button) findViewById(R.id.buttonExit);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser cUser=mAuth.getCurrentUser();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nname=name.getText().toString();
                String ddescription=description.getText().toString();
                if (!nname.equals("") ){
                    //запись имени и описания пользователя в базу данных
                    Profile pr=new Profile(cUser.getEmail(),nname,ddescription,new ArrayList<String>());
                    databaseReference.child("user").child(cUser.getUid()).setValue(pr);
                    Intent i= new Intent(HelloActivity.this,MainActivity2.class);
                    startActivity(i);
                }else{
                    Toast.makeText(getApplicationContext(),"empty name",Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}
