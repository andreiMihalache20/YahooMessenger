package com.example.SocialNetwork.Domain;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class User extends Entity<Long> {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private final ArrayList<User> friends;

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        friends = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void addFriend(User friend) throws IllegalArgumentException{
        if(friends.contains(friend))
            throw new IllegalArgumentException("These guys are already friends !");
        friends.add(friend);
    }

    public void removeFriend(User friend) throws IllegalArgumentException{
        if(!friends.contains(friend))
            throw new IllegalArgumentException("These guys are not friends !");
        friends.remove(friend);
    }

    public ArrayList<User> getFriends() {
        return friends;
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", friends=" + friends.stream()
                .map(friend -> "[" + friend.getFirstName() + " " + friend.getLastName() + "]")
                .collect(Collectors.joining(", ")) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }
}