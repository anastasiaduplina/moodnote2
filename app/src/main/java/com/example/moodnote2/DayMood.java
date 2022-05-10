package com.example.moodnote2;

public class DayMood {
    public String day;
    public String month;
    public String year;
    public String moodId;

    public DayMood(String day, String month, String year, String moodId) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.moodId = moodId;
    }

    public DayMood() {
    }
}
