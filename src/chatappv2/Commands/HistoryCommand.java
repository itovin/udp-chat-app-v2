
package chatappv2.Commands;

import chatappv2.Service;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class HistoryCommand implements Command {
    Service service;
    public HistoryCommand(Service service){
        this.service = service;
    }
    
    public void execute(DatagramSocket ds, DatagramPacket dp){
        String msg = new String(dp.getData(), 0, dp.getLength());
        String[] split = msg.split(" ");
        if(split.length != 2)
        {
            service.sendToSender(ds, dp, "Invalid use of \"/history\" command. Command must be followed by username");
            return;
        }
        String username = service.getUsername(dp.getPort());
        String receiverUsername = split[1];
        int userID = service.getUserIdByUsername(username);
        int receiverID = service.getUserIdByUsername(receiverUsername);
        service.sendToSender(ds, dp, service.getHistory(userID, receiverID));
    }
}
