package net.ali4j.youtbrowder.youtube;

import org.apache.log4j.Logger;

/**
 * Created by ehsan on 9/10/2017.
 */
public class YouTubeVideo {

    private static final Logger logger = Logger.getLogger(YouTubeVideo.class);




    private String url;
    private String id;
    private String title;
    private String error;
    private YouTubeURL[] all_videos;

    public YouTubeVideo(String url) {
        this.url = url;
        getVideoIdFromUrl();
        if(this.id != null){
            getVideoInfoFile();
            if(this.error == null) {
//                decodeVideoInfoFile();
                if(this.error == null) {
                    this.error = "OK";
                }
            }
        }
    }

    private void getVideoIdFromUrl() {
        String[] parameters;
        try {
            parameters = this.url.split("\\?")[1].split("\\&");
        }catch(Exception e) {
            logger.error("invalid url");
            return;
        }
        for(int i=0; i<parameters.length;i++){
            if(logger.isDebugEnabled())logger.debug(parameters[i]);
            if(parameters[i].startsWith("v=")) {
                String[] tmp = parameters[i].split("=");
                if(tmp.length>1){
                    this.id = tmp[1];
                }
            }
        }
        if(logger.isDebugEnabled())logger.debug(this.id);
    }

    private void getVideoInfoFile() {
        String fileUrl = "https://youtube.com/get_video_info?video_id=" + this.id;
        int cnt = 0;
        while(true){
            cnt++;
            /*if(Downloader.download(fileUrl, this.id + ".ytd") > 0) {
                break;
            }*/
            if(cnt > 110) {
                break;
            }
        }
        if(cnt > 110){
            this.error = "Error: Connection problem";
            //return false;
        }
        //return true;
    }


}
