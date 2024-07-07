package com.example.SocialNetwork;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Launch the first window (UsersTable)
        UsersTable usersTable = new UsersTable();
        usersTable.start(primaryStage);

    }
}
