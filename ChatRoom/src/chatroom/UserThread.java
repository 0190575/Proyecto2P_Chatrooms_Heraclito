/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static chatroom.ChatRoom.connectedUsers;

/**
 *
 * @author Heraclito
 */

public class UserThread implements Runnable {
    public String name;
    public Socket socket;
    public DataInputStream inSocket;
    public DataOutputStream outSocket;

    public UserThread(Socket socket, DataInputStream inSocket, DataOutputStream outSocket) {
        this.socket = socket;
        this.inSocket = inSocket;
        this.outSocket = outSocket;
        try {
            this.name = inSocket.readUTF();
        } catch (IOException ex) {
            Logger.getLogger(UserThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        sendMessage(name + " has joined the room");
        System.out.println(name);
    }
    
    
    @Override
    public void run() {
         try {
             outSocket.writeUTF("Hello " + name);
            //String msg = inSocket.readUTF();
            //sendMessage(msg);
            while(true)
            {
                if(inSocket.available() > 0)
                {
                    String msg = inSocket.readUTF();
                    sendMessage(name + ": " + msg);
                    //System.out.println(msg);
                   // outSocket.writeUTF(msg);
                }
                
            }
            
         } catch (IOException ex) {
            Logger.getLogger(UserThread.class.getName()).log(Level.SEVERE, null, ex);
        }
;
        
    }
    
    public void getMessage(String message)
    {
        try {
            outSocket.writeUTF(message);
        } catch (IOException ex) {
            Logger.getLogger(UserThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sendMessage(String mess)
    {
            for(int i = 0; i < connectedUsers.size(); i++)
            {
                if(!connectedUsers.get(i).equals(this))
                try {
                    connectedUsers.get(i).outSocket.writeUTF(mess);
                } catch (IOException ex) {
                    Logger.getLogger(UserThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
    }
}
