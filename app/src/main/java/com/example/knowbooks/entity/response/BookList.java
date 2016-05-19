package com.example.knowbooks.entity.response;

import java.util.List;

/**
 * Created by qq on 2016/4/20.
 */
public class BookList {

    private int peopleCount;
    private int bookCount;
    private int isCollect;
    private Long id;
    private String bookListName;
    private String booklistPicture;
    private Long createDate;
    private String createrId;

    @Override
    public String toString() {
        return "peopleCount:"+getPeopleCount()+";bookCount:"+getBookCount()+";isCollect:"+getIsCollect()+";id:"+getId()
                +";bookListName:"+getBookListName()+";image:"+getBooklistPicture()+";time:"+getCreateDate()+";createId:"+getCreaterId();
    }

    public int getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(int peopleCount) {
        this.peopleCount = peopleCount;
    }

    public int getBookCount() {
        return bookCount;
    }

    public void setBookCount(int bookCount) {
        this.bookCount = bookCount;
    }

    public int getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(int isCollect) {
        this.isCollect = isCollect;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookListName() {
        return bookListName;
    }

    public void setBookListName(String bookListName) {
        this.bookListName = bookListName;
    }

    public String getBooklistPicture() {
        return booklistPicture;
    }

    public void setBooklistPicture(String booklistPicture) {
        this.booklistPicture = booklistPicture;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public String getCreaterId() {
        return createrId;
    }

    public void setCreaterId(String createrId) {
        this.createrId = createrId;
    }
}
