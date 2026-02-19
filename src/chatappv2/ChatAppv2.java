
package chatappv2;

import java.io.IOException;
import java.util.*;
import java.net.*;


//====================================MAIN================================
public class ChatAppv2 {

    private static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        Repository repo = new Repository();
        Service service = new Service(repo);
        try(DatagramSocket ds = new DatagramSocket(service.getServerPort())){
            byte[] b = new byte[1024];
            
            Thread thread = new Thread(() ->{ 
                try {
                    while(true){
                        DatagramPacket dp = new DatagramPacket(b, b.length);
                        ds.receive(dp);
                        service.identifyCommand(ds, dp);
                    }
                } catch (IOException ex) {
                    System.getLogger(ChatAppv2.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
            });
            
            thread.start();
            System.out.println("================Server Started====================");
            
            while(true){
                String msg = scanner.nextLine();
                if(msg.equals("/list"))
                    System.out.println(service.getAllUsers(""));
                
            }
            
        } catch (SocketException ex) {
            System.out.println("Error connecting to the server");;
        }
    }
    
}
