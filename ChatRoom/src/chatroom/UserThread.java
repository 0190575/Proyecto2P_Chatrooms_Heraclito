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
import static chatroom.ChatRoom.activeRooms;

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
    }
    
    
    @Override
    public void run() {
         try {
            //outSocket.writeUTF("Hello " + name);
            //String msg = inSocket.readUTF();
            //sendMessage(msg);
            while(true)
            {
                    int code = inSocket.readInt();
                    System.out.println(code);
                    selectAction(code);
                    //System.out.println(msg);
                   // outSocket.writeUTF(msg);
                
                
            }
            
         } catch (IOException ex) {
            Logger.getLogger(UserThread.class.getName()).log(Level.SEVERE, null, ex);
        }
;
        
    }
    
    
    public void sendMessage(String mess)
    {
            for(int i = 0; i < connectedUsers.size(); i++)
            {
                if(!connectedUsers.get(i).equals(this))
                try {
                    connectedUsers.get(i).outSocket.writeInt(14);
                    connectedUsers.get(i).outSocket.writeUTF(mess);
                } catch (IOException ex) {
                    Logger.getLogger(UserThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
    }
    
    public void newUser()
    {
        
    }
    
    private void selectAction(int action) throws IOException
    {
        switch(action)
        {
            case 101: //Nueva conexión
            {
                name = inSocket.readUTF();
                outSocket.writeInt(connectedUsers.size());
                for(int i = 0; i < connectedUsers.size(); i++)
                    outSocket.writeUTF(connectedUsers.get(i).name);
                
                outSocket.writeInt(activeRooms.size());
                for(int i = 0; i < activeRooms.size(); i++)
                    outSocket.writeUTF(activeRooms.get(i).name);
                for (UserThread connectedUser : connectedUsers) {
                if(!connectedUser.equals(this))
                {
                    connectedUser.outSocket.writeInt(10);
                    connectedUser.outSocket.writeUTF(name);
                }
            }
                break;
            }
            case 102: //Crear sala
            {
                String roomName = inSocket.readUTF();
                activeRooms.add(new Room(this, roomName));
                for (UserThread connectedUser : connectedUsers) 
                {
                    connectedUser.outSocket.writeInt(11);
                    connectedUser.outSocket.writeUTF(roomName);
                }
                    
                break;
            }
            case 103: //Peticion de union a sala
            {
                Room cr = findRoom(inSocket.readUTF());
                if(cr != null){
                    cr.addMember(this);
                    outSocket.writeInt(12);
                    outSocket.writeUTF(cr.name);
                    for(int i = 0; i < cr.members.size(); i++)
                    {
                        if(!cr.members.get(i).equals(this))
                        {
                            try {
                                cr.members.get(i).outSocket.writeInt(18);
                                cr.members.get(i).outSocket.writeUTF(cr.name);
                                cr.members.get(i).outSocket.writeUTF(name);
                            } catch (IOException ex) {
                                Logger.getLogger(UserThread.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                    }
                }
                break;
            }
            case 104: //Salida de usuario de sala
            {
                Room cr = findRoom(inSocket.readUTF());
                if(cr != null)
                {
                    cr.removeMember(this);
                    for(int i = 0; i < cr.members.size(); i++)
                    {
                        if(!cr.members.get(i).equals(this))
                        {
                            try {
                                cr.members.get(i).outSocket.writeInt(17);
                                cr.members.get(i).outSocket.writeUTF(cr.name);
                                cr.members.get(i).outSocket.writeUTF(name);
                            } catch (IOException ex) {
                                Logger.getLogger(UserThread.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                    }
                }
                break;
            }
            case 105: 
            {
                break;
            }
            case 106:
            {
                break;
            }
            case 107: //Mensaje
            {
                Room cr = findRoom(inSocket.readUTF());
                if(cr != null)
                    cr.sendMessage(inSocket.readUTF(), this);
                break;
            }
            case 108:
            {
                break;
            }
           
        }
    }
    
    private Room findRoom(String roomName)
    {
        for(int i = 0; i < activeRooms.size(); i++)
        {
            if(activeRooms.get(i).name.equals(roomName))
                return activeRooms.get(i);
        }
        return null;
    }
}
