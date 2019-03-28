import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient
{

  public static void main(String[] args)
  {
    try
    {

    Socket socket = new Socket("127.0.0.1", 5000);
    DataInputStream inSocket = new DataInputStream(socket.getInputStream());
    DataOutputStream outSocket = new DataOutputStream(socket.getOutputStream());
    Scanner scan = new Scanner(System.in);
    String name = scan.nextLine();
    outSocket.writeInt(101);
    outSocket.writeUTF(name);
    int n = inSocket.readInt();
    String a;
    for(int i = 0; i < n; i++)
      a = inSocket.readUTF();
    n = inSocket.readInt();
    for(int i = 0; i < n; i++)
      a = inSocket.readUTF();
    outSocket.writeInt(103);
    outSocket.writeUTF("Prueba");
    do {
      int h = inSocket.readInt();
      String g = inSocket.readUTF();
      String msg = inSocket.readUTF();
      System.out.println(msg);
        String m = scan.nextLine();
      outSocket.writeInt(107);
      outSocket.writeUTF("Prueba");
      outSocket.writeUTF(m);

    } while (true);
  }
      catch(Exception ex){
          System.out.println("You must first start the server socket");
          System.out.println("(YourServer.java) at the command prompt.");
          System.out.println(ex);
      }

  }
}
