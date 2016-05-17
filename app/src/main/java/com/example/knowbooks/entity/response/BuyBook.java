package com.example.knowbooks.entity.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by qq on 2016/4/25.
 */
public class BuyBook implements Parcelable{

    private Long id;
    private String bookPicture;
    private String bookName;
    private String BuyBookUser;
    private String bookAuthor;
    private String BuyBookUserSex;
    private String bookPrice;
    private Long createDate;
    private String bookClass;
    private String bookSituation;
    private String locationRange;

    protected BuyBook(Parcel in) {
        id=in.readLong();
        bookPicture = in.readString();
        bookName = in.readString();
        BuyBookUser = in.readString();
        bookAuthor = in.readString();
        BuyBookUserSex = in.readString();
        bookPrice = in.readString();
        createDate=in.readLong();
        bookClass = in.readString();
        bookSituation=in.readString();
        locationRange=in.readString();
    }

    public static final Creator<BuyBook> CREATOR = new Creator<BuyBook>() {
        @Override
        public BuyBook createFromParcel(Parcel in) {
            return new BuyBook(in);
        }

        @Override
        public BuyBook[] newArray(int size) {
            return new BuyBook[size];
        }
    };

    public String getBookSituation() {
        return bookSituation;
    }

    public void setBookSituation(String bookSituation) {
        this.bookSituation = bookSituation;
    }

    public String getBookClass() {
        return bookClass;
    }

    public void setBookClass(String bookClass) {
        this.bookClass = bookClass;
    }

    @Override
    public String toString() {
        return "id:"+getId()+";bookPicture:"+getBookPicture()+";bookName:"+getBookName()+";BuyBookUser:"+
                getBuyBookUser()+";bookAuthor:"+getBookAuthor()+";BuyBookUserSex:"+getBuyBookUserSex()+
                ";bookPrice:"+getBookPrice()+";createDate:"+getCreateDate()+";bookClass:"+getBookClass()
                +";bookType:"+getBookSituation()+";locationRange:"+getLocationRange();
    }

    public String getLocationRange() {
        return locationRange;
    }

    public void setLocationRange(String locationRange) {
        this.locationRange = locationRange;
    }

    public BuyBook(){
        super();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookPicture() {
        return bookPicture;
    }

    public void setBookPicture(String bookPicture) {
        this.bookPicture = bookPicture;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBuyBookUser() {
        return BuyBookUser;
    }

    public void setBuyBookUser(String buyBookUser) {
        BuyBookUser = buyBookUser;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBuyBookUserSex() {
        return BuyBookUserSex;
    }

    public void setBuyBookUserSex(String buyBookUserSex) {
        BuyBookUserSex = buyBookUserSex;
    }

    public String getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(String bookPrice) {
        this.bookPrice = bookPrice;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(bookPicture);
        dest.writeString(bookName);
        dest.writeString(BuyBookUser);
        dest.writeString(bookAuthor);
        dest.writeString(BuyBookUserSex);
        dest.writeString(bookPrice);
        dest.writeLong(createDate);
        dest.writeString(bookClass);
        dest.writeString(bookSituation);
        dest.writeString(locationRange);
    }
}
