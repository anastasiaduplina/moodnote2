package com.example.moodnote2;

public class DayMood {
    public String day;
    public String month;
    public String year;
    public String moodId;
    public String note;

    public DayMood(String day, String month, String year, String moodId,String note) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.moodId = moodId;
        this.note=note;
    }

    public DayMood() {
    }
}
