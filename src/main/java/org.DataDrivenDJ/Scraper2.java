package org.DataDrivenDJ;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Scraper2 implements LyricScraperIF{

    public String url = "";

    public String getLyrics(String artist, String song) {
        Document doc = null;
        song = song.trim();
        artist = artist.replace(" ", "_");
        String url = "web site" + artist + ":" + song;
        try {
            doc = Jsoup.connect(url).get();
        } catch (Exception e) {
            System.out.println("Site exception for " + song + " by " + artist);
            e.printStackTrace();
            return null;
        }

        String lyrics = "";

        Elements ele = doc.getElementsByClass("lyric");

        lyrics = ele.toString();
        lyrics = cleanLyricsHtml(lyrics);

        return lyrics;

    }

    private String cleanLyricsHtml(String lyrics)
    {

        lyrics = lyrics.replaceAll("<[^>]*>", "");
        lyrics = lyrics.replace("\n", "");

        return lyrics;
    }
}
