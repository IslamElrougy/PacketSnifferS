import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;

public class PacketHandler implements PacketReceiver {

    TableView<packet> Table;
    TextArea detailedView;
    TextArea hexadecimalView;

    PacketHandler (TableView<packet> table, TextArea details, TextArea hexadecimal) {
        Table = table;
        detailedView = details;
        hexadecimalView = hexadecimal;
    }

    public void receivePacket (Packet packet) {

        //TODo
        //fill table with the received packet
        //Table.getItems().add(new packet("Packet Number", "Time", "Source Address", "Destination Address", "Protocol", "Length", "Information"));

        System.out.println(packet.toString());

        /*
        //for TCP packet
        InetAddress sourceAddress = ((TCPPacket) packet).src_ip;
        InetAddress destinationAddress = ((TCPPacket) packet).dst_ip;
        int src_port = ((TCPPacket) packet).src_port;
        int dst_port = ((TCPPacket) packet).dst_port;
        long sequence = ((TCPPacket) packet).sequence;
        long ack_num = ((TCPPacket) packet).ack_num;
        int window = ((TCPPacket) packet).window;
        int length = ((TCPPacket) packet).len;
        */

    }

}

