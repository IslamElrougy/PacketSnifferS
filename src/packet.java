import jpcap.packet.Packet;

public class packet {

    private String number;
    private String time;
    private String source;
    private String destination;
    private String protocol;
    private String IPlength;
    private String info;
    private String source_port;
    private String dest_port;
    private String sequence;
    private String ackNum;
    private String window;
    private String TLayerLength;
    private byte[] header;
    private byte[] payload;
    private Packet packObject;

    public packet(){
        this.number = "";
        this.time = "";
        this.source = "";
        this.destination = "";
        this.protocol = "";
        this.IPlength = "";
        this.info = "";
        this.source_port = "";
        this.dest_port = "";
        this.sequence = "";
        this.ackNum = "";
        this.window = "";
    }

    public packet(String n, String t, String s, String d, String p, String l, String i, String d_port, String s_port, String seq,
                  String ack, String win, String len, byte[] head, byte[] load, Packet object)
    {
        this.number = n;
        this.time = t;
        this.source = s;
        this.destination = d;
        this.protocol = p;
        this.IPlength = l;
        this.info = i;
        this.dest_port = d_port;
        this.source_port = s_port;
        this.sequence = seq;
        this.ackNum = ack;
        this.window = win;
        this.TLayerLength = len;
        this.payload = load;
        this.header = head;
        this.packObject = object;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getIPLength() {
        return IPlength;
    }

    public void setIPLength(String length) {
        this.IPlength = length;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public byte[] getPayload() {
        return payload;
    }

    public byte[] getHeader() {
        return header;
    }

    public String getSource_port() {
        return source_port;
    }

    public String getDest_port() {
        return dest_port;
    }

    public String getSequence() {
        return sequence;
    }

    public String getAckNum() {
        return ackNum;
    }

    public String getWindow() {
        return window;
    }

    public String getTLayerLength() {
        return TLayerLength;
    }

    public Packet getPackObject(){
        return packObject;
    }
}
