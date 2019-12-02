package Client;


import java.net.*;

public class HandshakePacket extends Packet {
    
    private byte[] b = new byte[2];
    
    
    public HandshakePacket (DatagramSocket socket, boolean SYN, boolean ACK) {
        
        super(socket);
        
        b[0] = SYN ? (byte)1:(byte)0;
        b[1] = ACK ? (byte)1:(byte)0;
        
    }
    // ------------------------------------------------------ OK
    
    public HandshakePacket(InetAddress address, int port, boolean SYN, boolean ACK) {
        super(address, port);
        
        b[0] = SYN ? (byte)1:(byte)0;
        b[1] = ACK ? (byte)1:(byte)0;
        
    }

    public void sendPacket(){
        
        super.sendPacket(b);
    }
    
    public boolean isSYN()
    {
        byte[] b = getActualData();
        return(b[0]==1 && b[1]==0);
    }
    
    public boolean isACK() {
        byte[] b = getActualData();
        
        if(b.length < 2)
            return false;
        
        return(b[0]==0 && b[1]==1);
    }
    
    public boolean isSYNACK() {
        byte[] b = getActualData();
        
        if(b.length < 2)
            return false;
        return(b[0]==1 && b[1]==1);
    }
}