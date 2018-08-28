package main;

import javax.naming.ldap.SortKey;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MainClient {
    public static void main(String args[]) throws IOException {
        Socket socket = new Socket("localhost",1997);
        socket.getOutputStream().write(("{\"options\":\"view-operation\",\"card_id\":\"12343546576879\"}").getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String contents = "";
        while (bufferedReader.ready() || contents == "") {
            int i;
            if((i=bufferedReader.read())!=-1) {
                contents += (char) i;
            }
        }
        System.out.println(contents);
        bufferedReader.close();
        socket.close();
    }
}
