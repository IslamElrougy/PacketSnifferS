import javafx.collections.ObservableList;
import jpcap.PacketReceiver;
import jpcap.packet.ARPPacket;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import jpcap.packet.UDPPacket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class PacketHandler implements PacketReceiver {

    private int packetNumber = 0;
    private ObservableList<packet> Table;
    private Map<Integer,String> specialPorts = new HashMap<>();

    PacketHandler (ObservableList<packet> table) {
        this.Table = table;
        this.packetNumber = Table.size();
        this.specialPorts.put(8,"HTTP");
        this.specialPorts.put(53,"DNS");
        this.specialPorts.put(443,"SSL");
        this.specialPorts.put(21,"FTP");
        this.specialPorts.put(22,"SSH");
        this.specialPorts.put(23,"Telnet");
        this.specialPorts.put(25,"Telnet");
        this.specialPorts.put(110,"POP3");
        this.specialPorts.put(143,"IMAP");
        this.specialPorts.put(993,"IMAPS");
    }

    @Override
    public void receivePacket (Packet packet) {
        String data = packet.toString();

        packet pack;
        if (data.contains("TCP")){
            pack = parseTCP(packet);
        } else if (data.contains("UDP")){
            pack = parseUDP(packet);
        } else if (data.contains("ARP")){
            pack = parseARP(packet);
        } else {
            return;
        }

        Table.add(pack);
    }

    private packet parseARP(Packet packet) {
        String SHA = ((ARPPacket) packet).getSenderHardwareAddress().toString();
        String SPA = ((ARPPacket) packet).getSenderProtocolAddress().toString();
        String THA = ((ARPPacket) packet).getTargetHardwareAddress().toString();
        String TPA = ((ARPPacket) packet).getTargetProtocolAddress().toString();

        int len = ((ARPPacket) packet).len ;

        String info = "" + SHA + " -> " + THA + "  Len = " + len;

        return new packet("" + packetNumber,"" + packet.sec, SPA, TPA,"UDP","" + len, info, "","", "", "","", "" + len, packet.header, packet.data, packet);
    }

    private packet parseUDP(Packet packet) {
        InetAddress sourceAddress = ((UDPPacket) packet).src_ip;
        InetAddress destinationAddress = ((UDPPacket) packet).dst_ip;
        int src_port = ((UDPPacket) packet).src_port;
        int dst_port = ((UDPPacket) packet).dst_port;
        int length = ((UDPPacket) packet).length;
        int len = ((UDPPacket) packet).len;

        String protocol = "UDP";
        if (specialPorts.containsKey(dst_port)){
            protocol = specialPorts.get(dst_port);
        }else if (specialPorts.containsKey(src_port)){
            protocol = specialPorts.get(src_port);
        }

        String info = "" + dst_port + " -> " + dst_port + "  Len = " + len;

        return new packet("" + packetNumber,"" + packet.sec, sourceAddress.getHostAddress(), destinationAddress.getHostAddress(), protocol,"" + length, info,
                String.valueOf(((UDPPacket) packet).dst_port), String.valueOf(((UDPPacket) packet).src_port),"","","","" + len, packet.header, ((UDPPacket)packet).data, packet);
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
        int len = ((TCPPacket) packet).len;

        String protocol = "TCP";
        if (specialPorts.containsKey(dst_port)){
            protocol = specialPorts.get(dst_port);
        }else if (specialPorts.containsKey(src_port)){
            protocol = specialPorts.get(src_port);
        }

        String info = "" + dst_port + " -> " + dst_port + "  Seq = " + sequence + "  Ack = " + ack_num + " Win = " + window + "  Len = " + len;

        return new packet("" + packetNumber,"" + packet.sec, sourceAddress.toString(), destinationAddress.toString(), protocol,"" + length, info,
                String.valueOf(((TCPPacket) packet).dst_port), String.valueOf(((TCPPacket) packet).src_port), String.valueOf(((TCPPacket) packet).sequence), String.valueOf(((TCPPacket) packet).ack_num),
                String.valueOf(((TCPPacket) packet).window), "" + len, packet.header, packet.data, packet);
    }

}