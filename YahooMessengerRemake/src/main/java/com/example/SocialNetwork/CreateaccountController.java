package com.example.SocialNetwork;

import com.example.SocialNetwork.Controller.MessageAlert;
import com.example.SocialNetwork.Domain.User;
import com.example.SocialNetwork.Service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;

import static com.example.SocialNetwork.Domain.Validator.EmailValidator.isValidEmail;

public class CreateaccountController {

    UserService service = UserService.getInstance();

    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordTextField;
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public void initialize() {
    }

    private void clearFields(){
        firstNameTextField.clear();
        lastNameTextField.clear();
        emailTextField.clear();
        passwordTextField.clear();
    }

    @FXML
    private void onAddButtonClick(ActionEvent event) throws IOException {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String email = emailTextField.getText();
        String password = passwordTextField.getText();
        if(firstName.isEmpty() || lastName.isEmpty()|| password.isEmpty()|| email.isEmpty()){
            MessageAlert.showErrorMessage(null, "Please fill in all the text fields in order to create an account!");
            return;
        }
        if(!isValidEmail(email)){
            MessageAlert.showErrorMessage(null, "Email format is not valid!");
            return;
        }
        User newUser = new User(firstName, lastName, email, bCryptPasswordEncoder.encode(password));
        try{service.add(firstName, lastName, email, bCryptPasswordEncoder.encode(password));}
        catch(Exception e){
            MessageAlert.showErrorMessage(null, "Account with email " + email + " already exists!");
            return;
        }
        this.clearFields();
        createWindowForNewAccount(newUser);
    }

    private void createWindowForNewAccount(User newUser) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("home-table.fxml"));
        Parent root = loader.load();
        HomeController homeController = loader.getController();
        homeController.setLoggedUser(newUser);
        stage.setTitle("Welcome " + newUser.getEmail());
        stage.setScene(new Scene(root, 630, 470));
        stage.show();
    }
}
