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
import javafx.scene.control.Alert;

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
                if(inSocket.readBoolean())
                {
                    String s = inSocket.readUTF();
                    int n = inSocket.readInt();
                    ChatRoom cr = new ChatRoom(s, menu.outSocket);
                    for(int i = 0; i < n; i++)
                        cr.updateMembers(inSocket.readUTF());
                    Platform.runLater(() -> {
                        menu.addMyRoom(cr);
                    });
                }
                else
                {
                    String s = inSocket.readUTF();
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Join request");
                        alert.setHeaderText("Your request to join " + s + " has been rejected by its admin");
                        alert.showAndWait();
                    });
                }
                break;
            }
            case 13: //Sala eliminada por admin
            {
                ChatRoom cr = menu.getChatRoombyName(inSocket.readUTF());
                Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Deleted room");
                        alert.setHeaderText(cr.getName() + " has been deleted by its admin");
                        if(menu.stage.getTitle().equals(cr.getName()))
                        {
                            menu.stage.setTitle("Menu");
                            menu.stage.setScene(menu.menuScene());
                            menu.stage.show();
                        }
                        alert.showAndWait();
                        menu.leaveRoom(cr.getName());
                    });
                break;
            }
            case 14: //Mensaje
            {
                ChatRoom cr = menu.getChatRoombyName(inSocket.readUTF());
                String msg = inSocket.readUTF();
                Platform.runLater(() -> {
                    if(!cr.getName().equals(menu.stage.getTitle()))
                    {
                        ChatRoom activeRoom = menu.getChatRoombyName(menu.stage.getTitle());
                        activeRoom.alertNewMessages(true);
                    }
                    cr.updateMessages(msg);
                });
                break;
            }
            case 15: //Peticion de entrada a sala
            {
                String user = inSocket.readUTF();
                String room = inSocket.readUTF();
                Platform.runLater(() -> {
                    menu.joinRequest(user, room);
                    });
                
                break;
            }
            case 16: //Admin privileges
            {
                ChatRoom cr = menu.getChatRoombyName(inSocket.readUTF());
                cr.setAdmin(true);
                Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Admin privileges");
                        alert.setHeaderText("Now you are " + cr.getName() + "'s admin");
                        alert.showAndWait();
                    });
                break;
            }
            case 17: //Usuario sale de sala
            {
                ChatRoom cr = menu.getChatRoombyName(inSocket.readUTF());
                String member = inSocket.readUTF();
                Platform.runLater(() -> {
                    cr.getNotification(member + " has left the room", true);
                    cr.deleteMember(member);
                });
                break;
            }
            case 18: //Entrada de usuario a la sala
            {
                ChatRoom cr = menu.getChatRoombyName(inSocket.readUTF());
                String user = inSocket.readUTF();
                Platform.runLater(() -> {
                    cr.getNotification(user + " has joined the room", false);
                    cr.updateMembers(user);
                });
                break;
            }
            case 19: //Usuario elminado por admin
            {
                ChatRoom cr = menu.getChatRoombyName(inSocket.readUTF());
                Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Deleted from room");
                        alert.setHeaderText("You have been deleted from " + cr.getName());
                        if(menu.stage.getTitle().equals(cr.getName()))
                        {
                            menu.stage.setTitle("Menu");
                            menu.stage.setScene(menu.menuScene());
                            menu.stage.show();
                        }
                        alert.showAndWait();
                        menu.leaveRoom(cr.getName());
                    });
                
                break;
            }
            case 20: //Notificacion de otro usuario eliminado
            {
                ChatRoom cr = menu.getChatRoombyName(inSocket.readUTF());
                String member = inSocket.readUTF();
                Platform.runLater(() -> {
                    cr.getNotification(member + " has been deleted", true);
                    cr.deleteMember(member);
                });
                break;
            }
            case 21: //Usuario agregado por admin
            {
                String s = inSocket.readUTF();
                int n = inSocket.readInt();
                ChatRoom cr = new ChatRoom(s, menu.outSocket);
                for(int i = 0; i < n; i++)
                    cr.updateMembers(inSocket.readUTF());
                Platform.runLater(() -> {
                    menu.addMyRoom(cr);
                });
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
