package com.abu.users.Model;

public class ItemModel {
    private String name;
    private String phone;
    private int gender;
    private String address;

    public ItemModel() {
    }

    public ItemModel(String name, String phone, int gender, String address) {
        this.name = name;
        this.phone = phone;
        this.gender = gender;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
