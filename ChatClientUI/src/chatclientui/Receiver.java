/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclientui;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

/**
 *
 * @author Heraclito
 */
public class Receiver implements Runnable{
    
    public DataInputStream inSocket;
    ScrollPane sp;

    public Receiver(DataInputStream inSocket, ScrollPane sp) {
        this.inSocket = inSocket;
        this.sp = sp;
    }
    
    @Override
    public void run() {
        
        do {
            String msg;
            try {
                msg = inSocket.readUTF();
                Platform.runLater(() -> {

                      updateMessages(msg);
                    });
                
            } catch (IOException ex) {
                Logger.getLogger(ChatClientUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while (true);
    }
    public void updateMessages(String message)
    {
        Label l = new Label(message);
        l.setTextFill(Paint.valueOf("blue"));
        l.setStyle("-fx-font-weight: bold;");
        HBox hb = new HBox();
        hb.setPadding(new Insets(10, 10, 10, 10));
        hb.setAlignment(Pos.CENTER_LEFT);
        hb.getChildren().add(l);
        ((VBox)sp.getContent( )).getChildren().add(hb);
        sp.setVvalue(1.0d);
    }
}
