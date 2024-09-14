package com.starsky.dollarwallpaper.Login_SignUp;

public class UserModelClass {
    String name, email, phone, pass;
    boolean isPremium = false;

    public UserModelClass(String name, String email, String phone, String pass, boolean isPremium) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.pass = pass;
        this.isPremium = isPremium;
    }

    public UserModelClass() {
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }
}
