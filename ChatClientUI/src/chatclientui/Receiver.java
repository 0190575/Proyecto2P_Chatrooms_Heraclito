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
    Menu menu;

    public Receiver(DataInputStream inSocket, Menu menu) {
        this.inSocket = inSocket;
        this.menu = menu;
    }
    
    @Override
    public void run() {
        
        do {
            int code;
            try {
                code = inSocket.readInt();
                selectAction(code);
//                Platform.runLater(() -> {
//                    try {
//                        //
//                        selectAction(code);
////                      updateMessages(msg);
//                    } catch (IOException ex) {
//                        Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                    });
                
            } catch (IOException ex) {
                Logger.getLogger(ChatClientUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while (true);
    }
    
    private void selectAction(int action) throws IOException
    {
        switch(action)
        {
            case 10: //Nuevo usuario
            {
                String s = inSocket.readUTF();
                Platform.runLater(() -> {
                menu.addUser(s);
                });
                break;
            }
            case 11: //Nueva sala
            {
                String s = inSocket.readUTF();
                Platform.runLater(() -> {
                    menu.addRoom(s);                
                });
                break;
            }
            case 12: // Respuesta de union a sala
            {
                String s = inSocket.readUTF();
                Platform.runLater(() -> {
                    menu.addMyRoom(new ChatRoom(s, menu.outSocket));
                });
                break;
            }
            case 13:
            {
                break;
            }
            case 14: //Mensaje
            {
                ChatRoom cr = menu.getChatRoombyName(inSocket.readUTF());
                String msg = inSocket.readUTF();
                Platform.runLater(() -> {
                    cr.updateMessages(msg);
                });
                break;
            }
            case 15:
            {
                break;
            }
            case 16:
            {
                break;
            }
            case 17: //Usuario sale de sala
            {
                ChatRoom cr = menu.getChatRoombyName(inSocket.readUTF());
                String member = inSocket.readUTF();
                Platform.runLater(() -> {
                    cr.deleteMember(member);
                });
                break;
            }
            case 18: //Entrada de usuario a la sala
            {
                ChatRoom cr = menu.getChatRoombyName(inSocket.readUTF());
                String user = inSocket.readUTF();
                Platform.runLater(() -> {
                    cr.updateMembers(user);
                });
                break;
            }
            case 19:
            {
                break;
            }
            default:
            {
                break;
            }
        }
    }
//    public void updateMessages(String message)
//    {
//        Label l = new Label(message);
//        l.setTextFill(Paint.valueOf("blue"));
//        l.setStyle("-fx-font-weight: bold;");
//        HBox hb = new HBox();
//        hb.setPadding(new Insets(10, 10, 10, 10));
//        hb.setAlignment(Pos.CENTER_LEFT);
//        hb.getChildren().add(l);
//        messages.getChildren().add(hb);
//    }
}
