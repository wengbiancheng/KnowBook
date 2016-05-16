package com.example.knowbooks.entity.response;

/**
 * Created by qq on 2016/5/9.
 */
public class DetailWantBook {

    private WantBook wantBook=new WantBook();
    private String qq;
    private String weixin;
    private String phoneNumber;

    @Override
    public String toString() {
        return wantBook.toString()+";qq:"+getQq()+";weixin:"+getWeixin()+";phoneNumber:"+getPhoneNumber();
    }

    public WantBook getWantBook() {
        return wantBook;
    }

    public void setWantBook(WantBook wantBook) {
        this.wantBook = wantBook;
    }


    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
