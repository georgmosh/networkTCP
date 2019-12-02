package Client;


import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class FileNamePacket extends Packet {
    String filename;
    byte[] b;
    public FileNamePacket(String filename, InetAddress address, int port) {
        
        super(address, port);
        
        this.filename = filename;
        b = filename.getBytes();
        
        super.setPayload(b.length);
    }
    
    public FileNamePacket(DatagramSocket socket) {
        
        super(socket);
        super.setPayload(256);
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
