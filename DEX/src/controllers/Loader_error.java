package controllers;

import global.Slave;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class Loader_error {
    @FXML
    AnchorPane anchor;
    public void initialize(){

    }


    public void close(MouseEvent mouseEvent) {
        Slave.animateClosure(anchor, new Runnable() {
            @Override
            public void run() {
                anchor.getScene().getWindow().hide();
                System.exit(0);
            }
        });
    }
}
