
package chatappv2.Commands;

import chatappv2.Service;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ListCommand implements Command {

    private Service service;
    public ListCommand(Service service){
        this.service = service;
    }
    public void execute(DatagramSocket ds, DatagramPacket dp) {
        String username = service.getUsername(dp.getPort());
        service.sendToSender(ds, dp, service.getAllUsers(username));
    }
    
}
