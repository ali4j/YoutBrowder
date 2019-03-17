package net.ali4j.youtbrowder;

import java.io.File;

/**
 * Created by ehsan on 9/4/2017.
 */
public interface Constants {
    /*String DEFAULT_URL = "https://youtube.com";*/
    String DEFAULT_URL = "https://youtube.com/watch?v=raUIHXgqDKs";
    String CONFIG_FILE_NAME = "config.properties";
    String CONFIG_FILE_LOCATION = System.getProperty("user.home") + File.separator
            + "youtbrowder" + File.separator + CONFIG_FILE_NAME;

    String Youtube_Address_Pattern = "https://www.youtube.com/watch?v=AbWBviQCMEE";
    String YOUTUBE_ADDRESS_BASE = "https://www.youtube.com";

    boolean USEP_ROXY       = false;
    String HTTP_PROXY_HOST  = null;
    String HTTP_PROXY_PORT  = null;
    String HTTPS_PROXY_HOST = null;
    String HTTPS_PROXY_PORT = null;
    String DEFAULT_SAVE_LOCATION = System.getProperty("user.home");

    String JQUERY_LOCATION = "https://code.jquery.com/jquery-3.2.1.min.js";
    String SCRIPT          = "$(\"a:visible\").each(function() {$(this).css(\"text-transform\", \"uppercase\");});";
    String REGEXP_SCRIPT   = "$(\"a[id='video-title']\").each(function() {$(this).css(\"text-transform\", \"uppercase\");});";

    Integer THREADPOOLSIZE = 10;

}
