
package chatappv2.Commands;

import Exceptions.InvalidCommandException;
import chatappv2.Service;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import org.json.JSONObject;

public class LogoutCommand implements Command{
    private Service service;
    public LogoutCommand(Service service){
        this.service = service;
    }
    
    public void execute(DatagramSocket ds, DatagramPacket dp) throws InvalidCommandException{
        if(!service.isPortLoggedIn(dp.getPort())){
            throw new InvalidCommandException("Invalid use of /logout command. You are already logged out.");
        }
        service.logout(ds, dp);
    }
}
