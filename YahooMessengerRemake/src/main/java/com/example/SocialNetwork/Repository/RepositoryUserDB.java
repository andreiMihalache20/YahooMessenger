package com.example.SocialNetwork.Repository;

import com.example.SocialNetwork.Domain.User;
import com.example.SocialNetwork.Domain.UserDTO;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.lang.Math.toIntExact;

public class RepositoryUserDB implements Repository<Long, User> {

    private String url;
    private String username;
    private String password;
    private static final RepositoryUserDB instance = new RepositoryUserDB("jdbc:postgresql://localhost:5432/social_network", "postgres", "postgres");

    public static RepositoryUserDB getInstance(){
        return instance;
    }

    private  RepositoryUserDB(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<User> findOne(Long longID) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from UsersSocialNetwork " +
                    "where id = ?");

        ) {
            statement.setInt(1, Math.toIntExact(longID));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                User u = new User(firstName,lastName,email,password);
                u.setId(longID);
                return Optional.ofNullable(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    public Optional<UserDTO> findOneDTO(Long longID) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from UsersSocialNetwork " +
                    "where id = ?");

        ) {
            statement.setInt(1, Math.toIntExact(longID));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                Long id = resultSet.getLong("id");
                UserDTO u = new UserDTO(firstName,lastName,email);
                u.setId(longID);
                return Optional.ofNullable(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from UsersSocialNetwork");
             ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next())
            {
                Long id= resultSet.getLong("id");
                String firstName=resultSet.getString("first_name");
                String lastName=resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                User user=new User(firstName,lastName,email,password);
                user.setId(id);
                users.add(user);
            }
            return users;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<User> save(User entity) {
        try(Connection connection= DriverManager.getConnection(this.url,this.username,this.password);
            PreparedStatement preparedStatement=connection.prepareStatement("INSERT INTO UsersSocialNetwork(first_name,last_name,email,password) VALUES (?,?,?,?)");

        ){
            preparedStatement.setString(1,entity.getFirstName());
            preparedStatement.setString(2,entity.getLastName());
            preparedStatement.setString(3,entity.getEmail());
            preparedStatement.setString(4,entity.getPassword());
            int affectedRows=preparedStatement.executeUpdate();
            return affectedRows == 0 ?  Optional.ofNullable(entity) : Optional.empty();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> update(User entity) {
        try(Connection connection= DriverManager.getConnection(this.url,this.username,this.password);
            PreparedStatement preparedStatement=connection.prepareStatement("UPDATE userssocialnetwork SET " +
                    "first_name = ?, last_name = ?," + " email = ?, password = ? WHERE id = ?");
        ){
            preparedStatement.setString(1,entity.getFirstName());
            preparedStatement.setString(2,entity.getLastName());
            preparedStatement.setString(3,entity.getEmail());
            preparedStatement.setString(4,entity.getPassword());
            preparedStatement.setInt(5, toIntExact(entity.getId()));
            int affectedRows=preparedStatement.executeUpdate();
            return affectedRows == 0 ? Optional.ofNullable(entity) : Optional.empty();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> delete(Long id) {
        Optional<User> user = this.findOne(id);
        int affectedRows = 0;
        if(user.isPresent()) {
            try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
                 PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM userssocialnetwork " +
                         "WHERE id = ?;");
            ) {
                preparedStatement.setLong(1, id);
                affectedRows = preparedStatement.executeUpdate();
                return affectedRows == 0 ? Optional.empty() : user;

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }

    public Optional<User> findAccount(String email, String password) {
        try(Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement statement = connection.prepareStatement("select * from UsersSocialNetwork " +
                    "where email = ? and password = ?");

        ) {
            statement.setString(1,email);
            statement.setString(2,password);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                User u = new User(firstName,lastName,email,password);
                return Optional.ofNullable(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    public Optional<UserDTO> findByEmail(String email) {
        try(Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement statement = connection.prepareStatement("select * from UsersSocialNetwork " +
                    "where email = ?");

        ) {
            statement.setString(1,email);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                Long id = resultSet.getLong("id");
                UserDTO u = new UserDTO(firstName,lastName,email);
                u.setId(id);
                return Optional.ofNullable(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    public Optional<User> findAccount(String email) {
        try(Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement statement = connection.prepareStatement("select * from UsersSocialNetwork " +
                    "where email = ?");

        ) {
            statement.setString(1,email);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String password = resultSet.getString("password");
                User u = new User(firstName,lastName,email,password);
                return Optional.ofNullable(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }
}
