package com.example.moodnote2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase db;
    private DatabaseReference dbr;
    private FirebaseAuth mAuth;
    public EditText login;
    public EditText password;
    public Button loginButton;
    public Button registerButton;
    public String name;
    private Profile pr;
    FirebaseUser cUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        init();


    }

    @Override
    protected void onStart() {
        super.onStart();
        cUser=mAuth.getCurrentUser();

        if (cUser!=null){
            //переход в главную домашнюю активность если пользователь уже заходил
            Intent i=new Intent(MainActivity.this,MainActivity2.class);
            startActivity(i);
        }else{
            Toast.makeText(getApplicationContext(),"user null",Toast.LENGTH_SHORT).show();
        }

        registerButton.setOnClickListener(new View.OnClickListener() {// регистрация пользователя
            @Override
            public void onClick(View view) {
                String email=login.getText().toString();
                String passwordd=password.getText().toString();
                if (!email.equals("") && ! passwordd.equals("")){
                    mAuth.createUserWithEmailAndPassword(email,passwordd).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                cUser=mAuth.getCurrentUser();
                                //создание записи в бд, где хранится имя, эл почта и описание пользователя
                                Profile pr=new Profile(cUser.getEmail(),cUser.getUid(),"",new ArrayList<String>());
                                dbr.child(cUser.getUid()).setValue(pr);
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
        loginButton.setOnClickListener(new View.OnClickListener() {//логирование пользователя
            @Override
            public void onClick(View view) {
                String email=login.getText().toString();
                String passwordd=password.getText().toString();
                if (!email.equals("") && ! passwordd.equals("")){
                    mAuth.signInWithEmailAndPassword(email,passwordd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                //переход в гл дом активность если пользователь успешно зашел
                                Toast.makeText(getApplicationContext(),"user sign in",Toast.LENGTH_SHORT).show();
                                Intent i=new Intent(MainActivity.this,MainActivity2.class);
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

    public void init(){ //различная инициализация
        login=(EditText)findViewById(R.id.lEditLogin);
        password=(EditText)findViewById(R.id.lEditPassword);
        loginButton=(Button)findViewById(R.id.lButtonLogin);
        registerButton=(Button)findViewById(R.id.lButtonRegister);
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance();
        dbr=db.getReference("user");
    }
}