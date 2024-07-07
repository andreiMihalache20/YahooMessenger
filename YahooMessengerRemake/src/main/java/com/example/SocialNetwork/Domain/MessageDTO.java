package com.example.SocialNetwork.Domain;

import java.sql.Timestamp;

public class MessageDTO extends Entity<Long>{
    private long replyID;
    private String senderEmail;
    private String receiverEmail;
    private String messageText;
    private Timestamp date;

    public MessageDTO(Long messageId, Long replyID, String senderEmail, String receiverEmail, String messageText, Timestamp date) {
        this.setId(messageId);
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.messageText = messageText;
        this.date = date;
        this.replyID = replyID;
    }

    public MessageDTO(long replyID, String senderEmail, String receiverEmail, String messageText, Timestamp date) {
        this.replyID = replyID;
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.messageText = messageText;
        this.date = date;
    }

    public long getReplyID() {
        return replyID;
    }

    public void setReplyID(long replyID) {
        this.replyID = replyID;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
