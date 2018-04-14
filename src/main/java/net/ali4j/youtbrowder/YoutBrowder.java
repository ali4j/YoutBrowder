package net.ali4j.youtbrowder;

import com.github.axet.vget.VGet;
import com.sun.webkit.dom.HTMLAnchorElementImpl;
import com.sun.webkit.dom.HTMLDocumentImpl;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


/**
 *
 * Some of the codes are taken from the oracle samples, SO and other websites
 */
public class YoutBrowder extends Application {

    private static final Logger logger = Logger.getLogger(YoutBrowder.class);
    public static String currentLink = "https://youtube.com";

    private OptionsDialog optionsDialog;
    private Button goButton;
    private Button stopButton;
    private Button downloadCurrentLinkButton;
    private Button exitButton;
    private Button optionsButton;

    private static final String YOUTUBE_VIDEO_URL_PATTERN = "^(https\\:\\/\\/)(www\\.)?(youtube.com\\/watch\\?v=).+$";
    private static final String YOUTUBE_URL_PATTERN = "^(https\\:\\/\\/)(www\\.)?(youtube.com)\\/?$";


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

        webEngine.documentProperty().addListener((observable, oldDoc, newDoc) -> {
            HTMLDocumentImpl realMcCoy = (HTMLDocumentImpl) newDoc;
            if(realMcCoy==null) return;
            realMcCoy.setOnmousedown(evt -> {
                Element element = (Element)evt.getTarget();
                logger.debug("clicked element name:"+element.getTagName());

                if("SPAN".equals(element.getTagName())) {
                    Node parent = element.getParentNode();
                    logger.debug("parent node name:" +  parent.getNodeName() +
                            ", link:" + ((HTMLAnchorElementImpl) parent).getAttribute("href"));
                    currentLink = ((HTMLAnchorElementImpl) parent).getAttribute("href");
                } else if("IMG".equals(element.getTagName())) {
                    Node parent = element.getParentNode();
                    Node parentParent = parent.getParentNode();
                    Node parentParentParentAsA =  parentParent.getParentNode();

                    String href = ((HTMLAnchorElementImpl) parentParentParentAsA).getAttribute("href");

                    logger.debug("parent node name:" +  parentParentParentAsA.getNodeName() +
                            ", link:" + href);
                    currentLink = href;
                } else {
                    String href = element.getAttribute("href");
                    currentLink = href;
                }
                logger.debug("clicked link:" + currentLink);
            });
        });

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


        EventHandler<ActionEvent> exitAction = e -> {
            logger.debug("exit button is clicked");
            //TODO: pause any active download before exiting
            System.exit(1);
        };

        EventHandler<ActionEvent> goAction = e -> {
            logger.debug("loading address");
            if(currentLink!=null &
                    !locationField.getText().matches(YOUTUBE_VIDEO_URL_PATTERN)
                    & !locationField.getText().matches(YOUTUBE_URL_PATTERN))  {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("invalid url");
                alert.setContentText("this link is not a valid youtube video link");
                alert.showAndWait();
            } else {
                goButton.setDisable(true);
                stopButton.setDisable(false);
                webEngine.load(locationField.getText());
            }
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

        EventHandler<ActionEvent> downloadCurrentAction = evt ->{
            logger.debug("download current is clicked");
            try{

                if(currentLink==null | !currentLink.matches(YOUTUBE_URL_PATTERN))
                    throw new MalformedURLException("please click on a youtube video link");

                String videoUrlString = Constants.YOUTUBE_ADDRESS_BASE + currentLink;
                URL videoUrl = new URL(videoUrlString);
                VGet v = new VGet(videoUrl, new File(Constants.DEFAULT_SAVE_LOCATION));
                v.download();
            } catch (Exception e){
                logger.error(e.getMessage(), e);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Exception Happended");
                if(e instanceof MalformedURLException) alert.setContentText("wrong url");
                else alert.setContentText("unknown exception happended");
                alert.showAndWait();
            }
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

        downloadCurrentLinkButton= new Button("DownCurrLin");
        downloadCurrentLinkButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        downloadCurrentLinkButton.setDefaultButton(true);
        downloadCurrentLinkButton.setOnAction(downloadCurrentAction);


        optionsButton = new Button("Options");
        optionsButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        optionsButton.setDefaultButton(true);
        optionsButton.setOnAction(optionsAction);

        exitButton = new Button("Exit");
        exitButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        exitButton.setDefaultButton(true);
        exitButton.setOnAction(exitAction);


        // Layout logic
        HBox hBox = new HBox(5);
        hBox.getChildren().setAll(locationField,downloadCurrentLinkButton, goButton, stopButton, optionsButton, exitButton);
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
