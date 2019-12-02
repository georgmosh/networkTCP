package Server;


import MainApp.MyPrintStream;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server
{
	int port;
	String IP;
        MyPrintStream out;
	
	public Server(int port, String IP, boolean delay, Object out) {
            this.port = port;
            this.IP = IP;
            Packet.delay = delay;
            
            this.out = new MyPrintStream(out);
            Packet.out = this.out;
	}
	
	public void startServer() {
		DatagramSocket serverSocket = null;	
            try {
                serverSocket = new DatagramSocket(this.port);
            } catch (SocketException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            while(true) {
                if(!this.handshake(serverSocket)) continue;
                int payload = this.establishPayload(serverSocket);
                String filename = this.establishFilename(serverSocket);
                out.println("Transfering " + filename + " with payload set to " + payload);
                this.transferFileName(serverSocket, payload, filename);
                out.println("File transfer completed sucessfully.");
            }
	}

	public boolean handshake(DatagramSocket serverSocket) {
            
            HandshakePacket handshakePacket = new HandshakePacket(serverSocket, false, false);
            handshakePacket.receivePacket();
            
            if(handshakePacket.isSYN()){
                handshakePacket = new HandshakePacket(serverSocket, true, true);
                out.println("Server received SYN packet.");
            }
            else
                return false;
            
            handshakePacket.sendPacket();
            out.println("Server sent SYNACK packet.");
            
            handshakePacket.receivePacket();
            if(handshakePacket.isACK()){
                out.println("Server received ACK packet.");
                return true;
            }
            else
                return false;
	}
        
        public int establishPayload(DatagramSocket serverSocket)
        {
            PayloadEstabPacket payloadPacket = new PayloadEstabPacket(serverSocket);
            int payload = payloadPacket.getMaxPayloadByServer();
            return payload;
        }
        
        public String establishFilename(DatagramSocket serverSocket)
        {
            FileNamePacket filenamePacket = new FileNamePacket(serverSocket);
            String filename = filenamePacket.getFileNameByServer();
            return filename;
        }
        
        public boolean transferFileName(DatagramSocket serverSocket, int payload, String filename) {
            Packet p = new Packet(serverSocket);
            p.setPayload(payload);

            OutputStream dataFlow = null;
            try {
                dataFlow = new FileOutputStream(new File(filename));
                p.receivePacket();
                byte[] buffer = p.getActualData();
                int i=0;
                while (p.getLastPackLength() > 0) {
                    out.println("Server got "+p.getLastPackLength() + " packet no "+(++i));

                    dataFlow.write(buffer, 0, p.getLastPackLength());
                    p.receivePacket();
                    buffer = p.getActualData();
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } finally { 
                try {
                    //file closure should execute in any case
                    dataFlow.close();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }
            }
            return true;
        }
	
   public static void run(int ans1, String ans2, boolean b, Object out){
        Server s = new Server(ans1, ans2, b, out);
        s.startServer();
   }
}