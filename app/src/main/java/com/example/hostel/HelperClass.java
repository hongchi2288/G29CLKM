package com.example.hostel;


public class HelperClass {
    String name,email,password;

    public HelperClass(String email, String name, String password) {
        this.email=email;
        this.name = name;
        this.password=password;
    }

    public HelperClass() {
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }
}