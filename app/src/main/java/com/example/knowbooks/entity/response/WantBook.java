package com.example.knowbooks.entity.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by qq on 2016/4/25.
 */
public class WantBook implements Parcelable{

    private Long id;
    private String distance;
    private String WantBookAuthor;
    private String WantBookName;
    private String WantBookPicture;
    private String wishPay;
    private Long createDate;
    private String wishContent;
    private String bookClass;
    private String UserSex;
    private String UserName;
    private String locationRange;

    @Override
    public String toString() {
        return "id:"+getId()+";distance:"+getDistance()+";Author:"+getWantBookAuthor()+";bookName:"+getWantBookName()+";image:"+getWantBookPicture()
                +";repay:"+getWishPay()+";time:"+getCreateDate()+";content:"+getWishContent()+";type:"+getBookClass()+";sex:"+getUserSex()
                +";userName:"+getUserName()+";locationRange:"+getLocationRange();
    }

    public WantBook(){
        super();
    }
    protected WantBook(Parcel in) {
        id=in.readLong();
        distance = in.readString();
        WantBookAuthor = in.readString();
        WantBookName = in.readString();
        WantBookPicture = in.readString();
        wishPay = in.readString();
        createDate=in.readLong();
        wishContent = in.readString();
        bookClass = in.readString();
        UserSex = in.readString();
        UserName = in.readString();
        locationRange=in.readString();
    }

    public static final Creator<WantBook> CREATOR = new Creator<WantBook>() {
        @Override
        public WantBook createFromParcel(Parcel in) {
            return new WantBook(in);
        }

        @Override
        public WantBook[] newArray(int size) {
            return new WantBook[size];
        }
    };

    public String getLocationRange() {
        return locationRange;
    }

    public void setLocationRange(String locationRange) {
        this.locationRange = locationRange;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getWantBookAuthor() {
        return WantBookAuthor;
    }

    public void setWantBookAuthor(String wantBookAuthor) {
        WantBookAuthor = wantBookAuthor;
    }

    public String getWantBookName() {
        return WantBookName;
    }

    public void setWantBookName(String wantBookName) {
        WantBookName = wantBookName;
    }

    public String getWantBookPicture() {
        return WantBookPicture;
    }

    public void setWantBookPicture(String wantBookPicture) {
        WantBookPicture = wantBookPicture;
    }

    public String getWishPay() {
        return wishPay;
    }

    public void setWishPay(String wishPay) {
        this.wishPay = wishPay;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public String getWishContent() {
        return wishContent;
    }

    public void setWishContent(String wishContent) {
        this.wishContent = wishContent;
    }

    public String getBookClass() {
        return bookClass;
    }

    public void setBookClass(String bookClass) {
        this.bookClass = bookClass;
    }

    public String getUserSex() {
        return UserSex;
    }

    public void setUserSex(String userSex) {
        UserSex = userSex;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(distance);
        dest.writeString(WantBookAuthor);
        dest.writeString(WantBookName);
        dest.writeString(WantBookPicture);
        dest.writeString(wishPay);
        dest.writeLong(createDate);
        dest.writeString(wishContent);
        dest.writeString(bookClass);
        dest.writeString(UserSex);
        dest.writeString(UserName);
        dest.writeString(locationRange);
    }
}
