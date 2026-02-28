/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chatappv2.Commands;

import chatappv2.Service;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 *
 * @author itovin
 */
public class DirectMessage implements Command {
    private Service service;
    public DirectMessage(Service service){
        this.service = service;
    }
    
    public void execute(DatagramSocket ds, DatagramPacket dp){
        int userPort = dp.getPort();
        if(service.isPortLoggedIn(userPort)){
            if(!service.getChattingWith(dp.getPort()).equals(""))
                service.sendToRecipient(ds, dp);
            else
                service.sendToSender(ds, dp, "No one hears you. You have not selected a recipient. Use \"/w\" command to select recipient");
        }
        else
            service.sendToSender(ds, dp, "No one hears you. You are not logged in. Please login first using \"/login\" command.");
    }
}
