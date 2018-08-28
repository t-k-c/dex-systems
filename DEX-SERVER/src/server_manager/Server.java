package server_manager;

import configuration.ConfigurationData;
import configuration.Logs;
import exception.DEXException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Server {
    private ServerSocket serverSocket;
    private static final String OPERATION_MODIFY = "modification-operation";
    private static final String OPERATION_DELTE = "delete-operation";
    private static final String OPERATION_INIT = "initialisation-operation";
    private static final String OPERATION_PRINT = "printing-operation";
    private static final String OPERATION_VIEW = "view-operation";

    public void startServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Logs.log(this.getClass(), "Attempting server startup");
                    serverSocket = new ServerSocket(ConfigurationData.SERVER_PORT);
                    Logs.log(this.getClass(), "Server succesfully started");
                    while (true) {
                        Logs.log(this.getClass(), "Waiting for server request from client");
                        Socket socket = serverSocket.accept();
                        Logs.log(this.getClass(), "Server requested granted to client at " + socket.getLocalAddress());
                        try {
                            String content = readSocket(socket);
                            treatSocketInformation(socket,content);
                        } catch (Exception e) {
                            e.printStackTrace();
                            String exceptionData = e.getMessage();
                            StackTraceElement[] stackTraceElements = e.getStackTrace();
                            for ( StackTraceElement stackTraceElement:
                                stackTraceElements) {
                                exceptionData+= "\n"+stackTraceElement.toString();
                            }
                            writeSocket(socket, exceptionData );
                        }
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void writeSocket(Socket socket, String string) throws IOException {
        socket.getOutputStream().write(string.getBytes());
        socket.close();
    }

    public boolean serverActive() {
        return serverSocket.isBound();
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
//        bufferedReader.close();
        Logs.log(this.getClass(), "Returning after reading from socket with content " + contents);
        return contents;
    }

    public void StopServer() throws IOException {
        Logs.log(this.getClass(), "Shutting down the server");
        serverSocket.close();
    }

    private void treatSocketInformation(Socket socket,String data) throws JSONException, IOException, SQLException, DEXException {
      if(data.startsWith("::")){ //indicator that it is the raspberry pi asking for the status of the card
          data = data.replace("::","");
          ArrayList<String> params = new ArrayList<>();
          params.add(data);
          System.out.println(data);
          ResultSet resultSet =  DataBaseConnector.queryResult("SELECT * FROM `system_data` WHERE `code` = ?",params);
          if(resultSet.first()){
              String stat =  resultSet.getString("status");
              throw new DEXException(stat);
          }else{
              throw new DEXException("-1"); // card id search doesn't return 1 as result
          }
      }else{
          // Data coming from the pc to carry out an operation
          Logs.log(this.getClass(), "Extracting the json data");
          JSONObject jsonObject = new JSONObject(data);
          String operation = jsonObject.getString("options");
          switch (operation) {
              case OPERATION_DELTE:
                  Logs.log(this.getClass(), "deletion operation");
                  break;
              case OPERATION_INIT:
                  Logs.log(this.getClass(), "Initialization operation");
                  setOperationInit(socket,jsonObject);
//                {"options":"initialisation-operation","card_id":"asdaw123143"}
                  break;
              case OPERATION_PRINT:
                  Logs.log(this.getClass(), "printing operation");
                  break;
              case OPERATION_VIEW:
                  Logs.log(this.getClass(), "viewing operation");
                  setOperationView(socket,jsonObject);
//                {"options":"view-operation","card_id":"asdaw123143"}
                  break;
              default:
                  Logs.log(this.getClass(), "No operation to be carried out on the data to be treated");
          }
          //operation and other data
      }
    }

    private void setOperationView(Socket socket,JSONObject jsonObject) throws SQLException, JSONException, DEXException, IOException{
        Logs.log(this.getClass(), "Preparing the params for the viewing operations");
        ArrayList<String> params = new ArrayList<>();
        params.add(jsonObject.getString("card_id"));
        ResultSet resultSet =  DataBaseConnector.queryResult("SELECT * FROM `system_data` WHERE `code` = ?",params);
        if(resultSet.first()){
            String result = " {\n" +
                    "                \"name\":\""+resultSet.getString("name")+"\"," +
                    "                \"surname\":\""+resultSet.getString("surname")+"\"," +
                    "                \"password\":\""+resultSet.getString("password")+"\"," +
                    "                \"code\":\""+resultSet.getString("code")+"\"," +
                    "                \"image\":\""+resultSet.getString("image")+"\"," +
                    "                \"balance\":\""+resultSet.getString("balance")+"\"," +
                    "                \"status\":\""+resultSet.getString("status")+"\"," +
                    "                \"creation_date\":\""+resultSet.getString("creation_date")+"\"" +
                    "            }";
            throw new DEXException(result);
        }else{
            throw new DEXException("-1"); // card id search doesn't return 1 as result
        }
    }

    private void setOperationInit(Socket socket,JSONObject jsonObject) throws SQLException, JSONException, DEXException, IOException {
        Logs.log(this.getClass(), "Preparing statement parameters for the initialization Operation");
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(jsonObject.getString("card_id"));
        if (DataBaseConnector.hasResult("SELECT * FROM `system_data` where code = ?", arrayList)) {
//            the card alreay exists
            Logs.log(this.getClass(), "Card "+jsonObject.getString("card_id")+" already found in the database" );
        } else {
//            the card doesnt exist deja
            ArrayList<String> arrayList1 = new ArrayList<>();
            arrayList1.add(jsonObject.getString("name"));
            arrayList1.add(jsonObject.getString("surname"));
            arrayList1.add(jsonObject.getString("password"));
            arrayList1.add(jsonObject.getString("code"));
            arrayList1.add(jsonObject.getString("image"));
            arrayList1.add(jsonObject.getString("balance"));
            Logs.log(this.getClass(), "About to initialise in the database");
            if (DataBaseConnector.executeQuery("INSERT INTO `system_data`(`name`, `surname`, `creation_date`, `password`, `code`, `status`, `image`, `balance`) " +
                    "VALUES (?,?,NOW(),?,?,1,?,?)", arrayList1)) {
                throw new DEXException("1");
            } else {
//                operation not effectuated
                throw new DEXException("Couldn't execute initialisation of card " + jsonObject.getString("card_id"));
            }
        }
    }
}