
package chatappv2;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Repository {
    private static Map<String, InetSocketAddress> activeUsers = new ConcurrentHashMap<>();
    private static Map<Integer, String> chattingWith = new ConcurrentHashMap<>();
    private static Map<Integer, String> activePortAndUsernames = new ConcurrentHashMap<>();
    private static final int serverPort = 2000;
    
    
    public void addActiveUsers(String username, InetSocketAddress address){
        activeUsers.put(username, address);
    }
    
    public void removeUser(String username, int port){
        activeUsers.remove(username);
        chattingWith.remove(port);
        System.out.println(username + " logged out!");
    }
    
    public void setChattingWith(int port, String username){
        chattingWith.put(port, username);
    }
    
    public void addActivePortAndUsernames(int port, String username){
        activePortAndUsernames.put(port, username);
    }
    //=======================GETTER===============================
    
    public String getRecipientUsername(int port){
        return chattingWith.get(port);
    }
    public String getUsername(int port){
        return activePortAndUsernames.getOrDefault(port, "Port " + port);
    }
    public InetSocketAddress getAddressPort(String username){
        return activeUsers.get(username);
    }
    
    public String getChattingWith(int port){
        
        return chattingWith.containsKey(port) ? chattingWith.get(port) : "";
    }
    
    public int getServerPort(){
        return serverPort;
    }
    
    public Map<Integer, String> getActivePortAndUsernames(){
        return activePortAndUsernames;
    }
    
    public String getAllUsers(String username){
        Map<String, InetSocketAddress> availableUsers = new HashMap<>(activeUsers);
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
