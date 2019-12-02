package Server;


import MainApp.MyPrintStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Packet implements Serializable
{
    public static MyPrintStream out;
    
    
    private DatagramPacket dp = null;
    
    //kathysterisi
     public static  boolean delay;
     public static int i_delay = 1;
    
    // Stoixeia parallipti
    private static DatagramSocket socket = null;
    private static InetAddress address = null;
    private static int port = 0;
    
    private static byte[] receivedData, sentData;
    private static int payload = 32;        // default
    
    private static final int header_size = 3;      // 3 bytes
    
    //char mode;
    private static byte packetNumber = 0, ACKNumber = 0;
    
    static private byte expectedPacket = 0;
    
    int lastPackLength = 0;
    
    enum packetTypes {DATAGRAM, ACKNOWLEDGMENENT};
    packetTypes type;
    
    
    public Packet(DatagramSocket socket) {
        Packet.socket = socket;
        this.type = packetTypes.ACKNOWLEDGMENENT;
        
        receivedData = new byte[header_size+payload];         // +3 for header
        sentData = new byte[header_size+payload];         // +3 for header
    }
    
    public Packet(InetAddress address, int port){
        Packet.address = address;
        Packet.port = port;
        
        receivedData = new byte[header_size+payload];
        sentData = new byte[header_size+payload];         // +3 for header
        
        this.type = packetTypes.DATAGRAM;
        
        try {
            // Dimiourgia kenou socket
            socket = new DatagramSocket( );
        } catch (SocketException ex) {
            Logger.getLogger(Packet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendACK() {
        // Edw prepei na allazei to akcnumber
        type= packetTypes.ACKNOWLEDGMENENT;
        
        this.setPacketData(null);       // Prwta dedomena
        this.addHeader();               // Prin to steilei vazei ta header
        
        if(delay) {
            try {
                Thread.sleep(i_delay*4L,i_delay*2);
                i_delay++;
            } catch (InterruptedException ex) {
                Logger.getLogger(Packet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        dp = new DatagramPacket(sentData, sentData.length, address, port);
        try {
            socket.send(dp);
            out.println("Acknowledgement " + this.getACKNumber() + " successfully sent.");
        } catch (IOException ex) {
            Logger.getLogger(Packet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void receivePacket() {
        try {
            boolean flag = false;
            do { //implementing Stop and Wait
                receivedData = new byte[payload];
                dp = new DatagramPacket(receivedData, receivedData.length);
                socket.receive(dp);
                this.removeHeader();
                if( type==packetTypes.DATAGRAM && expectedPacket == packetNumber) 
                    flag = true;
                out.println("Packet " + this.getPacketNumber() + " successfully received.");
                this.sendACK();
                
            } while(!flag);
            
        } catch (IOException ex) {}
        
        this.expectedPacket = (this.packetNumber == 0) ? (byte)1 : (byte)0;
        this.packetNumber = (this.packetNumber == 0) ? (byte)1 : (byte)0;
        this.ACKNumber = (this.ACKNumber == 0) ? (byte)1 : (byte)0;
    }
    
    public void sendPacket(byte [] b, int l) {
        byte [] newBee = new byte[l];
        
        for (int i = 0; i < l; i++) {
            newBee[i] = b[i];
        }
        
        sendPacket(newBee);
    }
    
    public void sendPacket(byte [] b) {
        while(true) {
            try {
                //if(type == packetTypes.DATAGRAM)      // den xreiazetai giati ta ACK ta stelnei h sendACK
                    if(delay) socket.setSoTimeout((int) Math.floor(i_delay++ * 8.4 * 1000));
                
                type = packetTypes.DATAGRAM;

                this.setPacketData(b);
                this.addHeader();
                socket.send(dp);
                out.println("Packet " + this.getPacketNumber() + " successfully sent.");
                
                // if there was a reset, rstore packnum
                if( packetNumber == -1 )
                    packetNumber = 0;

                // Perimenw ack
                do{
                    dp = new DatagramPacket(receivedData, receivedData.length, address, port);
                    socket.receive(dp);
                    out.println("Acknowledgement " + getAN(dp) + " successfully received.");
                } while(  (!isACK(dp) || getAN(dp) != ACKNumber ));

                break;
            } catch (IOException ex) {}
        }
        this.expectedPacket = (this.packetNumber == 0) ? (byte)1 : (byte)0;
        this.packetNumber = (this.packetNumber == 0) ? (byte)1 : (byte)0;
        this.ACKNumber = (this.ACKNumber == 0) ? (byte)1 : (byte)0;
    }
    
    private void addHeader() {
        sentData[0] = (byte)packetNumber;
        sentData[1] = (byte)ACKNumber;
        sentData[2] = (byte)type.ordinal();
        dp = new DatagramPacket(sentData, sentData.length, address, port);
    }
    
    private void setPacketData(byte [] b){
        
        if(b != null){
            // allazei to megethos toy data analogws to b
            sentData = new byte[header_size + b.length];
        
            // Meta to header antigrafei
            for (int i = 0; i < b.length; i++) {
                sentData[header_size + i] = b[i];
            }
        }
        else {      // an einai null tote krataei xwro mono gia to header
            sentData = new byte[header_size];
        }
    }
    
    private void removeHeader() {
        byte[] b = dp.getData();
        this.packetNumber = b[0];
        this.ACKNumber = b[1];
        this.type = b[2] == 0 ? packetTypes.DATAGRAM : packetTypes.ACKNOWLEDGMENENT;
        
        lastPackLength = dp.getLength() - header_size;
        
        // enimerwnei kai tin dieuthinsi+port
        address = dp.getAddress();
        port = dp.getPort();
        
        // reset connection
        if(packetNumber == -1){
            this.expectedPacket = 0;
            this.packetNumber = 0;
            this.ACKNumber = 0;
        }
        
        //ByteBuffer data = ByteBuffer.wrap(b, 2, b.length - 2);
        
        // An einai DATAgram tote mono antigrafei to data
        //type == packetTypes.DATAGRAM
        {
            receivedData = new byte[b.length];
            for (int i = 0; i < b.length; i++) {
                receivedData[i] = b[i];
            }
        }
        
    }
    
    protected byte[] getActualData(){
        byte [] b = new byte[ lastPackLength];
        for (int i = 0; i < b.length; i++) {
            b[i] = receivedData[i+header_size];
        }
        return b;
    }
    
    public void setPacketNumber(byte packetNumber) {
        this.packetNumber = packetNumber;
    }
    
    public byte getPacketNumber() {
        return packetNumber;
    }
    
    public void setExpectedNumber(byte expectedPacket) {
        this.expectedPacket = expectedPacket;
    }
    
    public byte getExpectedNumber() {
        return expectedPacket;
    }
    
    public void setACKNumber(byte ACKNumber) {
        this.ACKNumber = ACKNumber;
    }
    
    public byte getACKNumber() {
        return ACKNumber;
    }
    
    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }
    
    public static boolean isACK(DatagramPacket d){
        
        byte [] b = d.getData();
        
        return b[2] == 1;   
    }
    
    public static int getPN(DatagramPacket d){
        
        byte [] b = d.getData();
        
        return b[0];   
    }
    
    public static int getAN(DatagramPacket d){
        
        byte [] b = d.getData();
        
        return b[1];   
    }

    public static void setPayload(int payload) {
        
        if(payload >= 1 && payload <= 65500)
            Packet.payload = payload+header_size;
    }
    
    public void reset(){
                        
        this.expectedPacket = 0;
        this.packetNumber = -1;
        this.ACKNumber = 0;
    }

    public int getLastPackLength() {
        return lastPackLength;
    }
    
    
}
