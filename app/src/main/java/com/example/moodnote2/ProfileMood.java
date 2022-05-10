package com.example.moodnote2;

public class ProfileMood {
    public int moodId;
    public  String colorMood;
    public String descriptionMood;

    public ProfileMood(int moodId, String colorMood, String descriptionMood) {
        this.moodId = moodId;
        this.colorMood = colorMood;
        this.descriptionMood = descriptionMood;
    }

    public ProfileMood() {
    }
}
