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
    outSocket.writeUTF(name);
    do {
      String msg = inSocket.readUTF();
      System.out.println(msg);
      if(inSocket.available() == 0)
        outSocket.writeUTF(scan.nextLine());
    } while (true);
  }
      catch(Exception ex){
          System.out.println("You must first start the server socket");
          System.out.println("(YourServer.java) at the command prompt.");
          System.out.println(ex);
      }

  }
}
