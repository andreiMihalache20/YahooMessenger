package com.example.SocialNetwork;

import com.example.SocialNetwork.Controller.MessageAlert;
import com.example.SocialNetwork.Domain.Friendship;
import com.example.SocialNetwork.Domain.Tuple;
import com.example.SocialNetwork.Domain.User;
import com.example.SocialNetwork.Service.FriendshipService;
import com.example.SocialNetwork.Service.UserService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FriendshipsController {

    FriendshipService service = FriendshipService.getInstance();

    UserService userService = UserService.getInstance();
    @FXML
    private TableView<Friendship> tableViewFriendship;
    @FXML
    private TableColumn<Friendship, Long> idFriend1;
    @FXML
    private TableColumn<Friendship, Long> idFriend2;
    @FXML
    private TableColumn<Friendship, Timestamp> date;

    @FXML
    private TextField idFriend1TextField;
    @FXML
    private TextField idFriend2TextField;


    public void initialize() {
        idFriend1.setCellValueFactory(cellData -> {
            Tuple<Long, Long> tuple = cellData.getValue().getId();
            return new SimpleObjectProperty<>(tuple.getLeft());
        });
        idFriend2.setCellValueFactory(cellData -> {
            Tuple<Long, Long> tuple = cellData.getValue().getId();
            return new SimpleObjectProperty<>(tuple.getRight());
        });
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        System.out.println("Ok");
        try {
            List<Friendship> friendshipsIterable = (List<Friendship>) service.findAll();
            friendshipsIterable.forEach(System.out::println);
            List<Friendship> friendshipsList = new ArrayList<>(friendshipsIterable);
            ObservableList<Friendship> friendships = FXCollections.observableArrayList(friendshipsList);
            tableViewFriendship.setItems(friendships);
            tableViewFriendship.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void loadData(){
        try {
            List<Friendship> friendshipsIterable = (List<Friendship>) service.findAll();
            List<Friendship> friendshipsList = new ArrayList<>(friendshipsIterable);
            System.out.println(friendshipsList.get(0).getId().getLeft());
            ObservableList<Friendship> friendships = FXCollections.observableArrayList(friendshipsList);
            tableViewFriendship.setItems(friendships);
            tableViewFriendship.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void onAddButtonClick(ActionEvent event){
        Long id1 = Long.parseLong(idFriend1TextField.getText());
        Long id2 = Long.parseLong(idFriend2TextField.getText());
        if(userService.findOne(id1).isEmpty() || userService.findOne(id2).isEmpty()){
            MessageAlert.showErrorMessage(null, "Invalid IDs!");
            return;
        }
        try{
            service.add(id1, id2);
            loadData();
        }catch (Exception e){
            System.out.println(e.getMessage());
            MessageAlert.showErrorMessage(null, "Invalid IDs / Users are already friends");
        }
        idFriend1TextField.clear();
        idFriend2TextField.clear();
    }

    public void onDeleteButtonClick(ActionEvent event){
        long id1 = Long.parseLong(idFriend1TextField.getText());
        long id2 = Long.parseLong(idFriend2TextField.getText());
        try{
            service.remove(id1, id2);
            loadData();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            MessageAlert.showErrorMessage(null, "Invalid IDs");
        }
        idFriend1TextField.clear();
        idFriend2TextField.clear();
    }
}
