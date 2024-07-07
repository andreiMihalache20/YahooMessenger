package com.example.SocialNetwork;

import com.example.SocialNetwork.Controller.MessageAlert;
import com.example.SocialNetwork.Domain.Request;
import com.example.SocialNetwork.Domain.User;
import com.example.SocialNetwork.Domain.UserDTO;
import com.example.SocialNetwork.Service.FriendshipService;
import com.example.SocialNetwork.Service.RequestService;
import com.example.SocialNetwork.Service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Optional;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.example.SocialNetwork.Domain.Validator.EmailValidator.isValidEmail;

public class FriendrequestsController {
    private User loggedUser;
    private FriendshipService friendshipService;
    private UserService userService;
    private final RequestService service ;
    @FXML
    private TableView<Request> tableView;
    @FXML
    private TableColumn<Request, String> emailCol;
    @FXML
    private TableColumn<Request, String> dateCol;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField fromEmailTextField;

    public FriendrequestsController(){
        this.service = RequestService.getInstance();
        friendshipService = FriendshipService.getInstance();
        this.userService = UserService.getInstance();
    }

    public void initialize() {
        emailCol.setCellValueFactory(new PropertyValueFactory<>("fromEmail"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        try {
            Iterable<Request> requestsIterable = service.findRequests(loggedUser.getEmail());
            List<Request> requestsList = StreamSupport.stream(requestsIterable.spliterator(), false)
                    .collect(Collectors.toList());

            ObservableList<Request> requests = FXCollections.observableArrayList(requestsList);
            tableView.setItems(requests);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void loadData() {
        try {
            Iterable<Request> usersIterable = service.findRequests(loggedUser.getEmail());
            List<Request> usersList = StreamSupport.stream(usersIterable.spliterator(), false)
                    .collect(Collectors.toList());

            ObservableList<Request> requests = FXCollections.observableArrayList(usersList);
            tableView.setItems(requests);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private Timestamp getCurrentTime(){
        long currentTimeMillis = System.currentTimeMillis();
        Date currentDate = new Date(currentTimeMillis);
        Timestamp timestamp = new Timestamp(currentDate.getTime());
        return timestamp;
    }

    @FXML
    private void onAddButtonClick(ActionEvent event) {
        String toEmail = emailTextField.getText();
        if(toEmail.isEmpty()){
            MessageAlert.showErrorMessage(null, "Please fill in all the text fields in order to create an account!");
            return;
        }

        if(!isValidEmail(toEmail)){
            MessageAlert.showErrorMessage(null, "Email format is not valid!");
            return;
        }
        if(toEmail.equals(loggedUser.getEmail())){
            MessageAlert.showErrorMessage(null, "You can't send a friend request to yourself!");
            return;
        }
        try {
            Timestamp time = getCurrentTime();
            service.add(loggedUser.getEmail(), toEmail, "pending", time);
        }catch(Exception e){
            MessageAlert.showErrorMessage(null, "You can't add this user ! ");
            emailTextField.clear();
            return;
        }
        emailTextField.clear();
    }

    @FXML
    private void onDeclineButtonClick(ActionEvent event) throws IOException {
        String email = fromEmailTextField.getText();
        try{
            Request request = service.findRequest(email,loggedUser.getEmail()).get();
            request.setRequestStatus("declined");
            service.update(request);
            loadData();
        }catch (Exception e){
            MessageAlert.showErrorMessage(null,   e.getMessage());
        }
        loadData();
        emailTextField.clear();
    }
    public void handleSelectUser(){
        Request request =  tableView.getSelectionModel().getSelectedItem();

        fromEmailTextField.setText(request.getFromEmail());
        loadData();
    }

    public void setLoggedUser(User user){
        this.loggedUser = user;
    }

    public void updateRequests(ActionEvent actionEvent) {
        loadData();
    }

    public void onAcceptButtonClick(ActionEvent actionEvent) {
        String email = fromEmailTextField.getText();
        try{
            Request request = service.findRequest(email,loggedUser.getEmail()).get();
            request.setRequestStatus("accepted");
            service.update(request);
            Optional<UserDTO> user1 = userService.findByEmail(email);
            Optional<UserDTO> user2 = userService.findByEmail(loggedUser.getEmail());
            friendshipService.add(user1.get().getId(),user2.get().getId());
            loadData();
        }catch (Exception e){
            MessageAlert.showErrorMessage(null,   e.getMessage());
        }
        loadData();
        emailTextField.clear();
    }
}
