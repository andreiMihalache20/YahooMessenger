package com.example.SocialNetwork.Service;

import com.example.SocialNetwork.Domain.Request;
import com.example.SocialNetwork.Domain.User;
import com.example.SocialNetwork.Repository.*;
import java.util.List;
import java.sql.Timestamp;
import java.util.*;
import java.io.IOException;
import java.util.Optional;
public class RequestService{
    private static final RequestService instance = new RequestService();
    RepositoryRequestDB requestRepository;

    public static RequestService getInstance(){
        return instance;
    }
    private RequestService(){
        requestRepository = RepositoryRequestDB.getInstance();
    }

    public void add(String fromEmail, String toEmail, String status, Timestamp date) throws Exception {
        Optional<Request> r = requestRepository.findOne(fromEmail,toEmail);
        if(r.isPresent() && (r.get().getRequestStatus().equals("pending") || r.get().getRequestStatus().equals("accepted") ))
            throw new Exception("You have already added this user!");
        Request request = new Request(fromEmail,toEmail,status,date);
        requestRepository.save(request);
    }

    public Optional<Request> update(Request r){
        Optional<Request> request =  requestRepository.update(r);
        return request;
    }

    public Iterable<Request> findRequests(String email) {
        Iterable<Request> requests = requestRepository.findAll();
        List<Request> filteredRequests = new ArrayList<>();

        for (Request request : requests) {
            if (request.getToEmail().equals(email) && request.getRequestStatus().equals("pending")) {
                filteredRequests.add(request);
            }
        }
        return filteredRequests;
    }

    public Optional<Request> findRequest(String fromEmail,String toEmail){
        return requestRepository.findOne(fromEmail,toEmail);
    }
}
