import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jpcap.JpcapCaptor;
import jpcap.JpcapWriter;
import jpcap.NetworkInterface;

import java.io.IOException;

public class CaptureScene {

    private static Thread captureThread;
    private static String filter;
    private static JpcapCaptor CAP;
    private static JpcapWriter WRITER;
    private static NetworkInterface NetworkInterface;
    private static TableView<packet> table = new TableView<>();
    private static TextArea detailedView;
    private static TextArea hexadecimalView;
    private static Button startButton;
    private static Button saveButton;
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

        Label packetTableLabel = new Label("Captured Packets Table");
        packetTableLabel.setFont(Font.font("", FontWeight.BOLD, 14));
        grid.add(packetTableLabel, 0, 2, 3, 1);

        Label detailedLabel = new Label("Packet Details");
        detailedLabel.setFont(Font.font("", FontWeight.BOLD, 14));
        grid.add(detailedLabel, 0, 4, 3, 1);

        Label hexaLabel = new Label("Packet Raw Data");
        hexaLabel.setFont(Font.font("", FontWeight.BOLD, 14));
        grid.add(hexaLabel, 0, 6, 3, 1);

        startButton = new Button("Start Capturing");
        startButton.setMinWidth(130);
        startButton.setMaxHeight(50);
        startButton.setFont(Font.font(14));
        startButton.setStyle("-fx-focus-color: grey; -fx-faint-focus-color: transparent");
        startButton.setOnMouseClicked(e -> {
            if (startButton.getText().equals("Start Capturing")) {
                startCapture();
                startButton.setText("Stop Capturing");
            } else {
                stopCapture();
                startButton.setText("Start Capturing");
            }
        });
        grid.add(startButton, 0, 0, 1, 1);

        saveButton = new Button("Save Packets");
        saveButton.setMinWidth(130);
        saveButton.setMaxHeight(50);
        saveButton.setFont(Font.font(14));
        saveButton.setStyle("-fx-focus-color: grey; -fx-faint-focus-color: transparent");
        saveButton.setOnMouseClicked(e -> {
            try {
                WRITER = JpcapWriter.openDumpFile(CAP,"CapturedPackets.pcap");
                ObservableList<packet> rows = table.getItems();
                for(packet each : rows)
                {
                    WRITER.writePacket(each.getPackObject());
                }
                //WRITER.close();
            }
            catch (IOException i)
            {
                i.printStackTrace();
            }
            catch (Exception s)
            {
                s.printStackTrace();
            }
        });
        grid.add(saveButton, 87, 0, 1, 1);

