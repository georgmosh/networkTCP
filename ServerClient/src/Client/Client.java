package Client;


import MainApp.MyPrintStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;


public class Client {
    int port, maxPayload, packetRate;
    float dataTransferRate;
    long dataTransferTime;
    String IP, filename;
    InetAddress address;
    
    MyPrintStream out;

    public Client(int port, String IP, String filename, int maxPayload, boolean delay, Object output) throws IllegalArgumentException {
        this.port = port;
        this.IP = IP;
        this.filename = filename;
        Packet.delay = delay;
        
        out = new MyPrintStream( output );
        Packet.out = this.out;
        
        if(maxPayload >= 0 && maxPayload <= 65500) this.maxPayload = maxPayload;
            else throw new IllegalArgumentException("Maximum payload out of range.");
        try {
            this.address = InetAddress.getByName(IP);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void startClient() {
        if(!this.handshake()) {
            System.err.println("Handshake failed to accomplish.");
            return;
        }  
        PayloadEstabPacket pest = new PayloadEstabPacket(maxPayload, address, port);
        pest.sendPayload();
        
        FileNamePacket pfn = new FileNamePacket(this.basename(filename), address, port);
        pfn.sendFileName();
        
        long startTime = System.nanoTime();
        Vec2<Long, Integer> stats = this.transferFileName(address, port, maxPayload, filename);
        long endTime = System.nanoTime();
        dataTransferTime = (endTime - startTime)/1000000;
        dataTransferRate = (stats.getTValue()/1024.0f)/(dataTransferTime/1000.0f);
        packetRate = stats.getYValue();
        out.println("File transfer completed sucessfully.");
        this.getSuccResults();
    }
    
    public void getSuccResults() {
        out.println(dataTransferRate + " KByte/sec successfully transmited"
            + " within " + dataTransferTime + " ms. for an amount of " + packetRate +
            " UDP/IP packets and a payload that equals " + maxPayload + ".");
    }
    
    public boolean handshake() {
        HandshakePacket handshakePacket = new HandshakePacket(address, port, true, false);
        handshakePacket.reset();

        handshakePacket.sendPacket();
        out.println("Client sent SYN packet.");
        handshakePacket.receivePacket();

        if(handshakePacket.isSYNACK()){
            handshakePacket = new HandshakePacket(address, port, false, true);
            out.println("Client received SYNACK packet.");
        }
        else
            return false;

        handshakePacket.sendPacket();
        out.println("Client sent ACK packet.");
        return true;
    }
    
    public Vec2<Long, Integer> transferFileName(InetAddress address, int port, int payload, String filename) {
        InputStream dataFlow = null;
        long filesize = 0;
        int i=0;
        try {
            dataFlow = new FileInputStream(new File(filename));
            byte[] buffer = new byte[payload];
            Packet p = new Packet(address, port);
            Packet.setPayload(payload);
            i=0;
            int partlength;
            while((partlength=dataFlow.read(buffer)) > 0) {
                p.sendPacket(buffer, partlength);
                filesize += partlength;
                out.println("Client sent "+partlength + " packet no "+(++i));
            }
            p.sendPacket(null);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {
             try {
                //file closure should execute in any case
                dataFlow.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
        return new Vec2<Long, Integer>(filesize, i);
    }
    
      public static String basename(String fullname){
        
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] tokens = fullname.split(pattern);
        
        return tokens[ tokens.length-1 ];
    }
    
    public static void run(int ans1, String ans2, String ans3, int ans4, boolean b, Object out){
            Client c = new Client(ans1, ans2, ans3, ans4, b, out);
            c.startClient();
    }
}
