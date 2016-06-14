
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MulticastThread implements Runnable{
    
    private final MulticastSocket socket;
    private volatile boolean running = true;
    private final InetAddress addr;
    private final int port;
    private final MessengerFrame messenger;
    
    public MulticastThread(int portNum, String ip, MessengerFrame mf) throws IOException {
        socket = new MulticastSocket(portNum);
        addr = InetAddress.getByName(ip);
        port = portNum;
        socket.joinGroup(addr);
        messenger = mf;
    }
    
    public void send(String msg) {
        try {
            byte[] bytes = msg.getBytes();
            DatagramPacket dp = new DatagramPacket(bytes, bytes.length, addr, port);
            socket.send(dp);
        } catch (IOException ex) {
            Logger.getLogger(MulticastThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void stop() {
        try {
            socket.leaveGroup(addr);
            running = false;
        } catch (IOException ex) {
            Logger.getLogger(MulticastThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() {
        byte[] bytes;
        DatagramPacket dp;
        while(running) {
            try {
                bytes = new byte[8192];
                dp = new DatagramPacket(bytes, bytes.length);
                socket.receive(dp);
                String packet = new String(dp.getData(), "8859_1");
                String[] info = packet.split("\\$\\$\\@\\#\\$");
                messenger.recieveMessage(info);
            } catch (IOException ex) {
                Logger.getLogger(MulticastThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
