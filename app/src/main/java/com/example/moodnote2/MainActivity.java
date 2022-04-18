package com.example.moodnote2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase db;
    DatabaseReference dbr;
    private FirebaseAuth mAuth;
    public EditText login;
    public EditText password;
    public Button loginButton;
    public Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        db=FirebaseDatabase.getInstance();
        dbr=db.getReference("user");
        init();


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser cUser=mAuth.getCurrentUser();
        if (cUser!=null){
            Toast.makeText(getApplicationContext(),cUser.getEmail(),Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"user null",Toast.LENGTH_SHORT).show();
        }
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=login.getText().toString();
                String passwordd=password.getText().toString();
                if (!email.equals("") && ! passwordd.equals("")){
                    mAuth.createUserWithEmailAndPassword(email,passwordd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"create user",Toast.LENGTH_SHORT).show();
                                Intent i=new Intent(MainActivity.this,HelloActivity.class);
                                startActivity(i);
                            }else{
                                Toast.makeText(getApplicationContext(),"failed to create user",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(),"empty fields",Toast.LENGTH_SHORT).show();
                }
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=login.getText().toString();
                String passwordd=password.getText().toString();
                if (!email.equals("") && ! passwordd.equals("")){
                    mAuth.signInWithEmailAndPassword(email,passwordd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"user sign in",Toast.LENGTH_SHORT).show();
                                Intent i=new Intent(MainActivity.this,HelloActivity.class);
                                startActivity(i);
                            }else{
                                Toast.makeText(getApplicationContext(),"failed user sign in",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(),"empty fields",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void init(){
        login=(EditText)findViewById(R.id.lEditLogin);
        password=(EditText)findViewById(R.id.lEditPassword);
        loginButton=(Button)findViewById(R.id.lButtonLogin);
        registerButton=(Button)findViewById(R.id.lButtonRegister);
        mAuth=FirebaseAuth.getInstance();
    }
}