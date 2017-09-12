package net.ali4j.youtbrowder;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

/**
 * Created by ali4j on 9/10/2017.
 */
public class Config {
    private static final Logger logger = Logger.getLogger(Config.class);
    private static Properties props = new Properties();

    private static boolean USEPROXY = false;
    private static String HTTPPROXYHOST = null;
    private static String HTTPPROXYPORT = null;
    private static String HTTPSPROXYHOST = null;
    private static String HTTPSPROXYPORT = null;


    public static String getHTTPSPROXYPORT() {
        return HTTPSPROXYPORT;
    }

    public static String getHTTPSPROXYHOST() {
        return HTTPSPROXYHOST;
    }

    public static String getHTTPPROXYPORT() {
        return HTTPPROXYPORT;
    }

    public static String getHTTPPROXYHOST() {
        return HTTPPROXYHOST;
    }

    public static boolean isUSEPROXY() {
        return USEPROXY;
    }

    public static void setHTTPSPROXYPORT(String HTTPSPROXYPORT) {
        Config.HTTPSPROXYPORT = HTTPSPROXYPORT;
    }

    public static void setHTTPSPROXYHOST(String HTTPSPROXYHOST) {
        Config.HTTPSPROXYHOST = HTTPSPROXYHOST;
    }

    public static void setHTTPPROXYPORT(String HTTPPROXYPORT) {
        Config.HTTPPROXYPORT = HTTPPROXYPORT;
    }

    public static void setHTTPPROXYHOST(String HTTPPROXYHOST) {
        Config.HTTPPROXYHOST = HTTPPROXYHOST;
    }

    public static void setUSEPROXY(boolean USEPROXY) {
        Config.USEPROXY = USEPROXY;
    }


    /*
    * loading properties values from config file in config location
    * if it is not available default values should be loaded
     *
    * */
    static {
        File file = new File(Constants.CONFIG_FILE_LOCATION);
        if (file.exists()) {
            try (InputStream inputStream = new FileInputStream(file)) {
                props.load(inputStream);
                USEPROXY = Boolean.valueOf(props.getProperty("useproxy"));
                HTTPPROXYHOST = props.getProperty("http.proxy.host");
                HTTPPROXYPORT = props.getProperty("http.proxy.port");
                HTTPSPROXYHOST = props.getProperty("https.proxy.host");
                HTTPSPROXYPORT = props.getProperty("https.proxy.port");
                logger.info("config instance is loaded from file in config location file");
            } catch (IOException ioe) {
                logger.error(ioe.getMessage());
                USEPROXY = Constants.USEP_ROXY;
                HTTPPROXYHOST = Constants.HTTP_PROXY_HOST;
                HTTPPROXYPORT = Constants.HTTP_PROXY_PORT;
                HTTPSPROXYHOST = Constants.HTTPS_PROXY_HOST;
                HTTPSPROXYPORT = Constants.HTTPS_PROXY_PORT;
                logger.info("ioe exception, config instance is loaded with default values");
            }
        } else {
            USEPROXY = Constants.USEP_ROXY;
            HTTPPROXYHOST = Constants.HTTP_PROXY_HOST;
            HTTPPROXYPORT = Constants.HTTP_PROXY_PORT;
            HTTPSPROXYHOST = Constants.HTTPS_PROXY_HOST;
            HTTPSPROXYPORT = Constants.HTTPS_PROXY_PORT;
            logger.info("config file does not exist in default location, config instance is loaded with default values");
        }
    }


    public static void store() {
        File file = new File(Constants.CONFIG_FILE_LOCATION);
        OutputStream outputStream = null;
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            outputStream = new FileOutputStream(file);
            props.setProperty("useproxy", String.valueOf(isUSEPROXY()));
            props.setProperty("http.proxy.host", getHTTPPROXYHOST());
            props.setProperty("http.proxy.port", getHTTPPROXYPORT());
            props.setProperty("https.proxy.host", getHTTPSPROXYHOST());
            props.setProperty("https.proxy.port", getHTTPSPROXYPORT());
            props.store(outputStream, null);
            logger.info("config instance is stored in properties file in default location:" + Constants.CONFIG_FILE_LOCATION);
        } catch (IOException ioe) {
            logger.error(ioe.getMessage());
            logger.info("ioexception, unable to persist values in config file");
        } finally {
            if (outputStream != null)
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
        }
    }


}
