
package chatappv2;

import java.io.IOException;
import java.util.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

class Repository{
    private static Map<String, InetSocketAddress> users = new ConcurrentHashMap<>();
    private static Map<Integer, String> chattingWith = new ConcurrentHashMap<>();
    private static Map<Integer, String> usernames = new ConcurrentHashMap<>();
    private static final int serverPort = 2000;
    
    
    public void addUser(String username, InetSocketAddress address){
        users.put(username, address);
    }
    
    public void removeUser(String username, int port){
        users.remove(username);
        chattingWith.remove(port);
        System.out.println(username + " logged out!");
    }
    
    public void setChattingWith(int port, String username){
        chattingWith.put(port, username);
    }
    
    public void addUsername(int port, String username){
        usernames.put(port, username);
    }
    //=======================GETTER===============================
    
    public String getRecipientUsername(int port){
        return chattingWith.get(port);
    }
    public String getUsername(int port){
        return usernames.getOrDefault(port, "Port " + port);
    }
    public InetSocketAddress getAddressPort(String username){
        return users.get(username);
    }
    
    public String getChattingWith(int port){
        
        return chattingWith.containsKey(port) ? chattingWith.get(port) : "";
    }
    
    public int getServerPort(){
        return serverPort;
    }
    
    public String getAllUsers(String username){
        Map<String, InetSocketAddress> availableUsers = new HashMap<>(users);
        availableUsers.remove(username);
        if(availableUsers.isEmpty())
            return "No active user yet";
        StringBuilder allUsers = new StringBuilder("=============Active Users============");
        for(Map.Entry<String, InetSocketAddress> entry : availableUsers.entrySet()){
            allUsers.append("\n" + entry.getKey() + " | " + entry.getValue().getAddress() + " | " + entry.getValue().getPort());
        }
        return allUsers.toString();
    }
    
}

//========================================SERVICE================================================

class Service{
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
    public void messageFromClient(DatagramSocket ds, DatagramPacket dp){
        System.out.println(getUsername(dp.getPort()) + ": " + new String(dp.getData(), 0, dp.getLength()));
    }
    
    public void sendToRecipient(DatagramSocket ds, DatagramPacket dp){
        String msg = getUsername(dp.getPort()) + ": " + new String(dp.getData(), 0, dp.getLength());
        InetSocketAddress address = getRecipient(getRecipientUsername(dp.getPort()));
        if(address != null){
            try {
                ds.send(new DatagramPacket(msg.getBytes(), msg.length(), address.getAddress(), address.getPort()));
            } catch (IOException ex) {
                System.getLogger(Service.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }else
            sendToSender(ds, dp, "Server: Recipient not found");
    }
    
    public void sendToSender(DatagramSocket ds, DatagramPacket dp, String msg){
        try {
            ds.send(new DatagramPacket(msg.getBytes(), msg.length(), dp.getAddress(), dp.getPort()));
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
            addUser(username, new InetSocketAddress(dp.getAddress(), dp.getPort()));
            addUsername(dp.getPort(), username);
            sendToSender(ds, dp, "You successfully registered as " + username + "!");
            System.out.println(username + " registered! | " + dp.getAddress() + " | " + dp.getPort());
        }
        else if(split[0].equals("/w")){
            if(getRecipient(extractUsername(split)) == null){
                sendToSender(ds, dp, "Failed to connect to " + extractUsername(split) + ". User does not exist");
            }
            else{
                setChattingWith(dp.getPort(), extractUsername(split));
                System.out.println(getUsername(dp.getPort()) + " connected to " + getRecipientUsername(dp.getPort()));
                sendToSender(ds, dp, "You successfully connected to " + getRecipientUsername(dp.getPort()));
            }
        }
    }

    public String extractUsername(String[] split) {
        StringBuilder username = new StringBuilder(split[1]);
        for(int i = 2; i < split.length; i++)
            username.append(" ").append(split[i]);
        return username.toString();
    }
}
//====================================MAIN================================
public class ChatAppv2 {

    private static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        Repository repo = new Repository();
        Service service = new Service(repo);
        try(DatagramSocket ds = new DatagramSocket(service.getServerPort())){
            byte[] b = new byte[1024];
            
            Thread thread = new Thread(() ->{ 
                try {
                    while(true){
                        DatagramPacket dp = new DatagramPacket(b, b.length);
                        ds.receive(dp);
                        service.messageFromClient(ds, dp);
                        service.identifyCommand(ds, dp);
                    }
                } catch (IOException ex) {
                    System.getLogger(ChatAppv2.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
            });
            
            thread.start();
            System.out.println("================Server Started====================");
            
            while(true){
                String msg = scanner.nextLine();
                if(msg.equals("/list"))
                    System.out.println(service.getAllUsers(""));
                
            }
            
        } catch (SocketException ex) {
            System.out.println("Error connecting to the server");;
        }
    }
    
}

class User{
    
    private static final Scanner scanner = new Scanner(System.in);
    private static final int serverPort = 2000;
    public static void main(String[] args){
        try(DatagramSocket ds = new DatagramSocket()){
            byte[] b = new byte[1024];
            System.out.print("Enter your username: ");
            String username = scanner.nextLine();
            register(username, ds);
            DatagramPacket dp = new DatagramPacket(b, b.length);
            
            Thread thread = new Thread(() ->{
               while(true){
                   try {
                       ds.receive(dp);
                       System.out.println(new String(dp.getData(), 0, dp.getLength()));
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
    
    public static void register(String username, DatagramSocket ds){
        username = "/register " + username;
        try {
            ds.send(new DatagramPacket(username.getBytes(), username.length(), InetAddress.getByName("localhost"), serverPort));
        }catch (IOException ex) {
            System.getLogger(User.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
}
