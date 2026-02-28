
package chatappv2;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class HeartBeat{
    private static Map<Integer, Long> heartBeat = new ConcurrentHashMap<>();
    private static Map<Integer, DatagramPacket> dps = new ConcurrentHashMap<>();
   
    public void updateHeartBeat(int port, long time){
        heartBeat.put(port, time);
    }
    
    public void updateDps(int port, DatagramPacket dp){
        dps.put(port, dp);
    }
    
    public void removeHeartBeatPort(int port){
        heartBeat.remove(port);
    }
    
    public void removeDpsPort(int port){
        dps.remove(port);
    }
    
    public Map<Integer, Long> getHeartBeat(){
        return heartBeat;
    }
    
    public Map<Integer, DatagramPacket> getDps(){
        return dps;
    }
    
}
