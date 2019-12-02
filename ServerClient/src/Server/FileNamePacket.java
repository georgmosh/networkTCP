package Server;


import java.net.DatagramSocket;
import java.net.InetAddress;

public class FileNamePacket extends Packet {
    String filename;
    byte[] b;
    public FileNamePacket(String filename, InetAddress address, int port) {
        
        super(address, port);
        
        this.filename = filename;
        b = filename.getBytes();
        
        super.setPayload(256);
    }
    
    public FileNamePacket(DatagramSocket socket) {
        
        super(socket);
        super.setPayload(356);
    }
    
    public String getFileNameByServer() {
   
        super.receivePacket();

        byte[] b1 = super.getActualData();
        this.filename = new String(b1);
        
        return filename;
    }
    
    public void sendFileName(){
        super.sendPacket(b);
    }
}
