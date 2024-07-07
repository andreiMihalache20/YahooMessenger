module com.example.SocialNetwork {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    requires spring.security.crypto;

//    requires org.controlsfx.controls;
//    requires com.dlsc.formsfx;
//    requires net.synedra.validatorfx;
//    requires org.kordamp.ikonli.javafx;
//    requires org.kordamp.bootstrapfx.core;
//    requires eu.hansolo.tilesfx;
//    requires com.almasb.fxgl.all;

    opens com.example.SocialNetwork to javafx.fxml, javafx.base;
    opens com.example.SocialNetwork.Domain to javafx.base; // Add this line
    exports com.example.SocialNetwork;
}