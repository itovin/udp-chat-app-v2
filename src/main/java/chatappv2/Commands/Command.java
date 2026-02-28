
package chatappv2.Commands;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public interface Command {
    void execute(DatagramSocket ds, DatagramPacket dp);
}
