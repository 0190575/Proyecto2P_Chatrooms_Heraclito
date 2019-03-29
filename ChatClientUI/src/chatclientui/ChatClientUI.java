/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclientui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    public static String nickname;
    public static Stage stage;
    public static Menu menu;
    Socket socket;
    DataInputStream inSocket;
    DataOutputStream outSocket;
    ArrayList<ChatRoom> myChatRooms;
    @Override
    public void start(Stage primaryStage) {
//        myChatRooms = new ArrayList<ChatRoom>();
//        ChatRoom prueba = new ChatRoom("Sala uno", outSocket);
//        myChatRooms.add(prueba);
        
        
        Button btn = new Button();
        btn.setText("START");
        //btn.setId(prueba.getName());
        Label nameLabel = new Label("Nickname");
        Label ipLabel = new Label("Server's IP address");
        TextField ip = new TextField();
        ip.setText("127.0.0.1");
        TextField name = new TextField();
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                nickname = name.getText();
                try
                {

                socket = new Socket(ip.getText(), 5000);
                inSocket = new DataInputStream(socket.getInputStream());
                outSocket = new DataOutputStream(socket.getOutputStream());

                }
                catch(Exception ex){
                  System.out.println("You must first start the server socket");
                  System.out.println(ex);
              }
                menu = new Menu(primaryStage, outSocket);
                
                try {
                    outSocket.writeInt(101);
                    outSocket.writeUTF(nickname);
                    int n = inSocket.readInt();
                    for(int i = 0; i < n; i++)
                        menu.addUser(inSocket.readUTF());
                    n = inSocket.readInt();
                    for(int i = 0; i < n; i++)
                        menu.addRoom(inSocket.readUTF());
                } catch (IOException ex) {
                    Logger.getLogger(ChatClientUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                primaryStage.setScene(menu.menuScene());
                Thread t = new Thread(new Receiver(inSocket, menu));
                t.start();
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
    
    
    synchronized public void updateMessages(String message, VBox messages)
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
    
    public ChatRoom getChatRoombyName(String crName)
    {
        for(int i = 0; i < myChatRooms.size(); i++)
        {
            if(myChatRooms.get(i).getName().equals(crName))
                return myChatRooms.get(i);
        }
            
        return null;
    }
}

