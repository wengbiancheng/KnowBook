package com.example.knowbooks.entity.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

/**
 * Created by qq on 2016/4/22.
 */
public class DetailComment implements Parcelable{

    private Long id;
    private String commentContent;
    private int numOfLike;
    private int numOfDislike;
    private Long createDate;
    private Double commentScore;
    private String commentUser;
    private int sonCommentCount;
    private String headPicture;
    private String phoneNumber1;

    public DetailComment(){
        super();
    }


    protected DetailComment(Parcel in) {
        id=in.readLong();
        commentContent = in.readString();
        numOfLike = in.readInt();
        numOfDislike = in.readInt();
        createDate=in.readLong();
        commentScore=in.readDouble();
        commentUser = in.readString();
        sonCommentCount = in.readInt();
        headPicture = in.readString();
        phoneNumber1=in.readString();
    }

    public static final Creator<DetailComment> CREATOR = new Creator<DetailComment>() {
        @Override
        public DetailComment createFromParcel(Parcel in) {
            return new DetailComment(in);
        }

        @Override
        public DetailComment[] newArray(int size) {
            return new DetailComment[size];
        }
    };


    public String getPhoneNumber1() {
        return phoneNumber1;
    }

    public void setPhoneNumber1(String phoneNumber1) {
        this.phoneNumber1 = phoneNumber1;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public int getNumOfLike() {
        return numOfLike;
    }

    public void setNumOfLike(int numOfLike) {
        this.numOfLike = numOfLike;
    }

    public int getNumOfDislike() {
        return numOfDislike;
    }

    public void setNumOfDislike(int numOfDislike) {
        this.numOfDislike = numOfDislike;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public Double getCommentScore() {
        return commentScore;
    }

    public void setCommentScore(Double commentScore) {
        this.commentScore = commentScore;
    }

    public String getCommentUser() {
        return commentUser;
    }

    public void setCommentUser(String commentUser) {
        this.commentUser = commentUser;
    }

    public int getSonCommentCount() {
        return sonCommentCount;
    }

    public void setSonCommentCount(int sonCommentCount) {
        this.sonCommentCount = sonCommentCount;
    }

    public String getHeadPicture() {
        return headPicture;
    }

    public void setHeadPicture(String headPicture) {
        this.headPicture = headPicture;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    @Override
    public String toString() {
        return "id:"+id+";content:"+commentContent+";numOfLike:"+numOfLike+";numOfDislike:"+numOfDislike+";time:"+createDate+
                ";UserName:"+commentUser+";commentCount:"+sonCommentCount+";pictureUrl:"+headPicture+";phoneNumber:"+getPhoneNumber1();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(commentContent);
        dest.writeInt(numOfLike);
        dest.writeInt(numOfDislike);
        dest.writeLong(createDate);
        dest.writeDouble(commentScore);
        dest.writeString(commentUser);
        dest.writeInt(sonCommentCount);
        dest.writeString(headPicture);
        dest.writeString(phoneNumber1);
    }
}
