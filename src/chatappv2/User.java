/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chatappv2;

import chatappv2.Exceptions.InvalidCommandException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

class Helper{
    private static final int serverPort = 2000;
    public static boolean isCommandCorrect(String command){
        if(command.equals("")){
            System.out.println("Invalid command!");
            System.out.println("Enter command \"/login [username]\" to start\nNo account yet? \"/register [username]\n to register.");
            return false;
        }
        String[] split = command.split(" ");
        if(split[0].equals("/register") || split[0].equals("/login")){
            if(split.length != 2)
                System.out.println("Username must be 1 word!");
            else{
                return true;
            }
        }else
            System.out.println("Invalid command");
        return false;
    }
}
public class User {
        
    private static final Scanner scanner = new Scanner(System.in);
    private static final int serverPort = 2000;
    private static boolean isConnected = false;
    private static boolean isRunning = true;
    public static void main(String[] args){
        try(DatagramSocket ds = new DatagramSocket()){
            byte[] b = new byte[1024];
            DatagramPacket dp = new DatagramPacket(b, b.length);
            String startCommand = "";
            
            Thread thread = new Thread(() ->{
               while(isRunning){
                   try {
                       ds.receive(dp);
                       String msg = new String(dp.getData(), 0, dp.getLength());
                       System.out.println(msg);
                       if(msg.equals("Connected"))
                           isConnected = true;
                   } catch (IOException ex) {
                       break;
                       //System.getLogger(User.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                   }
               } 
            });
            
            thread.start();
            
            System.out.println("Enter command \"/login [username]\" to start. No account yet? \"/register [username]\" to register.");
            while(!isConnected){
                startCommand = scanner.nextLine().toLowerCase().trim();
                if(Helper.isCommandCorrect(startCommand))
                {
                    System.out.print("Enter your password: ");
                    startCommand += " " + scanner.nextLine();
                    try {
                        ds.send(new DatagramPacket(startCommand.getBytes(), startCommand.length(), InetAddress.getByName("localhost"), serverPort));
                    } catch (IOException ex) {
                        System.getLogger(User.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                    }
                }
            }
                
            
            
            
            while(true){
                try {
                    String msg = scanner.nextLine();
                    
                    if(!msg.equals("")){
                        ds.send(new DatagramPacket(msg.getBytes(), msg.length(), InetAddress.getByName("localhost"), serverPort));
                        if(msg.equals("/end")){
                            Thread.sleep(1000);
                            ds.close();
                            System.out.println("Logged out successfully");
                            isRunning = false;
                            break;
                        }
                    }
                } catch (IOException ex) {
                    System.out.println("Unable to send to the sever. Servery may be offline");
                } catch (InterruptedException ex) {
                    System.getLogger(User.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
            }
        } catch (SocketException ex) {
            System.getLogger(User.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
}
