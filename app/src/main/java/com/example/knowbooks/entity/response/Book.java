package com.example.knowbooks.entity.response;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qq on 2016/4/21.
 */
public class Book implements Parcelable {

//    private String bookName;//书名
//    private String bookAuthor;//作者
//    private String bookType;//书的类型：小说
//    private String book_userName;//发布这条消息的人的名字
//    private String book_score;//评分
//    private String book_commentCount;//书的评论个数
//    private String book_content;//书的简介

    private Double bookScore;//书的评分
    private Long id;//书的id
    private String bookAuthor;//书的作者
    private String titleImage;//书的照片的Url
    private String recommenReason;//推荐的理由
    private String bookLocation;//书跟你的距离
    private String createDate;//书创建的时间
    private String bookSummary;//书的简介
    private String bookName;//书的名字
    private int isCollect;//书是否被你收藏
    private int numOfComments;//书的评论的个数
    private String bookClass;//书的类型
    private String userName;//书的推荐人的昵称
    private String userSex;//书的推荐人的行呗

    protected Book(Parcel in) {
        bookScore = in.readDouble();
        id = in.readLong();
        bookAuthor = in.readString();
        titleImage = in.readString();
        recommenReason = in.readString();
        bookLocation = in.readString();
        createDate = in.readString();
        bookSummary = in.readString();
        bookName = in.readString();
        isCollect = in.readInt();
        numOfComments = in.readInt();
        bookClass = in.readString();
        userName = in.readString();
        userSex = in.readString();
    }
    public Book(){
        super();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public Double getBookScore() {
        return bookScore;
    }

    public void setBookScore(Double bookScore) {
        this.bookScore = bookScore;
    }

    public String getTitleImage() {
        return titleImage;
    }

    public void setTitleImage(String titleImage) {
        this.titleImage = titleImage;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecommenReason() {
        return recommenReason;
    }

    public void setRecommenReason(String recommenReason) {
        this.recommenReason = recommenReason;
    }

    public String getBookLocation() {
        return bookLocation;
    }

    public void setBookLocation(String bookLocation) {
        this.bookLocation = bookLocation;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getBookSummary() {
        return bookSummary;
    }

    public void setBookSummary(String bookSummary) {
        this.bookSummary = bookSummary;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(int isCollect) {
        this.isCollect = isCollect;
    }

    public int getNumOfComments() {
        return numOfComments;
    }

    public void setNumOfComments(int numOfComments) {
        this.numOfComments = numOfComments;
    }

    public String getBookClass() {
        return bookClass;
    }

    public void setBookClass(String bookClass) {
        this.bookClass = bookClass;
    }

    @Override
    public String toString() {
        return "bookScore:"+getBookScore()+";id:"+getId()+";bookAuthor:"+getBookAuthor()+";titleImage:"+getTitleImage()
                +";reason:"+getRecommenReason()+";location:"+getBookLocation()+";time:"+getCreateDate()+";summary:"+getBookSummary()
                +";bookName:"+getBookName()+";isCollect:"+getIsCollect()+";numOfComments:"+getNumOfComments()+";bookClass:"+getBookClass();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(bookScore);
        dest.writeLong(id);
        dest.writeString(bookAuthor);
        dest.writeString(titleImage);
        dest.writeString(recommenReason);
        dest.writeString(bookLocation);
        dest.writeString(createDate);
        dest.writeString(bookSummary);
        dest.writeString(bookName);
        dest.writeInt(isCollect);
        dest.writeInt(numOfComments);
        dest.writeString(bookClass);
        dest.writeString(userName);
        dest.writeString(userSex);
    }

}
