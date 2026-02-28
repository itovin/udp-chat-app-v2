
package chatappv2.Commands;

import Exceptions.InvalidRecipientException;
import chatappv2.Service;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class DirectMessageCommand implements Command {
    private Service service;
    public DirectMessageCommand(Service service){
        this.service = service;
    }

    public void execute(DatagramSocket ds, DatagramPacket dp) throws InvalidRecipientException {
        service.selectReceiver(ds, dp);
    }
}
