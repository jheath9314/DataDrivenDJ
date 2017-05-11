package org.DataDrivenDJ;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Scraper1 implements LyricScraperIF {

  private String base = "website here";

  public String getLyrics(String artist, String song) throws IOException {

    artist = artist.replace("The", "").replace("the", "");
    //song = song.replace("'", "");
    song = song.replaceAll("[^a-zA-Z0-9]", "");
    String
        url =
        base + artist.replaceAll("\\s+", "").toLowerCase() + "/" + song.replaceAll("\\s+", "")
            .toLowerCase() + ".html";

    try {
      Document doc = Jsoup.connect(url)
          .userAgent(
              "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
          .referrer("http://www.google.com").get();
      // String data
      String lyrics = "";
      if (doc != null) {
        lyrics = doc.select("div").get(21).toString();
        lyrics = this.cleanLyricsHtml(lyrics);
      }

      return lyrics;
    } catch (Exception e) {
      System.out.println("Exceptino from AZLyrics for " + song + " by " + artist);
      return null;
    }

  }

  private String cleanLyricsHtml(String lyrics) {

    lyrics = lyrics.replaceAll("<[^>]*>", "");
    lyrics = lyrics.replace("\n", "");

    return lyrics;
  }

  private String cleanArtistName(String song) {
    return "";
  }

  private String cleanSongName(String artist) {
    return "";
  }

}