        final TextField filter_TextField = new TextField();
        filter_TextField.setPromptText("Apply a display filter");
        GridPane.setConstraints(filter_TextField, 0, 1);
        filter_TextField.setMinWidth(1140);
        grid.add(filter_TextField, 0, 1, 2, 1);

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
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("TLayerLength"));

        TableColumn<packet, String> infoColumn = new TableColumn<>("Info");
        infoColumn.setMinWidth(200);
        infoColumn.setCellValueFactory(new PropertyValueFactory<>("info"));

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
                } else if (packet.getIPLength().toLowerCase().contains(lowerCaseFilter)) {
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

        table.getColumns().clear();
        table.getColumns().addAll(numberColumn, timeColumn, sourceColumn, destinationColumn, protocolColumn, lengthColumn, infoColumn);
        GridPane.setConstraints(table, 0, 3);
        table.setMinWidth(1140);
        table.setMaxHeight(350);
        grid.add(table, 0, 3, 2, 1);

        table.setRowFactory( tv -> {
            TableRow<packet> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()) ) {
                    packet rowData = row.getItem();
                    String dataTransport = "";
                    String dataPayload = "";
                    if(rowData.getProtocol() == "TCP") {
                        dataTransport = "Transmission Control Protocol\n";
                        dataTransport += "Source Port: " + rowData.getSource_port() + "\n";
                        dataTransport += "Destination Port: " + rowData.getDest_port() + "\n";
                        dataTransport += "TCP Segment Length: " + rowData.getTLayerLength() + "\n";
                        dataTransport += "Sequence Number: " + rowData.getSequence() + "\n";
                        dataTransport += "Acknowledgment Number: " + rowData.getAckNum() + "\n";
                        dataTransport += "Header Length: " + rowData.getHeader().length + "\n";
                        dataTransport += "Window Size Value: " + rowData.getWindow() + "\n";
                        dataTransport += "TCP Payload Size: " + rowData.getPayload().length + "\n\n";
                    }
                    else if(rowData.getProtocol() == "UDP")
                    {
                        dataTransport = "User Datagram Protocol\n";
                        dataTransport += "Source Port: " + rowData.getSource_port() + "\n";
                        dataTransport += "Destination Port: " + rowData.getDest_port() + "\n";
                        dataTransport += "UDP Segment Length: " + rowData.getTLayerLength() + "\n\n";
                    }

                    if(rowData.getPayload().length > 1)
                    {
                        if(new String(rowData.getPayload()).contains("HTTP"))
                        {
                            dataPayload = "Hypertext Transfer Protocol\n";
                            String payload = new String(rowData.getPayload());
                            dataPayload += payload;
                        }
                        else if(rowData.getProtocol() == "DNS")
                        {
                            dataTransport = "User Datagram Protocol\n";
                            dataTransport += "Source Port: " + rowData.getSource_port() + "\n";
                            dataTransport += "Destination Port: " + rowData.getDest_port() + "\n";
                            dataTransport += "UDP Segment Length: " + rowData.getTLayerLength() + "\n\n";
                            dataPayload = "Domain Name System Protocol\n";
                            String payload = new String(rowData.getPayload());
                            dataPayload += payload;
                        }
                    }

                    String viewData = dataTransport + dataPayload;
                    detailedView.setText(viewData);

                    String hexaHeader = PacketHandler.bytesToHex(rowData.getHeader());
                    String hexaPayload = PacketHandler.bytesToHex(rowData.getPayload());
                    String viewHexa = hexaHeader + " " + hexaPayload;
                    hexadecimalView.setText(viewHexa);
                }
            });
            return row;
        });

        //TODO
        //use this TextAres to display details of the packet
        detailedView = new TextArea();
        detailedView.setEditable(false);
        detailedView.setWrapText(true);
        detailedView.setMinWidth(1140);
        detailedView.setMaxHeight(250);
        GridPane.setConstraints(detailedView, 0, 8);
        grid.add(detailedView, 0, 5, 2, 1);

        //TODO
        //use this TextAres to display hexadecimal Value of the packet
        hexadecimalView = new TextArea();
        hexadecimalView.setEditable(false);
        hexadecimalView.setWrapText(true);
        hexadecimalView.setMinWidth(1140);
        hexadecimalView.setMaxHeight(250);
        GridPane.setConstraints(hexadecimalView, 0, 12);
        grid.add(hexadecimalView, 0, 7, 2, 1);

        //grid.getChildren().addAll(startButton, saveButton, filter_TextField, table, detailedView, hexadecimalView);
        GridPane.setHalignment(saveButton, HPos.RIGHT);
        Scene scene = new Scene(grid, 1150, 900);
        window.setScene(scene);
        window.setResizable(false);
        //window.showAndWait();
        window.show();
        window.setOnHiding(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        // Clear the packet observable list to clear the table for the next capturing session after closing the window.
                        packets.clear();
                    }
                });
            }

        });

    }

    private static void startCapture() {
        captureThread = new Thread() {
            int packetnumber = 0;
            @Override
            public void run() {

                try{
                    CAP = JpcapCaptor.openDevice(NetworkInterface, 65535, true, 20);
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
                        CAP.processPacket(1, new PacketHandler(packets, detailedView, hexadecimalView,packetnumber++));
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

