package com.example.moodnote2.ui.home;


import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.moodnote2.DayMood;
import com.example.moodnote2.FireBaseConnection;
import com.example.moodnote2.ProfileMood;
import com.example.moodnote2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;



public class CalendarViewModel extends LinearLayout {
    ImageButton NextButton, PreviousButton;
    TextView CurrentDate;
    GridView gridView;

    private FireBaseConnection fbc;
    private ArrayList<String> moods;
    private FirebaseDatabase db;
    private DatabaseReference dbr;
    private FirebaseAuth mAuth;
    private FirebaseUser cUser;

    private ArrayList<String> states;


    private static final int MAX_CALENDAR_DAYS = 42;
    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    Context context;

    SimpleDateFormat eventDateFormat = new SimpleDateFormat("dd MMMM yyyy",Locale.ENGLISH);
    SimpleDateFormat DateFormat = new SimpleDateFormat("dd", Locale.ENGLISH);
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);

    MyGridAdapter myGridAdapter;
    AlertDialog alertDialog;
    public List<Date> dates = new ArrayList<>();
    Button addNote;


    public CalendarViewModel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();

        IntializeLaoyut();
        SetUpCalendar();
        PreviousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                SetUpCalendar();

            }
        });
        NextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                SetUpCalendar();

            }
        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View addView = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_color, null);
                addNote=(Button)addView.findViewById(R.id.btnAdd);

                final String date = eventDateFormat.format(dates.get(position));
                final String day = DateFormat.format(dates.get(position));
                final String month = monthFormat.format(dates.get(position));
                final String year = yearFormat.format(dates.get(position));

                LinearLayout linear =(LinearLayout)addView.findViewById(R.id.linear);
                for (int i=0;i<moods.size();i++){
                    Button bt= new Button(getContext());
                    bt.setText("");
                    bt.setBackgroundColor(Color.parseColor(moods.get(i)));
                    bt.setId(i+1);
                    bt.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int id=bt.getId();
                            fbc.SaveMood(date,day,month,year,id+"");
                            SetUpCalendar();
                        }
                    });
                    linear.addView(bt);
                }
                addNote.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                builder.setView(addView);
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    public CalendarViewModel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
    private void init(){
        mAuth= FirebaseAuth.getInstance();
        db= FirebaseDatabase.getInstance();
        dbr=db.getReference();
        cUser=mAuth.getCurrentUser();
        states=new ArrayList<>();
        moods=new ArrayList<>();
    }



    private void IntializeLaoyut() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar_layout, this);
        NextButton = view.findViewById(R.id.nextBtn);
        PreviousButton = view.findViewById(R.id.previousBtn);
        CurrentDate = view.findViewById(R.id.current_Date);
        gridView = view.findViewById(R.id.gridview);
        fbc=new FireBaseConnection();

    }


    private void SetUpCalendar() {
          String currentDate = dateFormat.format(calendar.getTime());
          CurrentDate.setText(currentDate);
        dates.clear();
        Calendar calendar2 = (Calendar) calendar.clone();
        calendar2.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar2.get(Calendar.DAY_OF_WEEK) +5;
        Log.i("dateeee",calendar2.getTime()+"");
        if(monthBeginningCell>=7){
            monthBeginningCell -=7;
        }
        calendar2.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);
        while (dates.size() < 42)
        {
            dates.add(calendar2.getTime());
            calendar2.add(Calendar.DAY_OF_MONTH, 1);
        }
        dbr.child("moods").child(cUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                if (cUser!=null) {
                    dbr.child("users_mood").child(cUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            moods.clear();
                            for(DataSnapshot ds:dataSnapshot2.getChildren()){
                                ProfileMood prm=ds.getValue(ProfileMood.class);
                                moods.add(prm.colorMood);
                            }
                            states.clear();
                            for(DataSnapshot ds:dataSnapshot1.getChildren()){
                                DayMood dm=ds.getValue(DayMood.class);
                                states.add(dm.day+" "+dm.month+" "+dm.year+" "+dm.moodId);
                            }
                            Log.i("stata",states+"");
                            myGridAdapter = new MyGridAdapter(context, dates, calendar,  states,moods);

                            gridView.setAdapter(myGridAdapter);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public List<String> Days() {
        List<String> days = new ArrayList<>();

        return days;
    }




}
