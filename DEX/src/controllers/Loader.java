package controllers;

import configuration.Logs;
import configuration.StoredObjects;
import global.Slave;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONObject;
import server.ServerManager;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Loader {
    @FXML
    Text loading_text;
    @FXML
    AnchorPane anchor;

    public void initialize() {
       new Thread(new Runnable() {
           @Override
           public void run() {
               try {
                   Thread.sleep(2000);
                   File file = new File("src/storage/configuration.json");
                   String configurations = Slave.readFile(file);
                   Logs.log(this.getClass(), configurations);
                   JSONObject jsonObject = new JSONObject(configurations);

                   /*
                    *
                    *  1: check the raspberry pi
                    *  2: start internal server
                    *  3: check the master server
                    *
                    * */

            /*
            * Sample config data for network
           {
                 "rpi_address":"127.0.0.1",
                 "rpi_port":1999,
                 "client_socket_server_port":1998,
                 "server_socket_address":"127.0.0.1",
                 "server_socket_port":1997
            }
            * */
                   // checking the RPi
                   Platform.runLater(new Runnable() {
                       @Override
                       public void run() {
                           loading_text.setText("Testing hardware connectivity... ");
                       }
                   });

                   Socket socket = new Socket(jsonObject.getString("rpi_address"),jsonObject.getInt("rpi_port"));
                   socket.getOutputStream().write(System.getProperty("user.name").getBytes());
                   socket.close();
                   Platform.runLater(new Runnable() {
                       @Override
                       public void run() {
                           loading_text.setText("Hardware check positive ");
                       }
                   });
                   Thread.sleep(1000);
                   Platform.runLater(new Runnable() {
                       @Override
                       public void run() {
                           loading_text.setText("Testing Internal server... ");
                       }
                   });
                   //starting the internal server
                   ServerManager serverManager = new ServerManager();
                   serverManager.startServer();
                   StoredObjects.serverManager =  serverManager;
                   Platform.runLater(new Runnable() {
                       @Override
                       public void run() {
                           loading_text.setText("Internal server activated ");
                       }
                   });
                   Thread.sleep(1000);
                   Platform.runLater(new Runnable() {
                       @Override
                       public void run() {
                           loading_text.setText("Testing master server connectivity... ");
                       }
                   });
                   // checking the master server
                   socket = new Socket(jsonObject.getString("server_socket_address"),jsonObject.getInt("server_socket_port"));
                   socket.getOutputStream().write(System.getProperty("user.name").getBytes());
                   socket.close();
                   Platform.runLater(new Runnable() {
                       @Override
                       public void run() {
                           loading_text.setText("Master server reached ");
                       }
                   });
                   Thread.sleep(1000);
                   Platform.runLater(new Runnable() {
                       @Override
                       public void run() {
                           loading_text.setText("All Done! Launching command interface ");
                       }
                   });
                   Slave.animateClosure(anchor, new Runnable() {
                       @Override
                       public void run() {

                           try {
                               anchor.getScene().getWindow().hide();
                               Parent root = FXMLLoader.load(getClass().getResource("../interfaces/main_interface.fxml"));
                               Stage stage = new Stage();
                               stage.initStyle(StageStyle.UNDECORATED);
                               stage.setScene(new Scene(root));
                               stage.show();

                           } catch (IOException e1) {
                               e1.printStackTrace();
                           }
                       }
                   });
               } catch (Exception e) {
                   Logs.err_log(this.getClass(),e.getMessage());
                   Slave.animateClosure(anchor, new Runnable() {
                       @Override
                       public void run() {

                           try {
                               anchor.getScene().getWindow().hide();
                               Parent root = FXMLLoader.load(getClass().getResource("../interfaces/loading_error.fxml"));
                               Stage stage = new Stage();
                               stage.initStyle(StageStyle.UNDECORATED);
                               stage.setScene(new Scene(root));
                               stage.show();

                           } catch (IOException e1) {
                               e1.printStackTrace();
                           }
                       }
                   });

               }
           }
       }).start();
    }
}
/*

    name
            surname
    creation_date
            password
    code
            image
    balance
            status*/
