package server_manager;

import configuration.Logs;

import java.sql.*;
import java.util.ArrayList;

public  class DataBaseConnector {

    private static Connection getConnector() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dex", "root", "");
            Logs.log(DataBaseConnector.class, "connected to the database server");
            return connection;
        } catch (SQLException e) {
            Logs.log(DataBaseConnector.class, "SQL EXception");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Never call DataBaseConnector method. DataBaseConnector method is used by other method to facilitate the operations carried out
     */

    private static PreparedStatement query(String sqlQuery, ArrayList<String> parameters) throws SQLException {
        PreparedStatement preparedStatement = getConnector().prepareStatement(sqlQuery);
        int counter = 1;
        for (String param :
                parameters) {
            preparedStatement.setString(counter, param);
            counter++;
        }
        return preparedStatement;
    }

    public static ResultSet queryResult(String sqlQuery, ArrayList<String> parameters) throws SQLException {
        PreparedStatement preparedStatement = query(sqlQuery, parameters); //already prepared statement -=== the irony :)
        return preparedStatement.executeQuery();
    }
    public static boolean hasResult(String sqlQuery, ArrayList<String> parameters) throws SQLException {
       ResultSet  resultSet =  queryResult(sqlQuery, parameters);
       return resultSet.getFetchSize()!=0;
    }

    public static boolean executeQuery(String sqlQuery, ArrayList<String> parameters) throws SQLException {
        PreparedStatement preparedStatement = query(sqlQuery, parameters); //already prepared statement -=== the irony :)
        return preparedStatement.execute();
    }

    public static int executeUpdate(String sqlQuery, ArrayList<String> parameters) throws SQLException {
        PreparedStatement preparedStatement = query(sqlQuery, parameters); //already prepared statement -=== the irony :)
        return preparedStatement.executeUpdate();
    }

}
