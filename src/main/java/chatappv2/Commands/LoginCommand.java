
package chatappv2.Commands;

import chatappv2.Service;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import org.json.JSONObject;


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
        JSONObject msgJSON = new JSONObject(msg);
        String username = msgJSON.getString("username");
        String password = msgJSON.getString("password");
        service.login(username, password, ds, dp);
    }
}
