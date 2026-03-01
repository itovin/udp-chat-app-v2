
package chatappv2;

import Exceptions.InvalidCredentialsException;
import Exceptions.InvalidRecipientException;
import Exceptions.UserAlreadyLoggedInException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;
import org.json.JSONObject;


public class Service {
    private final Repository repo;
    private final String LIST_CMD = "/list";
    private final String REGISTER_CMD = "/register";
    private final String LOGIN_CMD = "/login";
    private final String DM_CMD = "/w";
    private final String LOGOUT_CMD = "/logout";
    private final String HISTORY_CMD = "/history";
    
    public Service(Repository repo){
        this.repo = repo;
    }
    
    public void addActiveUsers(String username, InetSocketAddress address){
        repo.addActiveUsers(username, address);
    }
    
    public void removeUser(String username, int port){
        repo.removeUser(username, port);
    }
    
    public void setChattingWith(int port, String username){
        repo.setChattingWith(port, username);
    }
    
    public void addUsername(int port, String username){
        repo.addActivePortAndUsernames(port, username);
    }
    
   
    public void removeActiveUsers(String username) {
        repo.removeActiveUsers(username);
    }

    public void removeActivePortAndUsername(int userPort) {
        repo.removeActivePortAndUsername(userPort);
    }
    
    //=======================GETTER===============================
    
    public String getRecipientUsername(int port){
        return repo.getRecipientUsername(port);
    }
    public String getUsername(int port){
        return repo.getUsername(port);
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
        if(isPortLoggedIn(dp.getPort()))
            System.out.println(getUsername(dp.getPort()) + " to " + recipientUsername + ": " + new String(dp.getData(), 0, dp.getLength()));
    }
    
