package com.example.SocialNetwork;

import com.example.SocialNetwork.Controller.MessageAlert;
import com.example.SocialNetwork.Domain.User;
import com.example.SocialNetwork.Security.Authentication;
import com.example.SocialNetwork.Service.MessageService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.Optional;

import java.io.IOException;

import static com.example.SocialNetwork.Domain.Validator.EmailValidator.isValidEmail;

public class LoginController {

    MessageService messageService = MessageService.getInstance();
    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordTextField;
    private final Authentication authentication = new Authentication();

    public void initialize() {
    }
    @FXML
    private void onLogInButton(ActionEvent event) throws IOException {
        String email = emailTextField.getText();
        String password = passwordTextField.getText();
        if(password.isEmpty()|| email.isEmpty()){
            MessageAlert.showErrorMessage(null, "Please fill in all the text fields in order to create an account!");
            return;
        }
        if(!isValidEmail(email)){
            MessageAlert.showErrorMessage(null, "Email format is not valid!");
            return;
        }

        Optional<User> u = authentication.attemptAuthentication(email,password);
        emailTextField.clear();
        passwordTextField.clear();

        if(email.equals("admin@admin.com") && password.equals("admin")){
            startAdminStage();
        }
        else if(u.isPresent()) {
            startUserStage(u.get());
        }
    }

    private void startAdminStage() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(UsersTable.class.getResource("admin-table.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 650, 500);
        stage.setScene(scene);
        stage.show();
    }

    private void startUserStage(User u) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("home-table.fxml"));
        Parent root = loader.load();
        HomeController homeController = loader.getController();
        homeController.setLoggedUser(u);
        homeController.setLoggedInUserForFriendRequests(u);
        messageService.addObserver(homeController);
        stage.setTitle("Welcome " + u.getEmail());
        stage.setScene(new Scene(root, 630, 470));
        stage.show();
    }
}
