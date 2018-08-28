package configuration;

import global.Slave;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class Logs {

    public void log(String tag, String content){
        System.out.println(tag+": "+ content);
    }
    public void log( String content){
        System.out.println(content);
    }
    public static void log(Class classObject, String content){ System.out.println(classObject.toString()+" : "+content);
    }
    public static void err_log(String tag, String content){
        try {
            Slave.writeFile(new File("src/storage/error_logs"),new Date().toString()+"/ "+tag+": "+ content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void err_log(Class classObject, String content){
        try {
            Slave.writeFile(new File("src/storage/error_logs"),new Date().toString()+"/ "+classObject.toString()+" : "+content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
