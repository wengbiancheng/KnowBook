package com.example.knowbooks.entity.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by qq on 2016/4/24.
 */
public class DetailBookList implements Parcelable {


    private String bookpicture;
    private String bookName;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DetailBookList(){
        super();
    }
    protected DetailBookList(Parcel in) {
        id=in.readLong();
        bookpicture = in.readString();
        bookName = in.readString();
    }

    public static final Creator<DetailBookList> CREATOR = new Creator<DetailBookList>() {
        @Override
        public DetailBookList createFromParcel(Parcel in) {
            return new DetailBookList(in);
        }

        @Override
        public DetailBookList[] newArray(int size) {
            return new DetailBookList[size];
        }
    };

    public String getBookpicture() {
        return bookpicture;
    }

    public void setBookpicture(String bookpicture) {
        this.bookpicture = bookpicture;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookpicture);
        dest.writeString(bookName);
        dest.writeLong(id);
    }
}
