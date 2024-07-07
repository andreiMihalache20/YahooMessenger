package com.example.SocialNetwork.Service;
//
//import SocialNetwork.repository.FriendshipFileRepository;

import com.example.SocialNetwork.Domain.Friendship;
import com.example.SocialNetwork.Domain.Tuple;
import com.example.SocialNetwork.Domain.User;
import com.example.SocialNetwork.Domain.UserDTO;
import com.example.SocialNetwork.Repository.RepositoryFriendshipDB;
import com.example.SocialNetwork.Repository.RepositoryUserDB;

import java.io.IOException;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FriendshipService {
    private static final FriendshipService instance = new FriendshipService();
    private RepositoryFriendshipDB repositoryFriendshipDB;
    private RepositoryUserDB repositoryUserDB;

    private FriendshipService(){
        repositoryFriendshipDB = new RepositoryFriendshipDB("jdbc:postgresql://localhost:5432/social_network", "postgres", "postgres");;
        repositoryUserDB = RepositoryUserDB.getInstance();
        uploadFriendships();
    }
    public static FriendshipService getInstance(){
        return instance;
    }
    public <E> void add(Long id1, Long id2) {
        if(repositoryUserDB.findOne(id2).isPresent() && repositoryUserDB.findOne((Long) id1).isPresent()) {
            User u1 = repositoryUserDB.findOne((Long) id1).get();
            User u2 = repositoryUserDB.findOne((Long) id2).get();
            u1.addFriend(u2);
            u2.addFriend(u1);
            Tuple<Long,Long> tuple = new Tuple<>(id1,id2);
            Friendship friendship = new Friendship(tuple);
            repositoryFriendshipDB.save(friendship);
        }
        else
            throw new IllegalArgumentException("Invalid users ID");
    }

    public void remove(long id1, long id2) throws IOException {
        if(repositoryUserDB.findOne(id2).isPresent() && repositoryUserDB.findOne(id1).isPresent()) {
            Tuple<Long,Long> tuple = new Tuple<>(id1,id2);
            repositoryFriendshipDB.delete(tuple);}
        else
            throw new IOException("Invalid users");
    }


    public Iterable<UserDTO> getFriendsDTO(long id){
        List<UserDTO> users = new ArrayList<>(); ;
        if(repositoryUserDB.findOne(id).isPresent()){
            Iterable<Friendship> friendships = repositoryFriendshipDB.getFriends(id);
            for(Friendship friendship:friendships){
                if(friendship.getId().getRight() == id){
                    users.add(repositoryUserDB.findOneDTO(friendship.getId().getLeft()).get());
                }
                if(friendship.getId().getLeft() == id){
                    users.add(repositoryUserDB.findOneDTO(friendship.getId().getRight()).get());
                }
            }
            return users;
        }
        else
            throw new IllegalArgumentException("Invalid user\n");
    }

    private void uploadFriendships(){
        Iterable<Friendship> iterator = repositoryFriendshipDB.findAll();
        iterator.forEach(f-> {
            User u1 = repositoryUserDB.findOne((Long) f.getId().getLeft()).get();
            User u2 = repositoryUserDB.findOne((Long) f.getId().getRight()).get();
            u1.addFriend(u2);
            u2.addFriend(u1);
        });
    }

    public Iterable<Friendship> findAll() {
        return repositoryFriendshipDB.findAll();
    }
}
