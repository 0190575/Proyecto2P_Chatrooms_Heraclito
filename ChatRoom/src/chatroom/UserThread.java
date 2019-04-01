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
import java.util.Random;

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
            while(true)
            {
                int code = inSocket.readInt();
                System.out.println(code);
                selectAction(code);
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
                    cr.admin.outSocket.writeInt(15);
                    cr.admin.outSocket.writeUTF(name);
                    cr.admin.outSocket.writeUTF(cr.name);
                }
                break;
            }
            case 104: //Salida de usuario de sala
            {
                Room cr = findRoom(inSocket.readUTF());
                if(cr != null)
                {
                    Boolean isAdmin = cr.admin.equals(this);
                    cr.removeMember(this);
                    if(isAdmin)
                    {
                        cr.admin = null;
                        cr.admin = cr.members.get(new Random().nextInt(cr.members.size()));
                        System.out.println(cr.admin.name);
                        cr.admin.outSocket.writeInt(16);
                        cr.admin.outSocket.writeUTF(cr.name);
                    }
                    
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
            case 108: //Eliminar usuario de sala
            {
                Room cr = findRoom(inSocket.readUTF());
                UserThread member = findUser(inSocket.readUTF());
                cr.removeMember(member);
                member.outSocket.writeInt(13);
                member.outSocket.writeUTF(cr.name);
                for(int i = 0; i < cr.members.size(); i++)
                   {
                        try {
                            cr.members.get(i).outSocket.writeInt(20);
                            cr.members.get(i).outSocket.writeUTF(cr.name);
                            cr.members.get(i).outSocket.writeUTF(member.name);
                        } catch (IOException ex) {
                            Logger.getLogger(UserThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                   }
                break;
            }
            case 109: //Respuesta de solicitud de entrada
            {
                Room cr = findRoom(inSocket.readUTF());
                UserThread newMember = findUser(inSocket.readUTF());
                Boolean response = cr.admin.inSocket.readBoolean();
                newMember.outSocket.writeInt(12);
                newMember.outSocket.writeBoolean(response);
                newMember.outSocket.writeUTF(cr.name);
                if(response)
                {
                    cr.addMember(newMember);
                    newMember.outSocket.writeInt(cr.members.size());
                    for(int i = 0; i < cr.members.size(); i++)
                    {
                        newMember.outSocket.writeUTF(cr.members.get(i).name);
                        if(!cr.members.get(i).equals(newMember))
                        {
                            try {
                                cr.members.get(i).outSocket.writeInt(18);
                                cr.members.get(i).outSocket.writeUTF(cr.name);
                                cr.members.get(i).outSocket.writeUTF(newMember.name);
                            } catch (IOException ex) {
                                Logger.getLogger(UserThread.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                    }
                }
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
    
    private UserThread findUser(String userName)
    {
        for(int i = 0; i < connectedUsers.size(); i++)
        {
            if(connectedUsers.get(i).name.equals(userName))
                return connectedUsers.get(i);
        }
        return null;
    }
}
