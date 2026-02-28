
package chatappv2;

import Exceptions.InvalidCommandException;
import Exceptions.InvalidRecipientException;
import Exceptions.NotLoggedInException;
import chatappv2.Commands.Command;
import chatappv2.Commands.DirectMessage;
import chatappv2.Commands.DirectMessageCommand;
import chatappv2.Commands.HistoryCommand;
import chatappv2.Commands.ListCommand;
import chatappv2.Commands.LoginCommand;
import chatappv2.Commands.LogoutCommand;
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
        HeartBeat hb = new HeartBeat();
        commands.put("/register", new RegisterCommand(service));
        commands.put("/login", new LoginCommand(service));
        commands.put("/list", new ListCommand(service));
        commands.put("/w", new DirectMessageCommand(service));
        commands.put("/whisper", new DirectMessage(service));
        commands.put("/history", new HistoryCommand(service));
        commands.put("/logout", new LogoutCommand(service));
        try(DatagramSocket ds = new DatagramSocket(2000)){
            byte[] b = new byte[1024];
            
            Thread thread = new Thread(() ->{ 
                try {
                    while(true){
                        DatagramPacket dp = new DatagramPacket(b, b.length);
                        ds.receive(dp);
                        int userPort = dp.getPort();
                        hb.updateHeartBeat(userPort, System.currentTimeMillis());
                        hb.updateDps(userPort, dp);
                        executeCommand(ds, dp, service);
                        
                        //service.identifyCommand(ds, dp);
                    }
                } catch (IOException ex) {
                    System.getLogger(ChatAppv2.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
            });
            thread.start();
            
            Thread hbThread = new Thread(() ->{
               while(true){
                   long currentTime = System.currentTimeMillis();
                   hb.getHeartBeat().entrySet().stream()
                           .filter(entry -> currentTime - entry.getValue() > 30_000)
                           .map(entry -> entry.getKey())
                           .forEach(port ->{
                               DatagramPacket dp = hb.getDps().get(port);
                               service.sendToSender(ds, dp, "Server: You are being logged out automatically due to being inactive for 5 mins.");
                               service.logout(ds, dp);
                               hb.removeHeartBeatPort(dp.getPort());
                               hb.removeDpsPort(dp.getPort());
                           });
                   try {
                       Thread.sleep(10_000);
                   } catch (InterruptedException ex) {
                       System.getLogger(ChatAppv2.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                   }
               } 
            });
            hbThread.start();
            
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
            try {
                command.execute(ds, dp);
        } catch (InvalidCommandException | InvalidRecipientException | NotLoggedInException ex) {
            service.sendToSender(ds, dp, ex.getMessage());
        }
        else
            service.sendToSender(ds, dp, "Invalid command");
    }
    
}
