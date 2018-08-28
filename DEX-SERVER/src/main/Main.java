package main;

import configuration.ConfigurationData;
import server_manager.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String args[]) throws IOException {
        Server server= new Server();
        server.startServer();
       /* ServerSocket serverSocket = new ServerSocket(1997);
        while (true){
           Socket socket= serverSocket.accept();
           BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String contents = "";
            while (bufferedReader.ready() || contents == "") {
                int i;
                if((i=bufferedReader.read())!=-1) {
                    contents += (char) i;
                }
            }
           System.out.println(contents);
           socket.getOutputStream().write("hi b\n".getBytes());
           bufferedReader.close();
           socket.close();
        }*/
    }
}
