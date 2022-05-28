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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.moodnote2.DayMood;
import com.example.moodnote2.YFireBaseConnection;
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

import yuku.ambilwarna.AmbilWarnaDialog;


public class CalendarViewModel extends LinearLayout {
    ImageButton NextButton, PreviousButton;
    TextView CurrentDate;
    GridView gridView;

    private YFireBaseConnection fbc;
    private ArrayList<Integer> moods;
    private FirebaseDatabase db;
    private DatabaseReference dbr;
    private FirebaseAuth mAuth;
    private FirebaseUser cUser;

    private ArrayList<String> states;


    private static final int MAX_CALENDAR_DAYS = 42;
    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    Context context;

    SimpleDateFormat eventDateformat = new SimpleDateFormat("dd MMMM yyyy",Locale.ENGLISH);
    SimpleDateFormat Dateformat = new SimpleDateFormat("dd", Locale.ENGLISH);
    SimpleDateFormat dateformat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    SimpleDateFormat monthformat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
    SimpleDateFormat yearformat = new SimpleDateFormat("yyyy", Locale.ENGLISH);

    MyGridAdapter myGridAdapter;
    AlertDialog alertDialog;
    public List<Date> dates = new ArrayList<>();
    Button saveNote;
    EditText textNote;
    Button addcolor;
    Button deleteColor;


