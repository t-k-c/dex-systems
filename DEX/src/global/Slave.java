package global;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;

import java.io.*;

/*
  Slave is responsible for all common operations
 */
public class Slave {
    public static String readFile(File file) throws IOException {
        StringBuilder file_content = new StringBuilder();
        FileReader fileReader = new FileReader(file);
        int c;
        while (fileReader.ready() && (c = fileReader.read()) != -1) {
            file_content.append((char) c);
        }
        fileReader.close();
        return file_content.toString();
    }

    public static void writeFile(File file, String data) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.append(data).append("\n");
        fileWriter.close();
    }
    public static void animateClosure(Node node,Runnable runnable){
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500));
        fadeTransition.setDelay(Duration.millis(100));
        fadeTransition.setNode(node);
        fadeTransition.setToValue(0);
        fadeTransition.play();
        fadeTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                runnable.run();
            }
        });
    }
}
