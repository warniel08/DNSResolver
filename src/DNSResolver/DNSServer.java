package DNSResolver;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class DNSServer extends Thread {
    private DatagramSocket socket;
    private byte[] buf = new byte[256];
    private int port = 8053;

    public DNSServer() throws SocketException {
        socket = new DatagramSocket(port);
    }

    public void run() {
        boolean isRunning = true;

        while (isRunning) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);

                InetAddress address = packet.getAddress();
                int port = packet.getPort();

                DatagramPacket gPacket = new DatagramPacket(buf, packet.getLength(), InetAddress.getByName("8.8.8.8"), 53);

                DNSCache dnsC = new DNSCache();
                DNSMessage dnsM = DNSMessage.decodeMessage(buf);
                DNSRecord dnsR =  dnsC.query(dnsM.getQuestionList().get(0));
                ArrayList<DNSRecord> recArrList = new ArrayList<>();
                recArrList.add(dnsR);

                if (dnsR != null) {
                    dnsM = DNSMessage.buildResponse(dnsM, recArrList);
                    packet = new DatagramPacket(dnsM.toBytes(), dnsM.toBytes().length, address, port);
                    System.out.println("record cached");
                } else {
                    socket.send(gPacket);
                    packet = new DatagramPacket(buf, buf.length);

                    socket.receive(packet);
                    dnsM = DNSMessage.decodeMessage(buf);
                    if (dnsM.getHeader().getResponse_code() == 0)
                        dnsC.insert(dnsM.getQuestionList().get(0), dnsM.getRecordList().get(0));
                    packet = new DatagramPacket(buf, buf.length, address, port);
                }

                socket.send(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }

    public static void main(String[] args) throws SocketException {
        DNSServer s = new DNSServer();
        System.out.println("DNS Server is running on Port " + s.port + "...");
        s.run();
    }
}


