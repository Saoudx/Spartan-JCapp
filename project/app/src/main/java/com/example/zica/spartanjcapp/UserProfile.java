package com.example.zica.spartanjcapp;

public class UserProfile {
    public String userAge;
    public String userEmail;
    public String userName;
    public String userPhone;

    public UserProfile(String userAge,String userEmail,String userName, String userPhone ){
        this.userAge = userAge;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userPhone = userPhone;
    }

    public UserProfile(){

    }

    public String getUserAge(){
        return userAge;
    }
    public void setUserAge(String userAge){
        this.userAge = userAge;
    }

    public String getUserEmail(){
        return userEmail;
    }
    public void setUserEmail(String userEmail){
        this.userEmail = userEmail;
    }

    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getUserPhone(){
        return userPhone;
    }
    public void setUserPhone(String userPhone){
        this.userPhone = userPhone;
    }
}
