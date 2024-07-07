package com.example.SocialNetwork.Domain;
import java.util.Comparator;
public class MessageDateComparator implements Comparator<Message> {
    @Override
    public int compare(Message message1, Message message2) {
        return message1.getDate().compareTo(message2.getDate());
    }
}