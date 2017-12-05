import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;

public class CaptureScene {

    private static Thread captureThread;
    private static String filter;
    private static JpcapCaptor CAP;
    private static NetworkInterface NetworkInterface;
    private static TableView<packet> table = new TableView<>();
    private static TextArea detailedView;
    private static TextArea hexadecimalView;
    private static ObservableList<packet> packets = FXCollections.observableArrayList();

    public static void display(NetworkInterface networkInterface, String fltr) {

        final Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("CAPTURE PACKETS");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        NetworkInterface = networkInterface;
        filter = fltr;

        MenuBar menuBar = new MenuBar();
        GridPane.setConstraints(menuBar, 0, 0);

        Label start_Label = new Label("Start");
        start_Label.setOnMouseClicked(e -> {
            if (start_Label.getText().equals("Start")) {
                startCapture();
                start_Label.setText("Stop");
            } else {
                stopCapture();
                start_Label.setText("Start");
            }
        });

        Menu start_Menu = new Menu();
        start_Menu.setGraphic(start_Label);
        menuBar.getMenus().add(start_Menu);

        final TextField filter_TextField = new TextField();
        filter_TextField.setPromptText("Apply a display filter");
        GridPane.setConstraints(filter_TextField, 0, 1);

        TableColumn<packet, String> numberColumn = new TableColumn<>("No.");
        numberColumn.setMinWidth(100);
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));

        TableColumn<packet, String> timeColumn = new TableColumn<>("Time");
        timeColumn.setMinWidth(100);
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<packet, String> sourceColumn = new TableColumn<>("Source");
        sourceColumn.setMinWidth(100);
        sourceColumn.setCellValueFactory(new PropertyValueFactory<>("source"));

        TableColumn<packet, String> destinationColumn = new TableColumn<>("Destination");
        destinationColumn.setMinWidth(100);
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));

        TableColumn<packet, String> protocolColumn = new TableColumn<>("Protocol");
        protocolColumn.setMinWidth(100);
        protocolColumn.setCellValueFactory(new PropertyValueFactory<>("protocol"));

        TableColumn<packet, String> lengthColumn = new TableColumn<>("Length");
        lengthColumn.setMinWidth(100);
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("length"));

        TableColumn<packet, String> infoColumn = new TableColumn<>("Info");
        infoColumn.setMinWidth(200);
        infoColumn.setCellValueFactory(new PropertyValueFactory<>("info"));

        packets.add(new packet("1", "000000", "src", "dst", "FTP", "20", "info1"));
        packets.add(new packet("2", "000001", "src", "dst", "ARP", "30", "info2"));
        packets.add(new packet("3", "000002", "src", "dst", "TCP", "50", "info3"));
        packets.add(new packet("4", "000003", "src", "dst", "UDP", "70", "info4"));
        FilteredList<packet> filteredData = new FilteredList<>(packets, p -> true);

        filter_TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(packet -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (packet.getNumber().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter match
                } else if (packet.getTime().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter match
                } else if (packet.getSource().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter match
                } else if (packet.getDestination().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter match
                } else if (packet.getProtocol().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter match
                } else if (packet.getLength().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter match
                } else if (packet.getInfo().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter match
                }
                return false; // Does not match
            });
        });

        SortedList<packet> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);

        table.getColumns().addAll(numberColumn, timeColumn, sourceColumn, destinationColumn, protocolColumn, lengthColumn, infoColumn);
        GridPane.setConstraints(table, 0, 3);

        table.setRowFactory( tv -> {
            TableRow<packet> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()) ) {
                    packet rowData = row.getItem();
                    detailedView.setText("Show detailed view of packet no." + rowData.getNumber());
                    hexadecimalView.setText("Show hexadecimal view of packet no." + rowData.getNumber());
                }
            });
            return row ;
        });


        //TODO
        //use this TextAres to display details of the packet
        detailedView = new TextArea();
        detailedView.setEditable(false);
        GridPane.setConstraints(detailedView, 0, 8);

        //TODO
        //use this TextAres to display hexadecimal Value of the packet
        hexadecimalView = new TextArea();
        hexadecimalView.setEditable(false);
        GridPane.setConstraints(hexadecimalView, 0, 12);

        grid.getChildren().addAll(menuBar, filter_TextField, table, detailedView, hexadecimalView);

        Scene scene = new Scene(grid, 800, 650);
        window.setScene(scene);
        window.showAndWait();
    }

    private static void startCapture() {
        captureThread = new Thread() {
            @Override
            public void run() {

                try{
                    CAP = JpcapCaptor.openDevice(NetworkInterface, 65535, false, 20);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                while (!isInterrupted()) {
                    try {

                        switch (filter) {
                            case "HTTP":
                                CAP.setFilter("port 80", true);
                                break;
                            case "SSL":
                                CAP.setFilter("port 443", true);
                                break;
                            case "FTP":
                                CAP.setFilter("port 21", true);
                                break;
                            case "SSH":
                                CAP.setFilter("port 22", true);
                                break;
                            case "Telnet":
                                CAP.setFilter("port 23", true);
                                break;
                            case "SMTP":
                                CAP.setFilter("port 25", true);
                                break;
                            case "POP3":
                                CAP.setFilter("port 110", true);
                                break;
                            case "IMAP":
                                CAP.setFilter("port 143", true);
                                break;
                            case "IMAPS":
                                CAP.setFilter("port 993", true);
                                break;
                            case "DNS":
                                CAP.setFilter("port 53", true);
                                break;
                            case "NetBIOS":
                                CAP.setFilter("port 139", true);
                                break;
                            case "SAMBA":
                                CAP.setFilter("port 137", true);
                                break;
                            case "AD":
                                CAP.setFilter("port 445", true);
                                break;
                            case "SQL":
                                CAP.setFilter("port 118", true);
                                break;
                            case "LDAP":
                                CAP.setFilter("port 389", true);
                                break;
                            default:
                                break;
                        }

                        //CAP.getPacket();
                        CAP.processPacket(1, new PacketHandler(table, detailedView, hexadecimalView));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        captureThread.start();
    }

    private static void stopCapture() {
        captureThread.interrupt();
    }

}

