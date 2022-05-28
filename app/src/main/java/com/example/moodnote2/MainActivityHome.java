package com.example.moodnote2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moodnote2.databinding.ActivityMain2Binding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivityHome extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMain2Binding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference dbr;
    private FirebaseUser cUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();//инициализация
        //всякая магия с фрагментами
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
//        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity2, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @Override
    protected void onStart() {
        super.onStart();
        //подставляет в выдвижную шторку имя и эл почту пользователя
        //fff();

    }

    @Override
    protected void onResume() {
        super.onResume();
        fff();
    }

    public  void fff(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        TextView text1=(TextView) headerLayout.findViewById(R.id.namecool);
        TextView text2=(TextView)headerLayout.findViewById(R.id.emailcool);
        ImageView imageView=(ImageView)headerLayout.findViewById(R.id.imageView);
        if (cUser!=null){
            dbr.child("user").child(cUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Profile pr = dataSnapshot.getValue(Profile.class);
                    if (pr.uri!=null){
                        try {
                            Picasso.get().load(pr.uri).fit().centerCrop().into(imageView);
                        }catch (Exception e){
                            //Toast.makeText(getApplicationContext(),"Что-то пошло не так",Toast.LENGTH_SHORT).show();
                        }
                    }

                    text1.setText(pr.name);
                    text2.setText(pr.email);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //Toast.makeText(getApplicationContext(),"Что-то пошло не так",Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            //Toast.makeText(getApplicationContext(),"Что-то пошло не так",Toast.LENGTH_SHORT).show();
        }
    }
    private void init(){
        mAuth=FirebaseAuth.getInstance();
        dbr= FirebaseDatabase.getInstance().getReference();
        cUser=mAuth.getCurrentUser();
    }

}