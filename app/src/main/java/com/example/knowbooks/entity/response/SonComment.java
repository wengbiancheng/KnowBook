package com.example.knowbooks.entity.response;

/**
 * Created by qq on 2016/5/6.
 */
public class SonComment {

    private Long id;
    private String sosnCommentUserPicture;
    private String sonCommentUserName;
    private Long createDate;
    private String commentContent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSosnCommentUserPicture() {
        return sosnCommentUserPicture;
    }

    public void setSosnCommentUserPicture(String sosnCommentUserPicture) {
        this.sosnCommentUserPicture = sosnCommentUserPicture;
    }

    public String getSonCommentUserName() {
        return sonCommentUserName;
    }

    public void setSonCommentUserName(String sonCommentUserName) {
        this.sonCommentUserName = sonCommentUserName;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}
