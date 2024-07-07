package com.example.SocialNetwork.Repository;


import com.example.SocialNetwork.Domain.*;

import java.sql.*;
import java.util.*;

import static java.lang.Math.toIntExact;

public class RepositoryMessageDB {

    private String url;
    private String username;
    private String password;

//    private Validator<User> validator;

    private static final RepositoryMessageDB instance = new RepositoryMessageDB("jdbc:postgresql://localhost:5432/social_network", "postgres", "postgres");
    public static RepositoryMessageDB getInstance(){
        return instance;
    }

    private RepositoryMessageDB(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
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



    public Optional<Message> save(Message entity) {
        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement preparedStatement1 = connection.prepareStatement("INSERT INTO Messages(senderId, messageText, date, replyId) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement preparedStatement2 = connection.prepareStatement("INSERT INTO Messages(senderId, messageText, date) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement insertMap = connection.prepareStatement("INSERT INTO userMessageMapping(user_id, message_id) VALUES (?,?)")) {
            if (entity.getReplyID() != 0) {
                preparedStatement1.setLong(1, entity.getFromUser());
                preparedStatement1.setString(2, entity.getMessage());
                preparedStatement1.setTimestamp(3, entity.getDate());
                preparedStatement1.setLong(4, entity.getReplyID());

                int affectedRows = preparedStatement1.executeUpdate();

                if (affectedRows != 0) {
                    ResultSet generatedKeys = preparedStatement1.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        long generatedId = generatedKeys.getLong(1);
                        this.mapUserMessage(entity.getToUsers().get(0), generatedId);
                    }

                    return Optional.ofNullable(entity);
                } else {
                    return Optional.empty();
                }
            } else {
                preparedStatement2.setLong(1, entity.getFromUser());
                preparedStatement2.setString(2, entity.getMessage());
                preparedStatement2.setTimestamp(3, entity.getDate());

                int affectedRows = preparedStatement2.executeUpdate();

                if (affectedRows != 0) {
                    ResultSet generatedKeys = preparedStatement2.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        long generatedId = generatedKeys.getLong(1);
                        this.mapUserMessage(entity.getToUsers().get(0), generatedId);
                    }

                    return Optional.ofNullable(entity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void mapUserMessage(Long userId,Long messageId) {
        try(Connection connection= DriverManager.getConnection(this.url,this.username,this.password);
            PreparedStatement insertMap =connection.prepareStatement("INSERT INTO userMessageMapping(user_id,message_id) VALUES (?,?)");
        ){
            insertMap.setLong(1, userId);
            insertMap.setLong(2, messageId);
            insertMap.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }


    public Iterable<Message> findReceivedMessagesAll(Long receiverID, Long senderID, int offSet, int limit) {
        List<Message> messages = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(" select messageid,messagetext,date,replyid,M.senderID as SENDERID1, U.user_id as RECEIVERID1 from usermessagemapping U " +
                     " INNER JOIN Messages M on M.messageId = U.message_id " +
                     " where (user_id = ? and senderId = ?) or (user_id = ? and senderId = ?) limit ? offset ?; ");
        ) {
            statement.setLong(1, receiverID);
            statement.setLong(2, senderID);
            statement.setLong(3, senderID);
            statement.setLong(4, receiverID);
            statement.setLong(5, limit);
            statement.setLong(6, offSet);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
                Long messageID = resultSet.getLong("messageId");
                String messageText = resultSet.getString("messageText");
                Long replyId = resultSet.getLong("replyId");
                Timestamp date = resultSet.getTimestamp("date");
                Long receiver = resultSet.getLong("RECEIVERID1");
                Long sender = resultSet.getLong("SENDERID1");
                if(replyId == null) {
                    Message m = new Message(sender, new ArrayList<Long>(toIntExact(receiver)), messageText, date);
                    m.setId(messageID);
                    messages.add(m);
                }
                else  {
                    Message m = new Message(sender, new ArrayList<Long>(toIntExact(receiver)), messageText, date, replyId);
                    m.setId(messageID);
                    messages.add(m);
                }
            }
            return messages;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMessageContent(Long messageId) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from  messages " +
                    "where messageId = ?");

        ) {
            statement.setLong(1, messageId);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getString("messagetext");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Message getMessageById(Long id) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from  messages " +
                    "where messageId = ?");

        ) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long messageID = resultSet.getLong("messageId");
                String messageText = resultSet.getString("messageText");
                Long replyId = resultSet.getLong("replyId");
                Timestamp date = resultSet.getTimestamp("date");
                Long receiver = resultSet.getLong("RECEIVERID1");
                Long sender = resultSet.getLong("SENDERID1");
                return new Message(sender, new ArrayList<Long>(toIntExact(receiver)), messageText, date);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}

