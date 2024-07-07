package com.example.SocialNetwork.Service;

import com.example.SocialNetwork.Domain.Message;
import com.example.SocialNetwork.Repository.RepositoryMessageDB;
import com.example.SocialNetwork.utils.Observable;
import com.example.SocialNetwork.utils.Observer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageService implements Observable<Message> {
    private static final MessageService instance = new MessageService();
    private final List<Observer<Message>> observers=new ArrayList<>();
    private final RepositoryMessageDB repositoryMessageDB = RepositoryMessageDB.getInstance();
    private MessageService(){
    }

    public static MessageService getInstance(){
        return instance;
    }
    @Override
    public void addObserver(Observer<Message> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<Message> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(Message t) {
        observers.forEach(x->x.update(t));
    }

    public Message sendMessage(Long senderID,Long receiverID, String text,Timestamp time){
        Message message = new Message(senderID,new ArrayList<Long>(Collections.singleton(receiverID)), text,time);
        repositoryMessageDB.save(message);
        notifyObservers(message);
        return message;
    }
    public Message sendMessage(Long senderID,Long receiverID, String text,Timestamp time,Long replyId){
        Message message = new Message(senderID,new ArrayList<Long>(Collections.singleton(receiverID)), text,time,replyId);
        repositoryMessageDB.save(message);
        notifyObservers(message);
        return message;
    }

    public Iterable<Message> getMessages(Long loggedUserId, Long friendId, int offSet, int limit) {
        List<Message> allMessages =(List<Message>) repositoryMessageDB.findReceivedMessagesAll(loggedUserId, friendId, offSet, limit); //(List<Message>) repositoryMessageDB.findReceivedMessages(loggedUserId,friendId);
        return allMessages;
    }

    public String getMessageContent(Long messageId){
        return repositoryMessageDB.getMessageContent(messageId);
    }

    public Message getMessageById(Long id) {
        return repositoryMessageDB.getMessageById(id);
    }
}

