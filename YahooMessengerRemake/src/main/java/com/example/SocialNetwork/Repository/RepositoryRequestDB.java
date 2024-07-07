package com.example.SocialNetwork.Repository;

import com.example.SocialNetwork.Domain.Friendship;
import com.example.SocialNetwork.Domain.Request;
import com.example.SocialNetwork.Domain.Tuple;
import com.example.SocialNetwork.Domain.User;

import java.io.IOException;
import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import static java.lang.Math.toIntExact;

public class RepositoryRequestDB {

    private String url;
    private String username;
    private String password;

//    private Validator<User> validator;

    private static final RepositoryRequestDB instance = new RepositoryRequestDB("jdbc:postgresql://localhost:5432/social_network", "postgres", "postgres");
    public static RepositoryRequestDB getInstance(){
        return instance;
    }

    private RepositoryRequestDB(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Optional<Request> findOne(String fromEmail, String toEmail) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from friendrequests " +
                    "where fromEmail = ? and toEmail = ? and (requestStatus = ? or requestStatus = ?)");
        ) {
            statement.setString(1, fromEmail);    // Assuming user1_id is the first value in the Tuple
            statement.setString(2, toEmail);    // Assuming user1_id is the first value in the Tuple
            statement.setString(3, "pending");    // Assuming user1_id is the first value in the Tuple
            statement.setString(4, "accepted");    // Assuming user1_id is the first value in the Tuple
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long requestID = resultSet.getLong("requestID");
                String fromEmailDB = resultSet.getString("fromEmail");
                String toEmailDB = resultSet.getString("toEmail");
                String requestStatus = resultSet.getString("requestStatus");
                Timestamp requestDate  = resultSet.getTimestamp("requestDate");
                Request r = new Request(fromEmailDB,toEmailDB,requestStatus,requestDate);
                r.setId(requestID);
                return Optional.ofNullable(r);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    public Iterable<Request> findAll() {
        List<Request> requests = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendrequests");
             ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next())
            {
                Long id = resultSet.getLong("requestID");
                String fromEmail = resultSet.getString("fromEmail");
                String toEmail = resultSet.getString("toEmail");
                String requestStatus = resultSet.getString("requestStatus");
                Timestamp requestDate= resultSet.getTimestamp("requestDate");
                Request r = new Request(fromEmail,toEmail,requestStatus,requestDate);
                r.setId(id);
                requests.add(r);
            }
            return requests;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public Optional<Request> update(Request entity) {
        try(Connection connection= DriverManager.getConnection(this.url,this.username,this.password);
            PreparedStatement preparedStatement=connection.prepareStatement("UPDATE friendrequests SET " +
                    " requestStatus = ? WHERE requestID = ?");
        ){
            preparedStatement.setString(1,entity.getRequestStatus());
            preparedStatement.setLong(2,entity.getId());
            int affectedRows=preparedStatement.executeUpdate();
            return affectedRows == 0 ?  Optional.empty() : Optional.of(entity);
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Optional<Request> save(Request entity) {
        try(Connection connection= DriverManager.getConnection(this.url,this.username,this.password);
            PreparedStatement preparedStatement=connection.prepareStatement("INSERT INTO friendrequests(fromEmail, toEmail, requestStatus, requestDate) VALUES (?,?,?,?)");
        ){
            preparedStatement.setString(1,entity.getFromEmail());
            preparedStatement.setString(2,entity.getToEmail());
            preparedStatement.setString(3,entity.getRequestStatus());
            preparedStatement.setTimestamp(4,entity.getRequestDate());
            int affectedRows=preparedStatement.executeUpdate();
            return affectedRows == 0 ?  Optional.ofNullable(entity) : Optional.empty();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Optional<Request> delete(Long id) throws IOException {
        int affectedRows = 0;
            try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
                 PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM friendrequests " +
                         "WHERE requestID = ?");
            ) {
                preparedStatement.setLong(1, id);
                affectedRows = preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        return Optional.empty();
    }
}
