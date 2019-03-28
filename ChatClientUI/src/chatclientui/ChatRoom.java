/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclientui;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

/**
 *
 * @author Heraclito
 */
public class ChatRoom {
    //ArrayList<Label> messages;
    //ArrayList<Button> members;
    public VBox messages;
    public VBox members;
    private String name;
    DataOutputStream outSocket;
    public ChatRoom(String name, DataOutputStream outSocket) {
        this.name = name;
        this.outSocket = outSocket;
        messages = new VBox();
        members = new VBox();
    }

    public String getName() {
        return name;
    }
    
    public void updateMessages(String message)
    {
        Label l = new Label(message);
        l.setTextFill(Paint.valueOf("blue"));
        l.setStyle("-fx-font-weight: bold;");
        l.setAlignment(Pos.CENTER_LEFT);
        HBox hb = new HBox();
        hb.setPadding(new Insets(10, 10, 10, 10));
        hb.setAlignment(Pos.CENTER_LEFT);
        hb.autosize();
        hb.getChildren().add(l);
        messages.getChildren().add(hb);
    }
    public void myMessage(String message)
    {
        Label l = new Label(message);
        l.setTextFill(Paint.valueOf("red"));
        l.setStyle("-fx-font-weight: bold;");
        l.setAlignment(Pos.CENTER_RIGHT);
        HBox hb = new HBox();
        hb.setPadding(new Insets(10, 10, 10, 10));
        hb.setAlignment(Pos.CENTER_RIGHT);
        hb.autosize();
        hb.getChildren().add(l);
        messages.getChildren().add(hb);
    }
    public void updateMembers(String memberName)
    {
        Label l = new Label(memberName);
        l.setTextFill(Paint.valueOf("black"));
        l.setStyle("-fx-font-weight: bold;");
        l.setAlignment(Pos.CENTER_LEFT);
        HBox hb = new HBox();
        hb.setPadding(new Insets(10, 10, 10, 10));
        hb.setAlignment(Pos.CENTER_LEFT);
        hb.autosize();
        hb.getChildren().add(l);
        members.getChildren().add(hb);
    }
    
    public Scene chatScene()
    {
        VBox vb = new VBox();
        HBox hb = new HBox();
        hb.setPadding(new Insets(15, 15, 15, 15));
        hb.setSpacing(10);
        ScrollPane msgPane = new ScrollPane();
        msgPane.setFitToWidth(true);
        msgPane.setPrefSize(350, 350);
        msgPane.setContent(messages);
        ScrollPane membersPane = new ScrollPane();
        membersPane.setFitToWidth(true);
        membersPane.setPrefSize(100, 350);
        membersPane.setContent(members);
        hb.getChildren().add(membersPane);
        hb.getChildren().add(msgPane);
        TextField message = new TextField();
        message.setOnKeyPressed(sendMessage());
        vb.setPadding(new Insets(15, 15, 15, 15));
        vb.setSpacing(10);
        vb.getChildren().add(hb);
        vb.getChildren().add(message);
        Scene newScene = new Scene(vb, 500, 600);
        return newScene;
    }
    
    private EventHandler sendMessage(){
        EventHandler enter = new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent ke) {
                if(ke.getCode().equals(KeyCode.ENTER))
                {
                    String msg = ((TextField)ke.getSource()).getText();
                    ((TextField)ke.getSource()).setText("");
                    if(!msg.equals(""))
                    {
                        myMessage(msg);
                        try {
                            outSocket.writeInt(107);
                            outSocket.writeUTF(name);
                            outSocket.writeUTF(msg);
                        } catch (IOException ex) {
                            Logger.getLogger(ChatClientUI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        };
        return enter;
    }
}
