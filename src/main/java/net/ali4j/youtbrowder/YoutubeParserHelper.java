package net.ali4j.youtbrowder;

import com.github.axet.vget.vhs.YouTubeParser;

public class YoutubeParserHelper {
    private static YouTubeParser youTubeParser;

    public static YouTubeParser getInstance(){
        if (youTubeParser==null)
            youTubeParser = new YouTubeParser();
        return youTubeParser;
    }
}
