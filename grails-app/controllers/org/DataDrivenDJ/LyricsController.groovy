package org.DataDrivenDJ
import datadrivendj.DatabaseService
import de.umass.lastfm.Album
import grails.converters.JSON
import groovy.sql.GroovyRowResult
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired

class LyricsController {
    @Autowired
    DatabaseService databaseService;
    def index() { }

    def insertSongLyrics(String songID) {
        if (!songID){
            render "Must provide ?songID="
            return
        }
        boolean result = databaseService.insertLyricsBySongId(songID);
        if (!result) {
            render "Lyrics not inserted.  They may already exist.";
        } else {
            render "Inserted $songID lyrics";
        }
    }

    def insertAllSongLyrics()
    {
        List<GroovyRowResult> result = databaseService.findAllSongIDs();
        if(result != null)
        {
            for(int i = 0; i < result.size(); i++)
            {
                String songID =result?.get(i)?.getAt("songID")?.toString();
                if (StringUtils.isBlank(songID)) {
                    throw new IllegalArgumentException("songID is not provided")
                }
                databaseService.insertLyricsBySongId(songID);
                Thread.sleep(200);
            }
        }
        render "Done"
    }


    def getSong(String songID) {
        if (StringUtils.isBlank(songID) || songID == "undefined") {
           render ("Blank or undefined songID");
            return
        }
        GroovyRowResult song = databaseService.getSongById(songID);
        if (song == null) {
            render "Song not found"
            return
        }
        render (song as JSON)
    }

    def update(String songID, String lyrics) {
        databaseService.updateLyricsForSong(songID, lyrics)
        render "Updated $songID to $lyrics"
    }

    def findSongsWithWord(String word) {
        render ([songs: databaseService.findSongsWithWord(word)] as JSON)
    }

}
