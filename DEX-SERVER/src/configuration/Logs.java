package configuration;

public class Logs {

    public void log(String tag, String content){
        System.out.println(tag+": "+ content);
    }
    public void log( String content){
        System.out.println(content);
    }
    public static void log(Class classObject, String content){ System.out.println(classObject.toString()+" : "+content);
    }
}
