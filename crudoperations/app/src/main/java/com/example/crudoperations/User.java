package com.example.crudoperations;



public class User {
    private String name;
    private String email;
    private int age;

    // Default constructor (required by Firestore)
    public User() { }

    public User(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }
}
