package com.example.SocialNetwork;

import com.example.SocialNetwork.Controller.MessageAlert;
import com.example.SocialNetwork.Domain.User;
import com.example.SocialNetwork.Service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.example.SocialNetwork.Domain.Validator.EmailValidator.isValidEmail;

public class UserController {

    UserService service ;
    @FXML
    private TableView<User> tableView;
    @FXML
    private TableColumn<User, Long> idCol;
    @FXML
    private TableColumn<User, String> firstNameCol;
    @FXML
    private TableColumn<User, String> lastNameCol;
    @FXML
    private TableColumn<User, String> emailCol;
    @FXML
    private TableColumn<User, String> passwordCol;

    @FXML
    private TextField idTextField;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField passwordTextField;
    public UserController(){
        this.service = UserService.getInstance();
    }


    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        System.out.println("Ok");
        try {
            Iterable<User> usersIterable = service.findAll();
            List<User> usersList = StreamSupport.stream(usersIterable.spliterator(), false)
                    .collect(Collectors.toList());

            ObservableList<User> users = FXCollections.observableArrayList(usersList);
            tableView.setItems(users);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }


    public void loadData() {
        try {
            Iterable<User> usersIterable = service.findAll();
            List<User> usersList = StreamSupport.stream(usersIterable.spliterator(), false)
                    .collect(Collectors.toList());

            ObservableList<User> users = FXCollections.observableArrayList(usersList);
            tableView.setItems(users);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }


    private void clearFields(){
        // Clear the text fields after adding the user
        idTextField.clear();
        firstNameTextField.clear();
        lastNameTextField.clear();
        emailTextField.clear();
        passwordTextField.clear();
    }


    @FXML
    private void onAddButtonClick(ActionEvent event) {
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
        User newUser = new User(firstName, lastName, email, password);
        service.add(firstName, lastName, email, password);
        tableView.getItems().add(newUser);
        loadData();
    }


    @FXML
    private void onDeleteButtonClick(ActionEvent event) throws IOException {
        String idTextFieldText = idTextField.getText();
        if(idTextFieldText.isEmpty()){
            MessageAlert.showErrorMessage(null, "Nu ati introdus ID-ul!");
            return;
        }
        try{
            long id = Long.parseLong(idTextFieldText);
            User user = service.findOne(id).get();
            tableView.getItems().remove(user);
            service.remove(id);
        }catch (Exception e){
            MessageAlert.showErrorMessage(null, "Nu exista un utilizator cu acest ID!");
        }
        loadData();
        this.clearFields();
    }


    @FXML
    private void onUpdateButton(ActionEvent event) throws IOException {
        String idTextFieldText = idTextField.getText();
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String email = emailTextField.getText();
        String password = passwordTextField.getText();

        if(idTextFieldText.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()){
            MessageAlert.showErrorMessage(null, "Nu ati introdus toate datele!");
            return;
        }
        try{
            long id = Long.parseLong(idTextFieldText);
            User user = service.findOne(id).get();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPassword(password);
            service.update(user);
        }catch (Exception e){
            MessageAlert.showErrorMessage(null, "Nu exista un utilizator cu acest ID!");
        }
        loadData();
        this.clearFields();
    }


    @FXML
    private void onFindButton(ActionEvent event) throws IOException {
        String idTextFieldText = idTextField.getText();
        if(idTextFieldText.isEmpty()){
            MessageAlert.showErrorMessage(null, "Nu ati introdus ID-ul!");
            return;
        }
        try{
            long id = Long.parseLong(idTextFieldText);
            User user = service.findOne(id).get();
            firstNameTextField.setText(user.getFirstName());
            lastNameTextField.setText(user.getLastName());
            emailTextField.setText(user.getEmail());
            passwordTextField.setText(user.getPassword());
        }catch (Exception e){
            MessageAlert.showErrorMessage(null, "Nu exista un utilizator cu acest ID!");
        }
    }


}
