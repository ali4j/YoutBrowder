package net.ali4j.youtbrowder;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
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
    private Dialog<ButtonType> dialog;
    private ButtonType okButtonType;

    public OptionsDialog(){
        dialog = new Dialog<>();
        dialog.setTitle("Options");

        // Set the button types.
        okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);



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
        portTextField = new TextField();

        if(Config.isUSEPROXY()) {
            useProxyCheckBox.setSelected(true);
            hostTextField.setText(Config.getHTTPPROXYHOST());
            portTextField.setText(Config.getHTTPPROXYPORT());
            hostTextField.setDisable(false);
            portTextField.setDisable(false);
        } else {
            hostTextField.setPromptText("host");
            portTextField.setPromptText("port");
            hostTextField.setDisable(true);
            portTextField.setMaxWidth(70);
            portTextField.setDisable(true);
        }








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

        Optional<ButtonType> result = dialog.showAndWait();
        result.ifPresent(pair -> {
            if (logger.isDebugEnabled()) logger.debug("if present");

            if(!pair.getButtonData().isCancelButton()){
                Config.setUSEPROXY(useProxyCheckBox.isSelected());
                Config.setHTTPPROXYHOST(hostTextField.getText());
                Config.setHTTPPROXYPORT(portTextField.getText());
                Config.setHTTPSPROXYHOST(hostTextField.getText());
                Config.setHTTPSPROXYPORT(portTextField.getText());
                YoutBrowder.setOptions();
                Config.store();
                logger.info("user clicked ok button, properties are set in config class also are stored in properties file");
            }
        });



    }
}
