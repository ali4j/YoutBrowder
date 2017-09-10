package net.ali4j.youtbrowder;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by ehsan on 9/10/2017.
 */
public class Config {
    private boolean useProxy;
    private String httpProxyHost;
    private String  httpProxyPort;
    private String httpsProxyHost;
    private String httpsProxyPort;
    private String tempDirectory;

    private static Logger logger = Logger.getLogger(Config.class);

    private static Config config;

    private Config(){
        Properties props = new Properties();
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("config.properties")){
            props.load(inputStream);
            useProxy = Boolean.valueOf(props.getProperty("useproxy"));
            httpProxyHost  = props.getProperty("http.proxy.host");
            httpProxyPort  = props.getProperty("http.proxy.port");
            httpsProxyHost = props.getProperty("https.proxy.host");
            httpsProxyPort = props.getProperty("https.proxy.port");
            tempDirectory  = props.getProperty("temp.directory");
        }catch(IOException ioe){
            logger.error(ioe);
        }
    }

    public static Config getConfig(){
        if(config==null) config = new Config();
        return config;
    }

}
