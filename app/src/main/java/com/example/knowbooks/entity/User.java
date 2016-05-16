package com.example.knowbooks.entity;

/**
 * Created by qq on 2016/4/29.
 */
public class User {

    private String phoneNumber;
    private String password;
    private String userName;
    private String sex;
    private String ImageUrl;
    private String x;
    private String y;
    private String connectPhone;
    private String QQ;
    private String Weixin;

    @Override
    public String toString() {
        return "phoneNumber:"+getPhoneNumber()+";password:"+getPassword()+";userName:"+getUserName()
                +";sex:"+getSex()+";ImageUrl:"+getImageUrl()+";x:"+getX()+";y:"+getY()+";connectPhone:"+getConnectPhone()
                +";QQ:"+getQQ()+";Weixin:"+getWeixin();
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getConnectPhone() {
        return connectPhone;
    }

    public void setConnectPhone(String connectPhone) {
        this.connectPhone = connectPhone;
    }

    public String getQQ() {
        return QQ;
    }

    public void setQQ(String QQ) {
        this.QQ = QQ;
    }

    public String getWeixin() {
        return Weixin;
    }

    public void setWeixin(String weixin) {
        Weixin = weixin;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public User(String phone ,String password){
        this.phoneNumber=phone;
        this.password=password;
    }

    public User(){

    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