    public CalendarViewModel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        IntializeLaoyut();
        Log.i("ys",year+ " "+month);
        SetUpCalendar(String.valueOf(year),getMonth(month));
        PreviousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                int year=calendar.get(Calendar.YEAR);
                int month=calendar.get(Calendar.MONTH);
                Log.i("ys",year+ " "+month);
                SetUpCalendar(String.valueOf(year),getMonth(month));

            }
        });
        NextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                int year=calendar.get(Calendar.YEAR);
                int month=calendar.get(Calendar.MONTH);
                Log.i("ys",year+ " "+month);
                SetUpCalendar(String.valueOf(year),getMonth(month));

            }
        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View addView = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_color, null);
                saveNote =(Button)addView.findViewById(R.id.btnAdd);
                textNote=(EditText) addView.findViewById(R.id.edNote);
                addcolor=(Button)addView.findViewById(R.id.addColor);
                deleteColor=(Button)addView.findViewById(R.id.deleteColor);
                Log.i("tnote",textNote.toString());

                final String date = eventDateformat.format(dates.get(position));
                final String day = Dateformat.format(dates.get(position));
                final String month = monthformat.format(dates.get(position));
                final String year = yearformat.format(dates.get(position));

                LinearLayout linearCheck =(LinearLayout)addView.findViewById(R.id.checkgroup);
                LinearLayout delAdd=(LinearLayout)addView.findViewById(R.id.delAndAd);
                RadioGroup rgb=(RadioGroup) addView.findViewById(R.id.rgroup);
                addradiobuttons(rgb);
                dbr.child("moods").child(cUser.getUid()).child(year).child(month).child(date).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DayMood db=snapshot.getValue(DayMood.class);
                        if (db !=null ){
                            textNote.setText(db.note.toString());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(),"Что-то пошло не так",Toast.LENGTH_SHORT).show();
                    }
                });
                addcolor.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AmbilWarnaDialog colorPicker=new AmbilWarnaDialog(getContext(), Color.parseColor("#FFFFFF"), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                            @Override
                            public void onCancel(AmbilWarnaDialog dialog) {
                                Toast.makeText(getContext(),"Что-то пошло не так",Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onOk(AmbilWarnaDialog dialog, int color) {
                                ProfileMood prm =new ProfileMood(color," ");
                                dbr.child("users_mood").child(cUser.getUid()).child(String.valueOf(color)).setValue(prm);
                                Toast.makeText(getContext(),"Цвет сохранен",Toast.LENGTH_SHORT).show();
                                RadioButton bt= new RadioButton(getContext());
                                bt.setText("");
                                bt.setBackgroundColor(color);
                                bt.setId(moods.size()+1);
                                bt.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                                moods.add(color);
                                addradiobuttons(rgb);
                                SetUpCalendar(year,month);

                            }
                        });
                        colorPicker.show();
                    }
                });
                deleteColor.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteColor.setEnabled(false);
                        addcolor.setEnabled(false);
                        rgb.removeAllViews();
                        ArrayList<Integer> selColors= new ArrayList<>();
                        for (int i=0;i<moods.size();i++){
                            CheckBox bt= new CheckBox(getContext());
                            bt.setText("");
                            bt.setBackgroundColor(moods.get(i));
                            bt.setId(i+1);
                            int color=moods.get(i);
                            bt.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                            bt.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (bt.isChecked()){
                                        selColors.add(color);
                                    }else {
                                        int d =selColors.indexOf(color);
                                        selColors.remove(d);
                                    }
                                }
                            });
                            linearCheck.addView(bt);
                        }
                        Button del=new Button(getContext());
                        del.setText("Удалить выбранные цвета");
                        del.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                        del.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                for (int i=0;i<selColors.size();i++){
                                    moods.remove(selColors.get(i));
                                    dbr.child("users_mood").child(cUser.getUid()).child(String.valueOf(selColors.get(i))).removeValue();
                                }
                                SetUpCalendar(year,month);
                                deleteColor.setEnabled(true);
                                addcolor.setEnabled(true);
                                linearCheck.removeAllViews();

                                addradiobuttons(rgb);
                                Toast.makeText(getContext(),"Цвета удалены",Toast.LENGTH_SHORT).show();

                            }
                        });
                        linearCheck.addView(del);
                    }
                });
                saveNote.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int i=rgb.getCheckedRadioButtonId();
                        String s="";
                        if (textNote !=null){
                            s=textNote.getText().toString();
                        }
                        Log.i("idi",i+"");
                        if (-i<-1){
                            fbc.SaveMood(date,day,month,year, String.valueOf(-i),s);
                            Toast.makeText(getContext(),"Сохранено",Toast.LENGTH_SHORT).show();
                        }else {
                            fbc.SaveMood(date,day,month,year, String.valueOf(1),s);
                            Toast.makeText(getContext(),"Сохранено",Toast.LENGTH_SHORT).show();
                        }

                        SetUpCalendar(year,month);
                        alertDialog.cancel();
                    }
                });
                builder.setView(addView);
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }
    public void addradiobuttons(RadioGroup rgb){
        rgb.removeAllViews();
        for (int i=0;i<moods.size();i++){
            RadioButton bt= new RadioButton(getContext());
            bt.setText("");
            bt.setBackgroundColor(moods.get(i));
            bt.setId(-1*moods.get(i));
            bt.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            rgb.addView(bt);
        }
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
        fbc=new YFireBaseConnection();

    }
    private  String getMonth(int m){
        String s="";
        switch (m+1){
            case 1:s="January"; break;
            case 2:s="February"; break;
            case 3:s="March"; break;
            case 4:s="April"; break;
            case 5:s="May"; break;
            case 6:s="June"; break;
            case 7:s="July"; break;
            case 8:s="August"; break;
            case 9:s="September"; break;
            case 10:s="October"; break;
            case 11:s="November"; break;
            case 12:s="December"; break;
        }
        return s;
    }


    private void SetUpCalendar(String year,String month) {
          String currentDate = dateformat.format(calendar.getTime());
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
        dbr.child("moods").child(cUser.getUid()).child(year).child(month).addValueEventListener(new ValueEventListener() {
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
                                states.add(dm.day+" "+dm.month+" "+dm.year+" "+dm.moodId+" "+dm.note);
                            }
                            Log.i("stata",states+"");
                            myGridAdapter = new MyGridAdapter(context, dates, calendar,  states,moods);

                            gridView.setAdapter(myGridAdapter);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //Toast.makeText(getContext(),"Что-то пошло не так",Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(getContext(),"Что-то пошло не так",Toast.LENGTH_SHORT).show();
            }
        });


    }



}
