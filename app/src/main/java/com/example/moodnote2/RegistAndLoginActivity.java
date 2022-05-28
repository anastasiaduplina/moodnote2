package com.example.moodnote2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
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

public class RegistAndLoginActivity extends AppCompatActivity {
    private FirebaseDatabase db;
    private DatabaseReference dbr;
    private FirebaseAuth mAuth;
    public EditText login;
    public EditText password;
    public Button loginButton;
    public Button registerButton;
    public String name;
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
            Intent i=new Intent(RegistAndLoginActivity.this, MainActivityHome.class);
            startActivity(i);
        }else{
            //Toast.makeText(getApplicationContext(),"user null",Toast.LENGTH_SHORT).show();
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

                                ProfileMood prm1=new ProfileMood( Color.parseColor("#FF9fd4a9"),"green");
                                ProfileMood prm2=new ProfileMood(Color.parseColor("#FFffedb3"),"yellow");
                                ProfileMood prm3=new ProfileMood(Color.parseColor("#FFa7bffc"),"blue");
                                ProfileMood prm4=new ProfileMood(Color.parseColor("#FFfca4a4"),"red");
                                ProfileMood prm5=new ProfileMood(Color.parseColor("#FFffbadf"),"pink");
                                Profile pr=new Profile(cUser.getEmail(),cUser.getUid(),"","");
                                dbr.child("user").child(cUser.getUid()).setValue(pr);
                                dbr.child("users_mood").child(cUser.getUid()).child(prm1.colorMood+"").setValue(prm1);
                                dbr.child("users_mood").child(cUser.getUid()).child(prm2.colorMood+"").setValue(prm2);
                                dbr.child("users_mood").child(cUser.getUid()).child(prm3.colorMood+"").setValue(prm3);
                                dbr.child("users_mood").child(cUser.getUid()).child(prm4.colorMood+"").setValue(prm4);
                                dbr.child("users_mood").child(cUser.getUid()).child(prm5.colorMood+"").setValue(prm5);

                                Toast.makeText(getApplicationContext(),"Пользователь зарегистрировался",Toast.LENGTH_SHORT).show();
                                Intent i=new Intent(RegistAndLoginActivity.this, AddInformationActivity.class);
                                startActivity(i);
                            }else{
                                //Toast.makeText(getApplicationContext(),"Что-то пошло не так",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    //Toast.makeText(getApplicationContext(),"Заполните поля",Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getApplicationContext(),"Пользователь вошел",Toast.LENGTH_SHORT).show();
                                Intent i=new Intent(RegistAndLoginActivity.this, MainActivityHome.class);
                                startActivity(i);
                            }else{
                                //Toast.makeText(getApplicationContext(),"Что-то пошло не так",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(),"Заполните поля",Toast.LENGTH_SHORT).show();
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