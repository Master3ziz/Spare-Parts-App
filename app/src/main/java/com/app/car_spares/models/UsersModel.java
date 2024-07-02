package com.app.car_spares.models;

public class UsersModel {
    private String id;
    private String name;
    private String email;
    private String password;
    private String account;

    public UsersModel() { }

    public UsersModel(String id, String name, String email, String password,String account) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.account = account;
    }


    public String  getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getAccount() {
        return account;
    }
}
