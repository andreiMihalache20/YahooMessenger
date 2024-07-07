package com.example.SocialNetwork.Domain;

import java.sql.Timestamp;

public class Request  extends  Entity<Long>{
   private String fromEmail;
   private String toEmail;
   private String requestStatus;
   private Timestamp requestDate;

    public Request(String fromEmail, String toEmail, String status, Timestamp requestDate) {
        this.fromEmail = fromEmail;
        this.toEmail = toEmail;
        this.requestStatus = status;
        this.requestDate = requestDate;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getToEmail() {
        return toEmail;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Timestamp requestDate) {
        this.requestDate = requestDate;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }
}
