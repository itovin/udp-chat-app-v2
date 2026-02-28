/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chatappv2.Commands;

import Exceptions.InvalidRecipientException;
import Exceptions.NotLoggedInException;
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
    
    public void execute(DatagramSocket ds, DatagramPacket dp) throws NotLoggedInException, InvalidRecipientException{
        int userPort = dp.getPort();
        if(!service.isPortLoggedIn(userPort))
            throw new NotLoggedInException("No one hears you. You are not logged in. Please login first using \"/login\" command.");
        if(service.getChattingWith(dp.getPort()).equals(""))
            throw new InvalidRecipientException("No one hears you. You have not selected a recipient. Use \"/w\" command to select recipient");
        service.sendToRecipient(ds, dp);
    }
}
