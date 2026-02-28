
package chatappv2.Commands;

import Exceptions.InvalidCommandException;
import Exceptions.InvalidRecipientException;
import Exceptions.NotLoggedInException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public interface Command {
    void execute(DatagramSocket ds, DatagramPacket dp) throws InvalidCommandException, InvalidRecipientException, NotLoggedInException;
}
