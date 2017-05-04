package com.example.rachamim.hacaktonproj.Model;

/**
 * Created by Rachamim on 5/2/17.
 */

public class User {
    private String email;
    private String phone;

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String licensePlate;
    private String password;
    private String lastUpdate;
    private boolean blocked;
    private boolean blocking;

    public String getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(String otherUserId) {
        this.otherUserId = otherUserId;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    private String otherUserId;

    public User(String mail,String phone, String licensePlate,Boolean blocked,boolean blocking,String otherUserId, String date) {
        this.email = mail;
        this.phone = phone;
        this.licensePlate = licensePlate;
        this.blocked = blocked;
        this.blocking = blocking;
        this.otherUserId = otherUserId;
        this.lastUpdate = date;
    }

    public User(String mail,String password, String lastUpdate){
        this.email = mail;
        this.password = password;
        this.lastUpdate = lastUpdate;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {

        return password;
    }

    public String getEmail() {
        return email;
    }


    public String getlastUpdate() {
        return lastUpdate;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setlastUpdate (String date) {
        this.lastUpdate = date;
    }
}
