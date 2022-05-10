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

import java.util.ArrayList;
import java.util.TreeMap;

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

                                ProfileMood prm1=new ProfileMood(1,"#FF9fd4a9","green");
                                ProfileMood prm2=new ProfileMood(2,"#FFffedb3","yellow");
                                ProfileMood prm3=new ProfileMood(3,"#FFa7bffc","blue");
                                ProfileMood prm4=new ProfileMood(4,"#FFfca4a4","red");
                                ProfileMood prm5=new ProfileMood(5,"#FFffbadf","pink");
                                Profile pr=new Profile(cUser.getEmail(),cUser.getUid(),"");
                                dbr.child("user").child(cUser.getUid()).setValue(pr);
                                dbr.child("users_mood").child(cUser.getUid()).child(prm1.moodId+"").setValue(prm1);
                                dbr.child("users_mood").child(cUser.getUid()).child(prm2.moodId+"").setValue(prm2);
                                dbr.child("users_mood").child(cUser.getUid()).child(prm3.moodId+"").setValue(prm3);
                                dbr.child("users_mood").child(cUser.getUid()).child(prm4.moodId+"").setValue(prm4);
                                dbr.child("users_mood").child(cUser.getUid()).child(prm5.moodId+"").setValue(prm5);

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
        dbr=db.getReference();
    }
}