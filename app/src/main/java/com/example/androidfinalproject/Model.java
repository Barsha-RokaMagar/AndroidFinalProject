//package com.example.androidfinalproject;
//
//public class Model {
//    String name, email, username, password, userType, gender;
//
//    public Model(String n, String e, String u, String p, String ut, String g){
//        name  = n;
//        email =e;
//        username = u;
//        password = p;
//        userType = ut;
//        gender = g;
//
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public String getUserType() {
//        return userType;
//    }
//
//    public void setUserType(String userType) {
//        this.userType = userType;
//    }
//
//    public String getGender() {
//        return gender;
//    }
//
//    public void setGender(String gender) {
//        this.gender = gender;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//}


package com.example.androidfinalproject;

public class Model {
    private String name, email, username, password, userType, gender, specialty;

    public Model() {
        // Default constructor required for Firebase
    }

    public Model(String name, String email, String username, String password, String userType, String gender, String specialty) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.gender = gender;
        this.specialty = specialty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
}
