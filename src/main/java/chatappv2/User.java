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
import java.util.Scanner;
import org.json.JSONObject;


public class User {
        
    private static final Scanner scanner = new Scanner(System.in);
    private static final int serverPort = 2000;
    private static String username;
    private static String command;
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
                    byte[] msgByte = JSONToByteArr(credentialsJSON(command, username, pw));
                    ds.send(new DatagramPacket(msgByte, msgByte.length, InetAddress.getByName("localhost"), serverPort));
                    ds.receive(dp);
                    msg = new String(dp.getData(), 0, dp.getLength());
                    System.out.println(msg);
                    if(msg.equals("Server: Login successful!"))
                        break;
                }
            }
            
            Thread thread = new Thread(() ->{
               while(true){
                   try {
                       ds.receive(dp);
                       String msg = new String(dp.getData(), 0, dp.getLength());
                       System.out.println(msg);
                       if(msg.equals("Server: You logged out successfully!"))
                           System.exit(0);
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
                        byte[] msgByte = JSONToByteArr(msgJSON(msg));
                        ds.send(new DatagramPacket(msgByte, msgByte.length, InetAddress.getByName("localhost"), serverPort));
                        if(msg.equals("/logout")){
                            Thread.sleep(1000);
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
        if(split[0].equals("/login") || split[0].equals("/register")){
            splitCommandAndUsername(msg);
            return true;
        }
        System.out.println("Please use command \"/login [username]\" to start. No account yet? Register using command \"/register[username]\" " );
        return false;
    }
    
    public static JSONObject credentialsJSON(String command, String username, String password){
        JSONObject creds = new JSONObject();
        creds.put("command", command);
        creds.put("username", username);
        creds.put("password", password);
        return creds;
    }
    
    public static JSONObject msgJSON(String msg){
        JSONObject msgJSON = new JSONObject();
        if(msg.charAt(0) == '/'){
            String[] split = msg.split(" ");
            msgJSON.put("command", split[0]);
            
            if(split.length > 1){
                StringBuilder sb = new StringBuilder(split[1]);
                for(int i = 2; i < split.length; i++){
                    sb.append(" ").append(split[i]);
                }
                msgJSON.put("message", sb);
            }
        }else
            msgJSON.put("message", msg);
        return msgJSON;
    }
    
    public static byte[] JSONToByteArr(JSONObject json){
        return json.toString().getBytes();
    }
    
    public static void splitCommandAndMessage(String msg){
        String[] split = msg.split(" ");
        command = split[0];
        username = split[1];
        
    }
    public static void splitCommandAndUsername(String msg){
        String[] split = msg.split(" ");
        command = split[0];
        username = split[1];
    }
    
}
