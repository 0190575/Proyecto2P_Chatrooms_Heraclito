/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclientui;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import static chatclientui.ChatClientUI.menu;
import static chatclientui.ChatClientUI.nickname;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

/**
 *
 * @author Heraclito
 */
public class ChatRoom {
    //ArrayList<Label> messages;
    //ArrayList<Button> members;
    public VBox messages;
    public VBox members;
    private Boolean admin;
    private String name;
    DataOutputStream outSocket;
    public ChatRoom(String name, DataOutputStream outSocket) {
        this.name = name;
        this.outSocket = outSocket;
        messages = new VBox();
        members = new VBox();
        admin = false;
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
        hb.setPadding(new Insets(5, 10, 5, 10));
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
        hb.setPadding(new Insets(5, 10, 5, 10));
        hb.setAlignment(Pos.CENTER_RIGHT);
        hb.autosize();
        hb.getChildren().add(l);
        messages.getChildren().add(hb);
    }
    
    public void updateMembers(String memberName)
    {
        Label l = new Label(memberName);
        if(getAdmin() && memberName.equals(nickname))
            l.setText(memberName + " (admin)");
        l.setTextFill(Paint.valueOf("black"));
        l.setStyle("-fx-font-weight: bold;");
        l.setAlignment(Pos.CENTER_LEFT);
        HBox hb = new HBox();
        hb.setId(memberName);
        hb.setPadding(new Insets(10, 10, 10, 10));
        hb.setAlignment(Pos.CENTER_LEFT);
        hb.autosize();
        hb.getChildren().add(l);
        members.getChildren().add(hb);
    }
    
    public void deleteMember(String memberName)
    {
        for(int i = 0; i < members.getChildren().size(); i++)
        {
            if(members.getChildren().get(i).getId().equals(memberName))
            {
                members.getChildren().remove(i);
                break;
            }
        }
    }
    
    public Scene chatScene()
    {
        ScrollPane msgPane = new ScrollPane();
        msgPane.setFitToWidth(true);
        msgPane.setPrefSize(600, 350);
        msgPane.setContent(messages);
        
        ScrollPane membersPane = new ScrollPane();
        membersPane.setFitToWidth(true);
        membersPane.setPrefSize(200, 350);
        membersPane.setContent(members);
        
        HBox hb = new HBox();
        hb.setPadding(new Insets(15, 15, 15, 15));
        hb.setSpacing(10);
        hb.getChildren().add(membersPane);
        hb.getChildren().add(msgPane);
        
        TextField message = new TextField();
        message.setOnKeyPressed(sendMessage());
        
        Button menuBtn = new Button("Back to menu");
        menuBtn.setOnAction(new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle(ActionEvent event) 
            {
                menu.stage.setTitle("Menu");
                menu.stage.setScene(menu.menuScene());
                menu.stage.show();
            }
        });
        
        Button leaveBtn = new Button("Leave room");
        leaveBtn.setId(name);
        leaveBtn.setOnAction(new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle(ActionEvent event) 
            {
                try {
                    outSocket.writeInt(104);
                    outSocket.writeUTF(name);
                    
                } catch (IOException ex) {
                    Logger.getLogger(ChatRoom.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println(((Button)event.getSource()).getId());
                menu.stage.setTitle("Menu");
                menu.stage.setScene(menu.menuScene());
                menu.stage.show();
                //Platform.runLater(()->{
                menu.leaveRoom(((Button)event.getSource()).getId());
            //});
            }
        });
        HBox bttns = new HBox();
        bttns.setPadding(new Insets(15, 15, 15, 15));
        bttns.setSpacing(100);
        bttns.getChildren().add(menuBtn);
        bttns.getChildren().add(leaveBtn);
        
        VBox vb = new VBox();
        vb.setPadding(new Insets(15, 15, 15, 15));
        vb.setSpacing(10);
        vb.getChildren().add(hb);
        vb.getChildren().add(message);
        vb.getChildren().add(bttns);
        
        Scene newScene = new Scene(vb, 900, 600);
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

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
    
    
}
