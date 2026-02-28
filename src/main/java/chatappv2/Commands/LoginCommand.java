
package chatappv2.Commands;

import Exceptions.InvalidCommandException;
import Exceptions.InvalidCredentialsException;
import Exceptions.UserAlreadyLoggedInException;
import chatappv2.Service;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import org.json.JSONObject;


public class LoginCommand implements Command {
    private Service service;
    public LoginCommand(Service service){
        this.service = service;
    }
    
    public void execute(DatagramSocket ds, DatagramPacket dp) throws InvalidCommandException{
        if(service.isPortLoggedIn(dp.getPort())){
            throw new InvalidCommandException("You are already logged in. Cannot use \"/login\" command.");
        }
        String msg = new String(dp.getData(), 0, dp.getLength());
        JSONObject msgJSON = new JSONObject(msg);
        String username = msgJSON.getString("username");
        String password = msgJSON.getString("password");
        try {
            service.login(username, password, ds, dp);
        }catch (InvalidCredentialsException | UserAlreadyLoggedInException ex) {
            service.sendToSender(ds, dp, ex.getMessage());
        } 
    }
}