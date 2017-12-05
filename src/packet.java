public class packet {

    private String number;
    private String time;
    private String source;
    private String destination;
    private String protocol;
    private String length;
    private String info;

    public packet(){
        this.number = "";
        this.time = "";
        this.source = "";
        this.destination = "";
        this.protocol = "";
        this.length = "";
        this.info = "";
    }

    public packet(String n, String t, String s, String d, String p, String l, String i){
        this.number = n;
        this.time = t;
        this.source = s;
        this.destination = d;
        this.protocol = p;
        this.length = l;
        this.info = i;
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

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
