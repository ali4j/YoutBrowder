package net.ali4j.youtbrowder;

/**
 * Created by ehsan on 8/29/2017.
 */
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.log4j.Logger;


/**
 * A sample that demonstrates a WebView object accessing a web page.
 * Most of the codes are taken form the oracle samples
 */
public class YoutBrowder extends Application {

    public static final String DEFAULT_URL = "https://youtube.com";

    private static final Logger logger = Logger.getLogger(YoutBrowder.class);

    private OptionsDialog optionsDialog;

    public static void setOptions(){
        Options options = Options.getInstance();
        if(options.getUseProxy()) {
            System.getProperties().put("http.proxyHost", options.getHost());
            System.getProperties().put("http.proxyPort", options.getPort());
            System.getProperties().put("https.proxyHost", options.getHost());
            System.getProperties().put("https.proxyPort", options.getPort());
        } else {
            System.getProperties().remove("http.proxyHost");
            System.getProperties().remove("http.proxyPort");
            System.getProperties().remove("https.proxyHost");
            System.getProperties().remove("https.proxyPort");
        }
    }




    public Parent createContent() {

        WebView webView = new WebView();

        final WebEngine webEngine = webView.getEngine();
        webEngine.load(DEFAULT_URL);

        final TextField locationField = new TextField(DEFAULT_URL);
        webEngine.locationProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) ->
                {
            locationField.setText(newValue);
        });

        EventHandler<ActionEvent> goAction = e -> {
            logger.debug("loading address");
            webEngine.load(locationField.getText().startsWith("http://")
                    ? locationField.getText()
                    : "http://" + locationField.getText());
        };
        locationField.setOnAction(goAction);

        EventHandler<ActionEvent> optionsAction = e ->{
            logger.debug("options is clicked");

            if(optionsDialog==null)
                optionsDialog = new OptionsDialog();
            else
                optionsDialog.show();
        };


        Button goButton = new Button("Go");
        goButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        goButton.setDefaultButton(true);
        goButton.setOnAction(goAction);

        Button optionsButton = new Button("Options");
        optionsButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        optionsButton.setDefaultButton(true);
        optionsButton.setOnAction(optionsAction);

        // Layout logic
        HBox hBox = new HBox(5);
        hBox.getChildren().setAll(locationField, goButton, optionsButton);
        HBox.setHgrow(locationField, Priority.ALWAYS);

        VBox vBox = new VBox(5);
        vBox.getChildren().setAll(hBox, webView);
        vBox.setPrefSize(800, 400);
        VBox.setVgrow(webView, Priority.ALWAYS);
        return vBox;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
    }

    /**
     * Java main for when running without JavaFX launcher
     */
    public static void main(String[] args) {
        launch(args);
    }
}
