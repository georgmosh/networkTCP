package Server;


import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;


public class PayloadEstabPacket extends Packet {
    int maxPayload;
    
    byte[] b;
    
    public PayloadEstabPacket(int maxPayload, InetAddress address, int port) {
        
        super(address, port);
        
        this.maxPayload = maxPayload;
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putInt(maxPayload);
        b = buf.array();
    }
    
    public PayloadEstabPacket(DatagramSocket socket) {
        
        super(socket);
    }
    
    public int getMaxPayloadByServer() {
     
        super.receivePacket();

        byte[] b = super.getActualData();
        ByteBuffer wrapped = ByteBuffer.wrap(b); // big-endian by default
        this.maxPayload = wrapped.getInt();        
     
        return maxPayload;
    }
    

    
    public void setMaxPayload(int maxPayload) {
        this.maxPayload = maxPayload;
    }
    
    public void sendPayload(){
        super.sendPacket(b);
    }

}
