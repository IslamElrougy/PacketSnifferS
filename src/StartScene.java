import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;

public class StartScene extends Application{

    private Stage window;
    private ListView<String> interfaces;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("Welcome");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        Label capture_Label = new Label("CAPTURE");
        GridPane.setConstraints(capture_Label, 0, 0);

        Label filter_Label = new Label("filter: ");
        GridPane.setConstraints(filter_Label, 0, 2);

        //final TextField filter_TextField = new TextField();
        //filter_TextField.setPromptText("Enter a capture filter");
        //GridPane.setConstraints(filter_TextField, 1, 2);

        ChoiceBox<String> filter_ChoiceBox = new ChoiceBox<>();
        filter_ChoiceBox.getItems().addAll("HTTP", "SSL", "FTP", "SSH", "Telnet", "SMTP", "POP3", "IMAP", "IMAPS", "DNS", "NetBIOS", "SAMBA", "AD", "SQL", "LDAP");
        //initially filter equals null so use setValue
        filter_ChoiceBox.setValue("");
        GridPane.setConstraints(filter_ChoiceBox, 1, 2);

        NetworkInterface[] NETWORK_INTERFACES = JpcapCaptor.getDeviceList();
        interfaces = new ListView<>();
        interfaces.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        for (NetworkInterface i : NETWORK_INTERFACES) {
            interfaces.getItems().add(i.description);
        }
        GridPane.setConstraints(interfaces, 1, 4);

        interfaces.setOnMouseClicked(e -> {
            CaptureScene.display(NETWORK_INTERFACES[interfaces.getSelectionModel().getSelectedIndex()], filter_ChoiceBox.getValue());
        });

        grid.getChildren().addAll(capture_Label, filter_Label, filter_ChoiceBox, interfaces);

        Scene scene = new Scene(grid, 800, 650);
        window.setScene(scene);
        window.show();

    }

}
