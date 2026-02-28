
package chatappv2;

import chatappv2.Commands.Command;
import chatappv2.Commands.DirectMessage;
import chatappv2.Commands.DirectMessageCommand;
import chatappv2.Commands.HistoryCommand;
import chatappv2.Commands.ListCommand;
import chatappv2.Commands.LoginCommand;
import chatappv2.Commands.RegisterCommand;
import java.io.IOException;
import java.util.*;
import java.net.*;
import org.json.JSONObject;




public class ChatAppv2 {

    private static final Scanner scanner = new Scanner(System.in);
    private static Map<String, Command> commands = new HashMap<>();
    
    public static void main(String[] args) {
        Repository repo = new Repository();
        Service service = new Service(repo);
        commands.put("/register", new RegisterCommand(service));
        commands.put("/login", new LoginCommand(service));
        commands.put("/list", new ListCommand(service));
        commands.put("/w", new DirectMessageCommand(service));
        commands.put("/whisper", new DirectMessage(service));
        commands.put("/history", new HistoryCommand(service));
        try(DatagramSocket ds = new DatagramSocket(service.getServerPort())){
            byte[] b = new byte[1024];
            
            Thread thread = new Thread(() ->{ 
                try {
                    while(true){
                        DatagramPacket dp = new DatagramPacket(b, b.length);
                        ds.receive(dp);
                        executeCommand(ds, dp, service);
                        
                        //service.identifyCommand(ds, dp);
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
    
    public static String getCommand(DatagramPacket dp){
        String msg = new String(dp.getData(), 0, dp.getLength());
        JSONObject msgJSON = new JSONObject(msg);
        String command = msgJSON.optString("command");
        if(command.equals(""))
            return "/whisper";
        return command;
    }
    
    public static void executeCommand(DatagramSocket ds, DatagramPacket dp, Service service){
        Command command = null;
        command = commands.get(getCommand(dp));
        if(command != null)
            command.execute(ds, dp);
        else
            service.sendToSender(ds, dp, "Invalid command");
    }
    
}
