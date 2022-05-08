package com.example.moodnote2;

import java.util.ArrayList;

public class Profile {
    public String email;
    public String name;
    public String description;
    public ArrayList<String> moods;

    public Profile(String email, String name, String description,ArrayList<String> moods) {
        this.email = email;
        this.name = name;
        this.description = description;
        this.moods=moods;
    }

    public Profile() {
    }
}
