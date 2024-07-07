package com.example.SocialNetwork.Repository;

import com.example.SocialNetwork.Domain.Friendship;
import com.example.SocialNetwork.Domain.Tuple;
import java.io.IOException;
import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
public class    RepositoryFriendshipDB implements Repository<Tuple<Long,Long>, Friendship> {

    private String url;
    private String username;
    private String password;

    public RepositoryFriendshipDB(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<Friendship> findOne(Tuple tuple) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from friendships " +
                    "where user1_id = ? and user2_id = ?");
        ) {
            statement.setLong(1, (Long)tuple.getLeft());    // Assuming user1_id is the first value in the Tuple
            statement.setLong(2, (Long)tuple.getRight());   // Assuming user2_id is the second value in the Tuple
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long id1 = resultSet.getLong("user1_id");
                Long id2 = resultSet.getLong("user2_id");
                Timestamp time = resultSet.getTimestamp("friendship_date");
                Tuple<Long, Long> tuplu= new Tuple<>(id1,id2);
                Friendship f = new Friendship(tuplu,time);
                return Optional.ofNullable(f);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Friendship> findAll() {
        List<Friendship> friendships = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendships");
             ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next())
            {
                Long id1 = resultSet.getLong("user1_id");
                Long id2 = resultSet.getLong("user2_id");
                Timestamp time = resultSet.getTimestamp("friendship_date");
                Tuple<Long,Long> id = new Tuple<>(id1,id2);
                Friendship friendship = new Friendship(id, time);
                friendship.setId(id);
                friendships.add(friendship);
            }
            return friendships;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Friendship> save(Friendship entity) {
        try(Connection connection= DriverManager.getConnection(this.url,this.username,this.password);
            PreparedStatement preparedStatement=connection.prepareStatement("INSERT INTO friendships(user1_id, user2_id, friendship_date) VALUES (?,?,?)");

        ){
            preparedStatement.setLong(1,entity.getId().getLeft());
            preparedStatement.setLong(2,entity.getId().getRight());
            preparedStatement.setTimestamp(3,entity.getDate());
            int affectedRows=preparedStatement.executeUpdate();
            return affectedRows == 0 ?  Optional.ofNullable(entity) : Optional.empty();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> delete(Tuple tuple) throws IOException {
        Optional<Friendship> friendship = this.findOne(tuple);
        int affectedRows = 0;
        if(friendship.isPresent()) {
            try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
                 PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM friendships " +
                         "WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?);");
            ) {
                preparedStatement.setLong(1, (Long) tuple.getRight());
                preparedStatement.setLong(2, (Long) tuple.getLeft());
                preparedStatement.setLong(3, (Long) tuple.getLeft());
                preparedStatement.setLong(4, (Long) tuple.getRight());
                affectedRows = preparedStatement.executeUpdate();
                return affectedRows == 0 ? Optional.empty() : friendship;


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }

    public Iterable<Friendship> getFriends(Long id){
        List<Friendship> friendships = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from friendships " +
                    "where user1_id = ? or user2_id = ?");
        ) {
            statement.setLong(1, id);    // Assuming user1_id is the first value in the Tuple
            statement.setLong(2, id);    // Assuming user1_id is the first value in the Tuple
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                Long id1 = resultSet.getLong("user1_id");
                Long id2 = resultSet.getLong("user2_id");
                Timestamp time = resultSet.getTimestamp("friendship_date");
                Tuple<Long, Long> tuplu= new Tuple<>(id1,id2);
                Friendship f = new Friendship(tuplu,time);
                friendships.add(f);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return friendships;
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        return Optional.empty();
    }
}