    public void sendToRecipient(DatagramSocket ds, DatagramPacket dp){
        String msg = new String(dp.getData(), 0, dp.getLength());
        JSONObject msgJSON = new JSONObject(msg);
        String message = msgJSON.getString("message");
        String senderUsername = getUsername(dp.getPort());
        message = senderUsername + ": " + message;
        String recipientUsername = getRecipientUsername(dp.getPort());
        InetSocketAddress recipientAddress = getRecipient(recipientUsername);
        if(recipientAddress != null){
            try {
                //displayMessageFromClient(ds, dp, recipientUsername);
                ds.send(new DatagramPacket(message.getBytes(), message.length(), recipientAddress.getAddress(), recipientAddress.getPort()));
                addMsgToDB(getUserIdByUsername(senderUsername), getUserIdByUsername(recipientUsername), message);
            } catch (IOException ex) {
                System.getLogger(Service.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }else{
            sendToSender(ds, dp, "Server: Failed to send. User is not online or does not exist.");
        }
    }
    
    public void sendToSender(DatagramSocket ds, DatagramPacket dp, String msg){
        try {
            ds.send(new DatagramPacket(msg.getBytes(), msg.length(), dp.getAddress(), dp.getPort()));
            //displayMessageFromClient(ds, dp, "Server");
        } catch (IOException ex) {
            System.getLogger(Service.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    
    
    //===============================HELPER===================================
    
    
    
    public int getUserIdByUsername(String username){
        try(Connection con = DriverManager.getConnection("jdbc:sqlite:chat.db");
                PreparedStatement stm = con.prepareStatement("select id from users where username = ?")){
            stm.setString(1, username);
            return stm.executeQuery().getInt(1);
        } catch (SQLException ex) {
            System.getLogger(Service.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return -1;
    }
    
    public void selectReceiver(DatagramSocket ds, DatagramPacket dp) throws InvalidRecipientException{
        String msg = new String(dp.getData(), 0, dp.getLength());
        JSONObject msgJSON = new JSONObject(msg);
        int userPort = dp.getPort();
        String username = getUsername(userPort);
        String receiverUsername = msgJSON.optString("message");
        if(username.equals(receiverUsername))
            throw new InvalidRecipientException("Invalid recipient. Cannot choose yourself as recipient");
        if(getRecipient(receiverUsername) == null){
            sendToSender(ds, dp, "Server: Failed to connect to " + receiverUsername + ". User does not exist or offline");
        }
        else{
            setChattingWith(dp.getPort(), receiverUsername);
            System.out.println(getUsername(dp.getPort()) + " connected to " + getRecipientUsername(dp.getPort()));
            sendToSender(ds, dp, "Server: You successfully connected to " + getRecipientUsername(dp.getPort()));
        }
    }
    
    public boolean isPortLoggedIn(int userPort){
        return repo.getActivePortAndUsernames().containsKey(userPort);
    }
    
    public boolean isPWMatched(String username, String password, DatagramSocket ds, DatagramPacket dp){
        try(Connection con  = DriverManager.getConnection("jdbc:sqlite:chat.db");
                PreparedStatement stm = con.prepareStatement("select password, status from users where username = ?")){
            stm.setString(1, username);
            ResultSet rs = stm.executeQuery();
            if(rs.next())
            {
                if(BCrypt.checkpw(password, rs.getString("password")))
                {
                    if(rs.getString("status").equals("online"))
                        sendToSender(ds, dp, "Server: Login failed. Account already logged in. Please logout from the other session first.");
                    else return true;
                }
            }
        } catch (SQLException ex) {
            System.getLogger(Service.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return false;
    }
    
    public boolean isRecipientOnline(String recipientUsername){
        return repo.isUserOnline(recipientUsername);
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
    
    public void register(String username, String password, DatagramSocket ds, DatagramPacket dp){
        password = BCrypt.hashpw(password, BCrypt.gensalt());
        try(Connection con = DriverManager.getConnection("jdbc:sqlite:chat.db");
                    PreparedStatement stm = con.prepareStatement("insert into users (username, password, status) values (?, ?, 'offline')")){
            stm.setString(1, username);
            stm.setString(2, password);
            stm.executeUpdate();
            sendToSender(ds, dp, "Successfully registered as " + username);
        } catch (SQLException ex) {
            sendToSender(ds, dp, "Registration failed. Username is already registered");
        }
    }
        
    public void login(String username, String password, DatagramSocket ds, DatagramPacket dp) throws UserAlreadyLoggedInException, InvalidCredentialsException{
        if(!isPWMatched(username, password, ds, dp))
            throw new InvalidCredentialsException("Server: Login failed. Invalid username or password");
        
        if(isDuplicateLogin(username))
            throw new UserAlreadyLoggedInException("User is already logged in. Please logout from the current session first.");
        sendToSender(ds, dp, "Server: Login successful!");
        sendToSender(ds, dp, "Connected!");
        addActiveUsers(username, new InetSocketAddress(dp.getAddress(), dp.getPort()));
        addUsername(dp.getPort(), username);
        System.out.println("Port " + dp.getPort() + " logged in as " + username);
        updateDBStatus(username, "online");
        
                    
    }
    
    public void addMsgToDB(int senderID, int receiverID, String msg){
        try(Connection con = DriverManager.getConnection("jdbc:sqlite:chat.db");
                    PreparedStatement stm = con.prepareStatement("insert into messages(sender_id, receiver_id, message) values(?, ?, ?)")){
            stm.setInt(1, senderID);
            stm.setInt(2, receiverID);
            stm.setString(3, msg);
            stm.executeUpdate();
        } catch (SQLException ex) {
            System.getLogger(Service.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    public String getHistory(int senderID, int receiverID){
        try(Connection con = DriverManager.getConnection("jdbc:sqlite:chat.db");
                    PreparedStatement stm = con.prepareStatement("select * from messages where (sender_id = ? and receiver_id = ?) Or (sender_id = ? and receiver_id = ?)")){
            stm.setInt(1, senderID);
            stm.setInt(2, receiverID);
            stm.setInt(3, receiverID);
            stm.setInt(4, senderID);
            ResultSet rs = stm.executeQuery();
            StringBuilder history = new StringBuilder("================History with " + getRecipientUsername(receiverID) + "=================");
            while(rs.next()){
                history.append("\n").append(rs.getDate("date_sent")).append("\n").append(rs.getString("message"));
            }
            history.append("\n").append("==================End of History================");
            return new String(history);
        } catch (SQLException ex) {
            System.getLogger(Service.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return "History not found!";
    }
    
    
    public void logout(DatagramSocket ds, DatagramPacket dp){
        int userPort = dp.getPort();
        String username = getUsername(dp.getPort());
        removeActivePortAndUsername(userPort);
        removeActiveUsers(username);
        updateDBStatus(username, "offline");
        sendToSender(ds, dp, "Server: You logged out successfully!");
        System.out.println(username + " at port " + userPort + " logged out!");
    }

    
    public boolean isDuplicateLogin(String username){
        try(Connection con = DriverManager.getConnection("jdbc:sqlite:chat.db");
                PreparedStatement stm = con.prepareStatement("select status from users where username = ?")){
            stm.setString(1, username);
            ResultSet rs = stm.executeQuery();
            if(rs.getString("status").equals("online"))
                return true;
        } catch (SQLException ex) {
            System.getLogger(Service.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return false;
    }
    
    public void updateDBStatus(String username, String status){
        try(Connection con = DriverManager.getConnection("jdbc:sqlite:chat.db");
                PreparedStatement stm = con.prepareStatement("update users set status = ? where username = ?")){
            stm.setString(1, status);
            stm.setString(2, username);
            stm.executeUpdate();
        } catch (SQLException ex) {
            System.getLogger(Service.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    public void setAllUsersToOffline(){
        try(Connection con = DriverManager.getConnection("jdbc:sqlite:chat.db");
                Statement stm = con.createStatement()){
            stm.executeUpdate("update users set status='offline'");
            System.out.println("Setting status of all users to offline.");
        } catch (SQLException ex) {
            System.getLogger(Service.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
}
