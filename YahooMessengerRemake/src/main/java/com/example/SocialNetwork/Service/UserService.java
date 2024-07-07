package com.example.SocialNetwork.Service;

import com.example.SocialNetwork.Domain.User;
import com.example.SocialNetwork.Domain.UserDTO;
import com.example.SocialNetwork.Repository.*;
import java.util.*;
import java.io.IOException;

public class UserService{
    private static final UserService instance = new UserService();
    RepositoryUserDB userRepository;

    public static UserService getInstance(){
        return instance;
    }
    private UserService(){
        userRepository = RepositoryUserDB.getInstance();
    }

    public void add(String firstName, String lastName, String email, String password){
        User user = new User(firstName,lastName,email,password);
        userRepository.save(user);
    }

    public void remove(long id) throws IOException {
        userRepository.delete(id);
    }


    public Optional<User> update(User u){
        Optional<User> us =  userRepository.update(u);
        return us;
    }

    public Optional<User> findOne(Long id){
        return userRepository.findOne(id);
    }
    public Iterable<User> findAll(){
        return userRepository.findAll();
    }


    public Optional<User> findAccount(String email){
        return userRepository.findAccount(email);
    }

    public Optional<UserDTO> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
