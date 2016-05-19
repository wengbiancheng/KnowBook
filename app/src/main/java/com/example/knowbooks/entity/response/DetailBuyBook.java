package com.example.knowbooks.entity.response;

/**
 * Created by qq on 2016/5/9.
 */
public class DetailBuyBook {

    private BuyBook book=new BuyBook();
    private String sellingWay;
    private String phoneNumber;
    private String qq;
    private String weixin;
    private String bookDescript;

    private String userPicture;



    @Override
    public String toString() {
        return book.toString()+",sellingWay:"+getSellingWay()+",phoneNumber:"+getPhoneNumber()+
                ",sellingWay:"+getSellingWay()+",qq:"+getQq()+",weixin:"+getWeixin()+",bookDescript:"+getBookDescript()+";userPicture:"+getUserPicture();
    }

    public String getUserPicture() {
        return userPicture;
    }

    public void setUserPicture(String userPicture) {
        this.userPicture = userPicture;
    }

    public BuyBook getBook() {
        return book;
    }

    public void setBook(BuyBook book) {
        this.book = book;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSellingWay() {
        return sellingWay;
    }

    public void setSellingWay(String sellingWay) {
        this.sellingWay = sellingWay;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getBookDescript() {
        return bookDescript;
    }

    public void setBookDescript(String bookDescript) {
        this.bookDescript = bookDescript;
    }


}
