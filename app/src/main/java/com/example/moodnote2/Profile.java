package com.example.moodnote2;



public class Profile {
    public String email;
    public String name;
    public String description;
    public String uri;

    public Profile(String email, String name, String description,String uri) {
        this.email = email;
        this.name = name;
        this.description = description;
        this.uri=uri;
    }

    public Profile() {
    }
}
