package com.example.moodnote2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class AddInformationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public EditText name;
    public EditText description;
    public Button save;
    public Button addImage;
    public ImageView imageAv;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser cUser;
    private Uri uploadUri;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        cUser=mAuth.getCurrentUser();
        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference();
        storageReference= FirebaseStorage.getInstance().getReference();
        name=(EditText) findViewById(R.id.editName);
        description=(EditText) findViewById(R.id.editDescription);
        save=(Button) findViewById(R.id.buttonExit);
        addImage=(Button)findViewById(R.id.chooseImage);
        imageAv=(ImageView)findViewById(R.id.imageAv);
        setInformation();

    }
    private void setInformation(){
        databaseReference.child("user").child(cUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Profile pr=snapshot.getValue(Profile.class);
                name.setText(pr.name);
                description.setText(pr.description);
                try {
                    Picasso.get().load(pr.uri).into(imageAv);
                }catch (Exception e){
                    //Toast.makeText(getApplicationContext(),"Что-то пошло не так",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(getApplicationContext(),"Что-то пошло не так",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private  void saveUser(){
        String nname=name.getText().toString();
        String ddescription=description.getText().toString();
        if (!nname.equals("") ){
            //запись имени и описания пользователя в базу данных
            Profile pr=new Profile(cUser.getEmail(),nname,ddescription,uploadUri.toString());
            databaseReference.child("user").child(cUser.getUid()).setValue(pr).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    Toast.makeText(getApplicationContext(),"Сохранено",Toast.LENGTH_SHORT).show();
                    Intent i= new Intent(AddInformationActivity.this, MainActivityHome.class);
                    startActivity(i);
                }
            });

        }else{
            Toast.makeText(getApplicationContext(),"Заполните поле Имя",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser cUser=mAuth.getCurrentUser();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && data !=null && data.getData()!= null){
            if (resultCode==RESULT_OK){
                 Log.i("dataa",data.getData()+"");
                 imageAv.setImageURI(data.getData());

            }else {
                imageAv.setImageResource(R.mipmap.camera);

            }

        }
    }
    private void uploadImage( ){
        Bitmap bitmap=((BitmapDrawable)imageAv.getDrawable()).getBitmap();
        Log.i("bytes",bitmap.getByteCount()+"");
        if (bitmap.getByteCount()<10000000){

            ByteArrayOutputStream baos= new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byte[] bytes=baos.toByteArray();
            final StorageReference mrf=storageReference.child("image").child(cUser.getUid());
            UploadTask uploadTask= mrf.putBytes(bytes);
            Task<Uri> taskup=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return mrf.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    uploadUri=task.getResult();
                    //Toast.makeText(getApplicationContext(),"Картинка загружена",Toast.LENGTH_SHORT).show();
                    saveUser();
                }
            });
        }else {
            Toast.makeText(getApplicationContext(),"Слишком большая картинка",Toast.LENGTH_SHORT).show();
        }


    }

    private void getImage(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }
}
