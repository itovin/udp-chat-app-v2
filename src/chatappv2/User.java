/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chatappv2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;


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
            
            
            System.out.println("Enter command \"/login [username]\" to start. No account yet? \"/register [username]\" to register.");
            
            while(true){
                String msg = scanner.nextLine();
                if(isloginOrRegisterCommand(msg)){
                    System.out.print("Please enter your password: ");
                    String pw = scanner.nextLine();
                    msg += " " + pw;
                    ds.send(new DatagramPacket(msg.getBytes(), msg.length(), InetAddress.getByName("localhost"), serverPort));
                    ds.receive(dp);
                    msg = new String(dp.getData(), 0, dp.getLength());
                    System.out.println(msg);
                    if(msg.equals("Server: Login successful!"))
                        break;
                }
            }
            
            Thread thread = new Thread(() ->{
               while(isRunning){
                   try {
                       ds.receive(dp);
                       String msg = new String(dp.getData(), 0, dp.getLength());
                       System.out.println(msg);
                   } catch (IOException ex) {
                       break;
                       //System.getLogger(User.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                   }
               } 
            });
            
            thread.start();
                      
            
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
        } catch (IOException ex) {
            System.getLogger(User.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    public static boolean isloginOrRegisterCommand(String msg){
        String[] split = msg.split(" ");
        if(split.length != 2){
            System.out.println("Please use command \"/login [username]\" to start. No account yet? Register using command \"/register[username]\" " );
            return false;
        }
        if(split[0].equals("/login") || split[0].equals("/register"))
            return true;
        
        return false;
    }
    
}
