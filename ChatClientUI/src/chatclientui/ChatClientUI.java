/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclientui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

/**
 *
 * @author Heraclito
 */
public class ChatClientUI extends Application {
    String nickname;
    Socket socket;
    DataInputStream inSocket;
    DataOutputStream outSocket;
    public ScrollPane sp;
    @Override
    public void start(Stage primaryStage) {
        
        try
        {

        socket = new Socket("127.0.0.1", 5000);
        inSocket = new DataInputStream(socket.getInputStream());
        outSocket = new DataOutputStream(socket.getOutputStream());

        }
        catch(Exception ex){
          System.out.println("You must first start the server socket");
          System.out.println("(YourServer.java) at the command prompt.");
          System.out.println(ex);
      }
        Button btn = new Button();
        btn.setText("START");
        Label nameLabel = new Label("Nickname");
        Label ipLabel = new Label("Server's IP address");
        TextField ip = new TextField();
        TextField name = new TextField();
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                nickname = name.getText();
                primaryStage.setScene(chatScene());
                
                Thread t = new Thread(new Receiver(inSocket, sp));
                t.start();
                
                try {
                    outSocket.writeUTF(nickname);
                } catch (IOException ex) {
                    Logger.getLogger(ChatClientUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                primaryStage.show();
            }
        });
        VBox vb = new VBox();
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        vb.setPadding(new Insets(15, 15, 15, 15));
        vb.setSpacing(10);
        vb.getChildren().add(nameLabel);
        vb.getChildren().add(name);
        vb.getChildren().add(ipLabel);
        vb.getChildren().add(ip);
        vb.getChildren().add(root);
        Scene scene = new Scene(vb, 300, 250);
        primaryStage.setTitle("ChatRoom");
        primaryStage.setScene(scene);
        primaryStage.show();
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    private Scene chatScene()
    {
        VBox vb = new VBox();
        StackPane root = new StackPane();
        sp = new ScrollPane();
        VBox messages = new VBox();
        sp.setFitToWidth(true);
        sp.setPrefHeight(350);
        sp.setContent(messages);
        TextField message = new TextField();
        message.setOnKeyPressed(sendMessage());
        vb.setPadding(new Insets(15, 15, 15, 15));
        vb.setSpacing(10);
        vb.getChildren().add(sp);
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
                        updateMessages(msg);
                        try {
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
    
    synchronized public void updateMessages(String message)
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
        ((VBox)sp.getContent( )).getChildren().add(hb);
        sp.setVvalue(1.0d);
    }
}

