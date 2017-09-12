package net.ali4j.youtbrowder;

/**
 * Created by ali4j on 8/29/2017.
 */

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


/**
 *
 * Some of the codes are taken from the oracle samples, SO and other websites
 */
public class YoutBrowder extends Application {

    private static final Logger logger = Logger.getLogger(YoutBrowder.class);

    private OptionsDialog optionsDialog;
    private Button goButton;
    private Button stopButton;

    public static void setOptions(){
        if(Config.isUSEPROXY()){
            System.getProperties().put("http.proxyHost", Config.getHTTPPROXYHOST());
            System.getProperties().put("http.proxyPort", Config.getHTTPPROXYPORT());
            System.getProperties().put("https.proxyHost", Config.getHTTPSPROXYHOST());
            System.getProperties().put("https.proxyPort", Config.getHTTPSPROXYPORT());
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
        webEngine.load(Constants.DEFAULT_URL);

        final TextField locationField = new TextField(Constants.DEFAULT_URL);
        webEngine.locationProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) ->
                {
            locationField.setText(newValue);
        });


        webEngine.getLoadWorker().stateProperty().addListener(
            (observable, oldValue, newValue) -> {
                if( newValue != Worker.State.SUCCEEDED ) return;
                try{
                    loadJquery(webEngine);
                }catch(IOException ioe){
                    logger.error(ioe);
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Dialog");
                    alert.setHeaderText("Exception happened");
                    alert.setContentText("please check you internet connection");
                    alert.showAndWait();
                    alert.setOnCloseRequest(e -> System.exit(1));
                }

                logger.debug("web engine is loaded");
                goButton.setDisable(false);
                stopButton.setDisable(true);

                EventListener listener = ev -> {
                    String href = ((Element)ev.getTarget()).getAttribute("href");
                    if(logger.isDebugEnabled()) logger.debug(href);
                };

                Document doc = webEngine.getDocument();
                NodeList hrefList = doc.getElementsByTagName("a");
                if(logger.isDebugEnabled()) logger.debug("href list size:" + hrefList.getLength());
                for (int i=0; i<hrefList.getLength(); i++)
                    ((EventTarget)hrefList.item(i)).addEventListener("click", listener, false);
            }
        );


        EventHandler<ActionEvent> goAction = e -> {
            logger.debug("loading address");
            goButton.setDisable(true);
            stopButton.setDisable(false);
            webEngine.load(locationField.getText().startsWith("http://")
                    ? locationField.getText()
                    : "http://" + locationField.getText());
        };
        locationField.setOnAction(goAction);

        EventHandler<ActionEvent> optionsAction = e ->{
            logger.debug("options is clicked");
            optionsDialog = new OptionsDialog();
        };

        EventHandler<ActionEvent> stopAction = e ->{
            logger.debug("stop is clicked");
            goButton.setDisable(false);
            stopButton.setDisable(true);
            webEngine.getLoadWorker().cancel();
        };


        goButton = new Button("Go");
        goButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        goButton.setDefaultButton(true);
        goButton.setOnAction(goAction);

        stopButton = new Button("Stop");
        stopButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        stopButton.setDefaultButton(true);
        stopButton.setOnAction(stopAction);
        stopButton.setDisable(true);


        Button optionsButton = new Button("Options");
        optionsButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        optionsButton.setDefaultButton(true);
        optionsButton.setOnAction(optionsAction);


        // Layout logic
        HBox hBox = new HBox(5);
        hBox.getChildren().setAll(locationField, goButton, stopButton, optionsButton);
        HBox.setHgrow(locationField, Priority.ALWAYS);

        VBox vBox = new VBox(5);
        vBox.getChildren().setAll(hBox, webView);
        vBox.setPrefSize(800, 400);
        VBox.setVgrow(webView, Priority.ALWAYS);
        return vBox;
    }



    public void loadJquery(WebEngine webEngine) throws IOException {
        StringBuilder jQueryContents = new StringBuilder();
        BufferedReader in = null;
        URLConnection urlConnection;
        URL jqueryLocation;
        try {
            jqueryLocation = new URL(Constants.JQUERY_LOCATION);
            urlConnection = jqueryLocation.openConnection();
            urlConnection.connect();
            in = new BufferedReader(new InputStreamReader(jqueryLocation.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                jQueryContents.append(inputLine);
            webEngine.executeScript(jQueryContents.toString());
            logger.trace("jquery is loaded and executed");
            webEngine.executeScript(Constants.REGEXP_SCRIPT);
            logger.trace("script executed");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
