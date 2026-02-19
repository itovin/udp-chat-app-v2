
package chatappv2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Service {
    private final Repository repo;
    
    public Service(Repository repo){
        this.repo = repo;
    }
    
    public void addUser(String username, InetSocketAddress address){
        repo.addUser(username, address);
    }
    
    public void removeUser(String username, int port){
        repo.removeUser(username, port);
    }
    
    public void setChattingWith(int port, String username){
        repo.setChattingWith(port, username);
    }
    
    public void addUsername(int port, String username){
        repo.addUsername(port, username);
    }
    
    //=======================GETTER===============================
    
    public String getRecipientUsername(int port){
        return repo.getRecipientUsername(port);
    }
    public String getUsername(int port){
        return repo.getUsername(port);
    }
    
    public int getServerPort(){
        return repo.getServerPort();
    }
    
    public InetSocketAddress getRecipient(String username){
        return repo.getAddressPort(username);
    }
    
    
    public String getChattingWith(int port){
        return repo.getChattingWith(port);
    }
    
    public String getAllUsers(String username){
        return repo.getAllUsers(username);
    }
    
    //===========================SENDING/RECEIVING===================
    public void displayMessageFromClient(DatagramSocket ds, DatagramPacket dp, String recipientUsername){
        System.out.println(getUsername(dp.getPort()) + " to " + recipientUsername + ": " + new String(dp.getData(), 0, dp.getLength()));
    }
    
    public void sendToRecipient(DatagramSocket ds, DatagramPacket dp){
        String msg = getUsername(dp.getPort()) + ": " + new String(dp.getData(), 0, dp.getLength());
        String recipientUsername = getRecipientUsername(dp.getPort());
        InetSocketAddress recipientAddress = getRecipient(recipientUsername);
        if(recipientAddress != null){
            try {
                displayMessageFromClient(ds, dp, recipientUsername);
                ds.send(new DatagramPacket(msg.getBytes(), msg.length(), recipientAddress.getAddress(), recipientAddress.getPort()));
            } catch (IOException ex) {
                System.getLogger(Service.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }else{
            sendToSender(ds, dp, "Server: Recipient not found");
        }
    }
    
    public void sendToSender(DatagramSocket ds, DatagramPacket dp, String msg){
        try {
            ds.send(new DatagramPacket(msg.getBytes(), msg.length(), dp.getAddress(), dp.getPort()));
            displayMessageFromClient(ds, dp, "Server");
        } catch (IOException ex) {
            System.getLogger(Service.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    
    
    //===============================HELPER===================================
    
    
    
    public void identifyCommand(DatagramSocket ds, DatagramPacket dp){
        String msg = new String(dp.getData(), 0, dp.getLength());
        if(msg.charAt(0) != '/'){
            if(getChattingWith(dp.getPort()).equals(""))
                sendToSender(ds, dp, "Server: No one hears you");
            else
                sendToRecipient(ds, dp);
            return;
        }
        if(msg.equals("/list")){
            sendToSender(ds, dp, getAllUsers(getUsername(dp.getPort())));
            return;
        }
        if(msg.equals("/end")){
            removeUser(getUsername(dp.getPort()), dp.getPort());
            return;
        }
        
        String[] split = msg.split(" ");
        if(split.length < 2){
            sendToSender(ds, dp, "Server: Invalid/Incomplete Command");
            return;
        }
        if(split[0].equals("/register")){
            String username = extractUsername(split);
            String password = extractPassword(split);
            if(isUsernameAvailable(username)){
                register(username, password);
                sendToSender(ds, dp, "Successfully registered as " + username);
            }else
                sendToSender(ds, dp, "Server: Username " + username + " is not avaiable!");
        }
        else if(split[0].equals("/login")){
            String username = extractUsername(split);
            String password = extractPassword(split);
            if(isLoginSuccessful(username, password, ds, dp)){
                addUser(username, new InetSocketAddress(dp.getAddress(), dp.getPort()));
                addUsername(dp.getPort(), username);
                sendToSender(ds, dp, "Server: You successfully registered as " + username + "!");
                System.out.println(username + " registered! | " + dp.getAddress() + " | " + dp.getPort());
            }
        }
        else if(split[0].equals("/w")){
            if(getRecipient(extractUsername(split)) == null){
                sendToSender(ds, dp, "Server: Failed to connect to " + extractUsername(split) + ". User does not exist or offline");
            }
            else{
                setChattingWith(dp.getPort(), extractUsername(split));
                System.out.println(getUsername(dp.getPort()) + " connected to " + getRecipientUsername(dp.getPort()));
                sendToSender(ds, dp, "Server: You successfully connected to " + getRecipientUsername(dp.getPort()));
            }
        }
    }

    public String extractUsername(String[] split) {
        return split[1];
    }
    public String extractPassword(String[] split){
        return split[2];
    }
    
    //==================================DATABASE COMMANDS================================
    public boolean isUsernameAvailable(String username){
        try(Connection con = DriverManager.getConnection("jdbc:sqlite:chat.db");
                    PreparedStatement stm = con.prepareStatement("select username from users where username = ?")){
            stm.setString(1, username);
            ResultSet rs = stm.executeQuery();
            if(rs.next()){
                return false;
            }
            return true;
        
        } catch (SQLException ex) {
            System.out.println("Error connected to db");
            System.getLogger(Service.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
         }
        return true;
    }
    
    public void register(String username, String password){
        try(Connection con = DriverManager.getConnection("jdbc:sqlite:chat.db");
                    PreparedStatement stm = con.prepareStatement("insert into users (username, password, status) values (?, ?, 'offline')")){
            stm.setString(1, username);
            stm.setString(2, password);
            stm.executeUpdate();
        } catch (SQLException ex) {
             System.getLogger(Service.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
         }
    }
    
    public boolean isLoginSuccessful(String username, String password, DatagramSocket ds, DatagramPacket dp){
        try(Connection con = DriverManager.getConnection("jdbc:sqlite:chat.db");
                    PreparedStatement stm = con.prepareStatement("select status from users where username = ? and password = ?")){
                stm.setString(1, username);
                stm.setString(2, password);
                ResultSet rs = stm.executeQuery();
                if(rs.next()){
                    if(rs.getString(1).equals("offline")){
                        sendToSender(ds, dp, "Server: Login successful!");
                        sendToSender(ds, dp, "Connected");
                        return true;
                    }
                    else
                        sendToSender(ds, dp, "Server: Login failed. " + username + " already logged in a different session. Please logout from existing session first");
                }else
                    sendToSender(ds, dp, "Server: Login failed. Invalid username or password");
                    
        } catch (SQLException ex) {
             System.getLogger(Service.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
         }
        return false;
    }
}
