package server;

import configuration.ConfigurationData;
import configuration.Logs;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerManager {
     ServerSocket serverSocket;
    public  void startServer(){
       new Thread(new Runnable() {
           @Override
           public void run() {
               try {
                   Logs.log(this.getClass(),"Attempting server startup");
                   serverSocket = new ServerSocket(ConfigurationData.SOCKET_PORT);
                   Logs.log(this.getClass(),"Server succesfully started");
                   while (true){
                       Logs.log(this.getClass(),"Waiting for server request from client");
                       Socket socket  = serverSocket.accept();
                       Logs.log(this.getClass(),"Server requested granted to client at "+ socket.getLocalAddress());
                       String  content  =readSocket(socket);
                       socket.close();
                   }
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }).start();
    }

    private String readSocket(Socket socket) throws IOException {
        Logs.log(this.getClass(), "Reading the socket for input stream");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String contents = "";
        while (bufferedReader.ready() || contents == "") {
            int i;
            if((i=bufferedReader.read())!=-1) {
                contents += (char) i;
            }
        }
        bufferedReader.close();
        Logs.log(this.getClass(), "Returning after reading from socket with content " + contents);
        return contents;
    }

    public void StopServer() throws IOException {
        Logs.log(this.getClass(),"Shutting down the server");
        serverSocket.close();
    }

    public void treatSocketInformation(String data) throws JSONException {
        Logs.log(this.getClass(),"Extracting the json data");
        JSONObject jsonObject = new JSONObject(data);
//        the actual data structure {“card_id”:”value_of_the_id”,”status”:STATUS_INTEGER} 
        try{
            int i = jsonObject.getInt("status"); // i e {1,0,-1}
            String card_id =  jsonObject.getString("card_id"); // card_id

            Logs.log(this.getClass(),"The value of the card id is " +card_id+" and its status is "+i);

        }catch (NumberFormatException e){
            e.printStackTrace();
        }
    }

}

/*import org.json.*;


JSONObject obj = new JSONObject(" .... ");
String pageName = obj.getJSONObject("pageInfo").getString("pageName");

JSONArray arr = obj.getJSONArray("posts");
for (int i = 0; i < arr.length(); i++)
{
    String post_id = arr.getJSONObject(i).getString("post_id");
    ......
}*/