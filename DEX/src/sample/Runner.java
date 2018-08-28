package sample;

import configuration.ConfigurationData;
import server.ServerManager;

import java.io.IOException;
import java.net.Socket;

public class Runner {
    public static void main(String args[]) throws IOException {
        ServerManager serverManager = new ServerManager();
        serverManager.startServer();
        Socket socket = new Socket("localhost",ConfigurationData.SOCKET_PORT);
        socket.getOutputStream().write("{\"card_id\":\"xaslahed394h2eda\",\"status\":1}".getBytes());

//        {“card_id”:”value_of_the_id”,”status”:1}
        socket.close();
    }
}
