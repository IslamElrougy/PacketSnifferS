import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;
import jpcap.PacketReceiver;
import jpcap.packet.ARPPacket;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import jpcap.packet.UDPPacket;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class PacketHandler implements PacketReceiver {

    ObservableList<packet> Table;
    TextArea detailedView;
    TextArea hexadecimalView;
    int packetNumber = 0;
    Map<Integer,String> specialPorts = new HashMap<>();
    ObservableList<packet> packets = FXCollections.observableArrayList();

    PacketHandler (ObservableList<packet> table, TextArea details, TextArea hexadecimal , int packetNumber) {
        Table = table;
        detailedView = details;
        hexadecimalView = hexadecimal;
        this.packetNumber = Table.size();
        specialPorts.put(8,"HTTP");
        specialPorts.put(53,"DNS");
        specialPorts.put(443,"SSL");
        specialPorts.put(21,"FTP");
        specialPorts.put(22,"SSH");
        specialPorts.put(23,"Telnet");
        specialPorts.put(25,"Telnet");
        specialPorts.put(110,"POP3");
        specialPorts.put(143,"IMAP");
        specialPorts.put(993,"IMAPS");
    }
    @Override
    public void receivePacket (Packet packet) {

        //TODo
        //fill table with the received packet
        //Table.getItems().add(new packet("Packet Number", "Time", "Source Address", "Destination Address", "Protocol", "Length", "Information"));

        String data = packet.toString();
        System.out.println(data);
        packet pack;
        if (data.contains("TCP")){
            System.out.println("TCP");
            pack = parseTCP(packet);
        }else if (data.contains("UDP")){
            System.out.println("UDP");
            pack = parseUDP(packet);

        }else if (data.contains("ARP")){
            pack = parseARP(packet);
        }
        else
            return;
        packets.add(pack);
        Table.add(pack);

        // Table.notifyAll();
        /*
            HTTP 80
            DNS 53
            ARP > ARP REQUEST 70:9f:2d:82:65:e8(/192.168.1.1) -> 00:00:00:00:00:00(/192.168.1.2)
            ARP REPLY 78:45:61:c9:7c:8b(/192.168.1.2) -> 70:9f:2d:82:65:e8(/192.168.1.1)

            Arraylist

            diplay packets in columns

            filter works on packets captured
       */

    }

    private packet parseARP(Packet packet) {
        String SHA = ((ARPPacket) packet).getSenderHardwareAddress().toString();
        String SPA = ((ARPPacket) packet).getSenderProtocolAddress().toString();
        String THA = ((ARPPacket) packet).getTargetHardwareAddress().toString();
        String TPA = ((ARPPacket) packet).getTargetProtocolAddress().toString();
        String protcol = "UDP";

        int len = ((ARPPacket) packet).len ;
        String no = "" + packetNumber;

        String info = "" + SHA + " -> " + THA + "  Len = " + len;
        packet pack = new packet(no,"" + packet.sec, SPA.toString(),TPA.toString(),protcol,"" + len,info);
        return pack;
    }

    private packet parseUDP(Packet packet) {
        InetAddress sourceAddress = ((UDPPacket) packet).src_ip;
        InetAddress destinationAddress = ((UDPPacket) packet).dst_ip;
        int src_port = ((UDPPacket) packet).src_port;
        int dst_port = ((UDPPacket) packet).dst_port;
        int length = ((UDPPacket) packet).length;
        int len = ((UDPPacket) packet).len ;

        String protcol = "UDP";
        if (specialPorts.containsKey(dst_port)){
            protcol = specialPorts.get(dst_port);
        }else if (specialPorts.containsKey(src_port)){
            protcol = specialPorts.get(src_port);
        }
        String no = "" + packetNumber;

        String info = "" + dst_port + " -> " + dst_port + "  Len = " + len;
        packet pack = new packet(no,"" + packet.sec, sourceAddress.getHostAddress().toString(),destinationAddress.getHostAddress().toString(),protcol,"" + length,info);

        return pack;
    }

    private packet parseTCP(Packet packet) {
        InetAddress sourceAddress = ((TCPPacket) packet).src_ip;
        InetAddress destinationAddress = ((TCPPacket) packet).dst_ip;
        int src_port = ((TCPPacket) packet).src_port;
        int dst_port = ((TCPPacket) packet).dst_port;
        long sequence = ((TCPPacket) packet).sequence;
        long ack_num = ((TCPPacket) packet).ack_num;
        int window = ((TCPPacket) packet).window;
        int length = ((TCPPacket) packet).length;
        int len = ((TCPPacket) packet).len ;

        String protcol = "TCP";
        if (specialPorts.containsKey(dst_port)){
            protcol = specialPorts.get(dst_port);
        }else if (specialPorts.containsKey(src_port)){
            protcol = specialPorts.get(src_port);
        }
        String no = "" + packetNumber;

        String info = "" + dst_port + " -> " + dst_port + "  Seq = " + sequence + "  Ack = " + ack_num + " Win = " + window + "  Len = " + len;
        packet pack = new packet(no,"" + packet.sec, sourceAddress.toString(),destinationAddress.toString(),protcol,"" + length,info);

        return pack;
    }

}

