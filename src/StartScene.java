import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;

public class StartScene extends Application{

    private ListView<String> interfaces;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        Stage window = primaryStage;
        window.setTitle("Welcome");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        Label capture_Label = new Label("Welcome To Packet Sniffer, Please Select Interface.");
        capture_Label.setFont(Font.font("", FontWeight.SEMI_BOLD, 22));
        GridPane.setConstraints(capture_Label, 5, 0);
        grid.add(capture_Label, 1, 0, 11, 2);

        Label filter_Label = new Label("Choose Filter: ");
        filter_Label.setFont(Font.font("", FontWeight.SEMI_BOLD, 16));
        GridPane.setConstraints(filter_Label, 0, 1);
        grid.add(filter_Label, 1, 2, 5, 2);

        ComboBox<String> filter_ChoiceBox = new ComboBox<>();
        filter_ChoiceBox.getItems().addAll("HTTP", "SSL", "FTP", "SSH", "Telnet", "SMTP", "POP3", "IMAP", "IMAPS", "DNS", "NetBIOS", "SAMBA", "AD", "SQL", "LDAP");
        filter_ChoiceBox.setValue("Available Filters");
        filter_ChoiceBox.setPromptText("Available Filters");
        filter_ChoiceBox.setStyle("-fx-focus-color: grey; -fx-faint-focus-color: transparent");
        filter_ChoiceBox.setPrefSize(150, 20);
        GridPane.setConstraints(filter_ChoiceBox, 1, 1);
        grid.add(filter_ChoiceBox, 6, 2, 5, 2);

        NetworkInterface[] NETWORK_INTERFACES = JpcapCaptor.getDeviceList();
        interfaces = new ListView<>();
        interfaces.setMinSize(490, interfaces.getPrefHeight());
        interfaces.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        for (NetworkInterface i : NETWORK_INTERFACES) {
            interfaces.getItems().add(i.description);
        }
        GridPane.setConstraints(interfaces, 0, 4);
        grid.add(interfaces, 1, 4, 10, 2);

        interfaces.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                CaptureScene.display(NETWORK_INTERFACES[interfaces.getSelectionModel().getSelectedIndex()], filter_ChoiceBox.getValue());
            }
        });

        Scene scene = new Scene(grid, 530, 500);
        window.setScene(scene);
        window.show();
    }

}