
package chatappv2.Commands;

import chatappv2.Service;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class LoginCommand implements Command {
    private Service service;
    public LoginCommand(Service service){
        this.service = service;
    }
    
    public void execute(DatagramSocket ds, DatagramPacket dp){
        if(service.isPortLoggedIn(dp.getPort())){
            service.sendToSender(ds, dp, "You are already logged in. Cannot use \"/login\" command.");
            return;
        }
        String msg = new String(dp.getData(), 0, dp.getLength());
        String[] split = msg.split(" ");
        if(split.length < 2){
            service.sendToSender(ds, dp, "Please include your username");
            return;
        }
        if(split.length > 3){
            service.sendToSender(ds, dp, "Username can only have 1 word only");
            return;
        }
        String username = split[1];
        String password = split[2];
        service.login(username, password, ds, dp);
    }
}
