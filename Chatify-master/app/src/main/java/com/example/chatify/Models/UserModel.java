package com.example.chatify.Models;

public class UserModel {
    private String name;
    private String email;
    private String cnic;
    private String number;
    private String id;
    private String fcmToken;


    public UserModel() {

    }

    public UserModel(String name, String email, String cnic, String number, String id, String fcmToken) {
        this.name = name;
        this.email = email;
        this.cnic = cnic;
        this.number = number;
        this.id = id;
        this.fcmToken = fcmToken;
    }


    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getName() {
        return name;
    }
    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void setId(String id) {
        this.id = id;
    }

}