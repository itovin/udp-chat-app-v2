
package chatappv2.Commands;

import Exceptions.InvalidCommandException;
import chatappv2.Service;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import org.json.JSONObject;


public class HistoryCommand implements Command {
    Service service;
    public HistoryCommand(Service service){
        this.service = service;
    }
    
    public void execute(DatagramSocket ds, DatagramPacket dp) throws InvalidCommandException{
        String msg = new String(dp.getData(), 0, dp.getLength());
        JSONObject msgJSON = new JSONObject(msg);
        String historyWith = msgJSON.optString("message");
        if(historyWith.equals(""))
        {
            throw new InvalidCommandException("Invalid use of \"/history\" command. Command must be followed by username");
        }
        String username = service.getUsername(dp.getPort());
        int userID = service.getUserIdByUsername(username);
        int receiverID = service.getUserIdByUsername(historyWith);
        service.sendToSender(ds, dp, service.getHistory(userID, receiverID));
    }
}
