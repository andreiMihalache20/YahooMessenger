package com.example.SocialNetwork.Domain;

import java.sql.Timestamp;
import java.util.List;

public class Message extends Entity<Long>{
    private Long fromUser;
    private List<Long> toUsers;
    private String message;
    private Timestamp date;
    private long replyID;

    public Message(Long fromUser, List<Long> toUsers, String message, Timestamp date) {
        this.fromUser = fromUser;
        this.toUsers = toUsers;
        this.message = message;
        this.date = date;
        this.replyID = 0;
    }

    public Message(Long fromUser, List<Long> toUsers, String message, Timestamp date, long id) {
        this.fromUser = fromUser;
        this.toUsers = toUsers;
        this.message = message;
        this.date = date;
        this.replyID = id;
    }

    public Long getFromUser() {
        return fromUser;
    }

    public void setFromUser(Long fromUser) {
        this.fromUser = fromUser;
    }

    public List<Long> getToUsers() {
        return toUsers;
    }

    public void setToUsers(List<Long> toUsers) {
        this.toUsers = toUsers;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public long getReplyID() {
        return replyID;
    }

    public void setReplyID(long replyID) {
        this.replyID = replyID;
    }
}
