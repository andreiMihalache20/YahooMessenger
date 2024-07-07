package com.example.SocialNetwork;

import com.example.SocialNetwork.Controller.MessageAlert;
import com.example.SocialNetwork.Domain.*;
import com.example.SocialNetwork.Service.FriendshipService;
import com.example.SocialNetwork.Service.MessageService;
import com.example.SocialNetwork.Service.UserService;
import com.example.SocialNetwork.utils.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ScrollEvent;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.ArrayList;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class HomeController implements Observer<Message>{
    private UserService userService = UserService.getInstance();
    private FriendshipService friendshipService = FriendshipService.getInstance();
    private MessageService messageService = MessageService.getInstance();
    @FXML
    private TableColumn mailFriendCol;
    @FXML
    private Label loggedInUserNameLabel;
    User loggedUser;
    UserDTO selectedFriend;
    private Message selectedMessage;
    @FXML
    private TextField messageTextField;
    @FXML
    private TextField limitTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TableView<UserDTO> friendsTableView;
    @FXML
    private TableView<MessageDTO> messageTableView;
    @FXML
    private TableColumn<Message,String> messageEmailCol;
    @FXML
    private TableColumn<Message, String> messageTextCol;
    @FXML
    private TableColumn<Message, Timestamp> messageDateCol;

    private int offSet = 0;
    private int limit = 15;
    public void initialize(){
        messageEmailCol.setCellValueFactory(new PropertyValueFactory<>("senderEmail"));
        messageTextCol.setCellValueFactory(new PropertyValueFactory<>("messageText"));
        messageDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        mailFriendCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
    }
    private void initTable(){

        try {
            Optional<UserDTO> user = userService.findByEmail(loggedUser.getEmail());
            Iterable<UserDTO> friendsIterable = friendshipService.getFriendsDTO(user.get().getId());
            List<UserDTO> requestsList = StreamSupport.stream(friendsIterable.spliterator(), false)
                    .collect(Collectors.toList());
            ObservableList<UserDTO> friends = FXCollections.observableArrayList(requestsList);
            friendsTableView.setItems(friends);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void loadDataFriends() {
        try {
            Optional<UserDTO> user = userService.findByEmail(loggedUser.getEmail());
            Iterable<UserDTO> friendsIterable = friendshipService.getFriendsDTO(user.get().getId());
            List<UserDTO> requestsList = StreamSupport.stream(friendsIterable.spliterator(), false)
                    .collect(Collectors.toList());

            ObservableList<UserDTO> friends = FXCollections.observableArrayList(requestsList);
            friendsTableView.setItems(friends);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    public void setLoggedUser(User user){
        this.loggedUser = user;
        loggedInUserNameLabel.setText("Welcome, " + loggedUser.getFirstName());
        initTable();
    }
    @FXML
    private void showFriends(ActionEvent event) throws IOException {
        loadDataFriends();
    }
    @FXML
    private Tab friendRequestsTab;

    public void setLoggedInUserForFriendRequests(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("friendrequests-table.fxml"));
        Parent root = loader.load();

        FriendrequestsController friendrequestsController = loader.getController();
        friendrequestsController.setLoggedUser(user);
        friendRequestsTab.setContent(root);
    }
    /************************************* MESSAGE   LOGIC  *********************************/
    @Override
    public void update(Message message) {
        loadMessages();
    }

    public void handleSelectUser(){
        selectedFriend =  friendsTableView.getSelectionModel().getSelectedItem();
        emailTextField.setText(selectedFriend.getLastName());
        loadMessages();
    }

    public void handleSelectMessage() {
        MessageDTO selectedMessageDTO = messageTableView.getSelectionModel().getSelectedItem();
        if (selectedMessageDTO != null) {
            selectedMessage = messageService.getMessageById(selectedMessageDTO.getId());
        }
        }


    private void loadMessages() {
        try {
            Optional<UserDTO> userSender = userService.findByEmail(loggedUser.getEmail());
            Iterable<Message> messageIterable = messageService.getMessages(userSender.get().getId(), selectedFriend.getId(), offSet, limit);
            List<MessageDTO> messageDTOList = this.convertToMessageDto(messageIterable);

            for(MessageDTO m : messageDTOList){
                if(m.getReplyID() != 0){
                    m.setMessageText("Reply " + messageService.getMessageContent(m.getReplyID()) + " :" + m.getMessageText());
                }
            }
            List<MessageDTO> messagesList = StreamSupport.stream(messageDTOList.spliterator(), false)
                    .collect(Collectors.toList());

            ObservableList<MessageDTO> messages = FXCollections.observableArrayList(messagesList);
            messageTableView.setItems(messages);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private List<MessageDTO> convertToMessageDto(Iterable<Message> messageIterable) {
        List<MessageDTO> messageDTOList = new ArrayList<>();
        for (Message message : messageIterable) {
            String senderEmail = userService.findOne(message.getFromUser()).get().getEmail();
            MessageDTO messageDTO = new MessageDTO(
                    message.getId(),
                    message.getReplyID(),
                    senderEmail,
                    message.getToUsers().toString(),
                    message.getMessage(),
                    message.getDate()
            );

            messageDTOList.add(messageDTO);
        }
        return messageDTOList;
    }

    @FXML
    private void sendMessage(ActionEvent event) {
        String email = emailTextField.getText();
        String message = messageTextField.getText();
        if(message.isEmpty()|| email.isEmpty()){
            MessageAlert.showErrorMessage(null, "Please fill in all the text fields in order to send a message!");
            return;
        }
        if(selectedMessage == null) { // case where the message is not a reply to anything
            String[] parts = email.split("\\|");
            if(parts.length == 1) {  // only one selected email
                Optional<UserDTO> user = userService.findByEmail(email);
                Optional<UserDTO> userSender = userService.findByEmail(loggedUser.getEmail());
                Timestamp time = getCurrentTime();
                messageService.sendMessage(userSender.get().getId(), user.get().getId(), message, time);
            }  // the selected emails ( you can send a message to multiple emails )
            else{
                for(String part:parts) {
                    Optional<UserDTO> user = userService.findByEmail(part);
                    Optional<UserDTO> userSender = userService.findByEmail(loggedUser.getEmail());
                    Timestamp time = getCurrentTime();
                    messageService.sendMessage(userSender.get().getId(), user.get().getId(), message, time);
                }
            }
        }
        else {
            Optional<UserDTO> user = userService.findByEmail(email);
            Optional<UserDTO> userSender = userService.findByEmail(loggedUser.getEmail());
            Timestamp time = getCurrentTime();
            messageTableView.getSelectionModel().clearSelection();
            messageService.sendMessage(userSender.get().getId(), user.get().getId(), message, time, selectedMessage.getId());
        }
        emailTextField.clear();
        messageTextField.clear();
    }

    private Timestamp getCurrentTime(){
        long currentTimeMillis = System.currentTimeMillis();
        Date currentDate = new Date(currentTimeMillis);
        Timestamp timestamp = new Timestamp(currentDate.getTime());
        return timestamp;
    }
    /*----------------------------------------Paging logic----------------------------------------------------------*/

    @FXML
    private void handleScroll(ScrollEvent event) {
        if (event.getDeltaY() < 0) {
            offSet++;
            loadMessages();
        } else if(offSet > 0){
            offSet--;
            loadMessages();
        }

    }

    @FXML
    private void setLimit(ActionEvent actionEvent) {
        limit = Integer.parseInt(limitTextField.getText());
        loadMessages();
    }
}

