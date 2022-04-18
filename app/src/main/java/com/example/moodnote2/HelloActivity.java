package com.example.moodnote2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HelloActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public TextView text;
    public Button exit;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        text=(TextView)findViewById(R.id.textView2);
        exit=(Button)findViewById(R.id.buttonExit);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser cUser=mAuth.getCurrentUser();
        if (cUser!=null){
            text.setText("Hello "+cUser.getEmail());
        }else{
            Toast.makeText(getApplicationContext(),"user null",Toast.LENGTH_SHORT).show();
        }
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent i=new Intent(HelloActivity.this,MainActivity.class);
                startActivity(i);
            }
        });

    }
}
