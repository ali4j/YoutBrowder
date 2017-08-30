package net.ali4j.youtbrowder;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.util.Optional;

/**
 * Created by ehsan on 8/30/2017.
 */
public class OptionsDialog {
    private static final Logger logger = Logger.getLogger(OptionsDialog.class);

    private GridPane gridPane;
    private VBox vBox;
    private FlowPane secondRowFlowPane;
    private FlowPane firstRowFlowPane;
    private TextField portTextField;
    private TextField hostTextField;
    private CheckBox useProxyCheckBox;
    private Label useProxyLabel;
    private Dialog<Pair<String, String>> dialog;
    private ButtonType loginButtonType;


    public void show(){
        dialog.show();
    }

    public OptionsDialog(){
        dialog = new Dialog<>();
        dialog.setTitle("Options");

        // Set the button types.
        loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);



        useProxyLabel = new Label("use proxy:");

        useProxyCheckBox = new CheckBox();
        useProxyCheckBox.setOnAction(e -> {
            if(useProxyCheckBox.isSelected()) {
                hostTextField.setDisable(false);
                portTextField.setDisable(false);
            } else {
                hostTextField.setDisable(true);
                portTextField.setDisable(true);
            }
        });

        hostTextField = new TextField();
        hostTextField.setPromptText("host");
        hostTextField.setDisable(true);

        portTextField = new TextField();
        portTextField.setPromptText("port");
        portTextField.setMaxWidth(70);
        portTextField.setDisable(true);






        firstRowFlowPane  = new FlowPane();
        secondRowFlowPane = new FlowPane();

        firstRowFlowPane.getChildren().add(useProxyLabel);
        firstRowFlowPane.getChildren().add(useProxyCheckBox);
        firstRowFlowPane.setVisible(true);

        secondRowFlowPane.getChildren().add(hostTextField);
        secondRowFlowPane.getChildren().add(new Label(":"));
        secondRowFlowPane.getChildren().add(portTextField);
        secondRowFlowPane.setVisible(true);

        vBox = new VBox(firstRowFlowPane, secondRowFlowPane);
        vBox.setAlignment(Pos.CENTER);
        vBox.setVisible(true);

        gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setVgap(50);


        gridPane.getChildren().add(vBox);


        dialog.getDialogPane().setContent(gridPane);

        // Request focus on the username field by default.
        Platform.runLater(hostTextField::requestFocus);

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(hostTextField.getText(), portTextField.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(pair -> {

            Options options = Options.getInstance();
            options.setUseProxy(Boolean.TRUE);
            options.setHost(pair.getKey());
            options.setPort(pair.getValue());
            if(logger.isDebugEnabled()) logger.debug(options.toString());
            YoutBrowder.setOptions();

        });
    }
}
