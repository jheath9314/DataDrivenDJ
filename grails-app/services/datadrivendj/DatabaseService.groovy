package datadrivendj

import de.umass.lastfm.Album
import de.umass.lastfm.Artist
import de.umass.lastfm.Track
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.DataDrivenDJ.Scraper1
import org.DataDrivenDJ.LastFMInterface
import org.DataDrivenDJ.LyricScraperIF
import org.DataDrivenDJ.Scraper2
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.DataDrivenDJ.TopicModeler.AlbumTopic
import org.DataDrivenDJ.TopicModeler.TopicModelGenerator

import javax.sql.DataSource

@Transactional
class DatabaseService {

    @Autowired
    DataSource dataSource;

    boolean deleteAlbum(id){
        Sql sql = new Sql(dataSource)

        try {
             return (sql.execute("delete from Album where AlbumId = ?", [id]));
        } finally {
            sql.close();
        }
    }

    boolean deleteArtist(id) {
        Sql sql = new Sql(dataSource)

        boolean result = false;

        try {
            result = (sql.execute("delete from Artist where ArtistId = ?", [id]));
            result = true;


        } finally {
            sql.close();
        }
        return result;

    }

    List<GroovyRowResult> searchArtist(String name) {
        Sql sql = new Sql(dataSource)
        try {
            String queryString = "select * from Artist where name like '%${name}%'";
            println queryString
            return sql.rows(queryString);
        } finally {
            sql.close();
        }
    }

    GroovyRowResult getSongById(String id) {
        Sql sql = new Sql(dataSource)
        try {
            return sql.firstRow("select song.*, Album.name as albumName, Artist.Name as artistName, Artist.ArtistId as artistId from song, Album, Artist where song.albumId=Album.AlbumId AND Artist.ArtistId=Album.ArtistId and song.songId=?", [id])
        } finally {
            sql.close();
        }
    }

    void insertArtistWithValidation(String name) {
        Collection<Artist> artistsFound = LastFMInterface.search_artist(name);
        boolean noArtistWasFound = (artistsFound == null || artistsFound.isEmpty());
        if (noArtistWasFound) {
            return;
        }

        Sql sql = new Sql(dataSource)
        try {
            sql.executeInsert("insert into Artist (name) VALUES(?)", [name])
        } finally {
            sql.close();
        }
    }

    void insertArtist(String name) {
        Sql sql = new Sql(dataSource)
        try {
            sql.executeInsert("insert into Artist (name) VALUES(?)", [name])
        } finally {
            sql.close();
        }
    }

    GroovyRowResult findArtist(String name) {
        Sql sql = new Sql(dataSource)
        try {
            return sql.firstRow("select * from Artist where name=?", [name])
        } finally {
            sql.close()
        }
    }

    GroovyRowResult findArtistbyID(int artistId) {
        Sql sql = new Sql(dataSource)
        try {
            return sql.firstRow("select * from Artist where artistId=?", [artistId])
        } finally {
            sql.close()
        }
    }

    GroovyRowResult findAlbumbyID(int albumId) {
        Sql sql = new Sql(dataSource)
        try {
            return sql.firstRow("select * from Album where albumId=?", [albumId])
        } finally {
            sql.close()
        }
    }

    List<GroovyRowResult> findAllArtists() {
        Sql sql = new Sql(dataSource)
        try {
            return sql.rows("select * from Artist")
        } finally {
            sql.close()
        }
    }

    List<GroovyRowResult> findAllAlbums() {
        Sql sql = new Sql(dataSource)
        try {
            return sql.rows("select * from Album")
        } finally {
            sql.close()
        }
    }

    GroovyRowResult findOrInsertArtist(String name) {
        println "Searching db for ${name}"

        GroovyRowResult artist = findArtist(name);
        if (artist) {
            return artist;
        } else {
            println "No existing db record for ${name}"
            insertArtistWithValidation(name);
        }

        return findArtist(name);
    }

    private boolean isGreatestHitsAlbum(String albumName){
        albumName = albumName.toLowerCase();
        return (albumName.contains("unplugged") || albumName.contains("remaster") || albumName.contains("hits") || albumName.contains("edition"))
    }

    List<GroovyRowResult> insertAlbumsForArtist(String artistName) {
        GroovyRowResult artist = findOrInsertArtist(artistName)

        Collection<Album> albums = LastFMInterface.artist_albums(artistName).findAll({Album album -> !isGreatestHitsAlbum(album.getName())})

        if (albums) {
            //The Album class has lots of extra data we don't care about, so this simplifies our objects
            def simpleAlbumObjects = albums.collect({ album -> [name: album.name, year: album.releaseDate?.getYear(), artistId: artist.artistId] })

            Sql sql = new Sql(dataSource)
            try {
                simpleAlbumObjects.each { album ->
//                    println("Album: ${album}")
                    sql.executeInsert("insert into Album (name, year, artistId) values (?,?,?)", [album.name, album.year, album.artistId])

                }
            } finally {
                sql.close()
            }

        }

        return findAlbumsForArtist(artistName);
    }


    List<GroovyRowResult> insertSongsForAlbumId(int albumId) {

        GroovyRowResult album = findAlbumbyID(albumId)
        GroovyRowResult artist = findArtistbyID(album.artistId)
        Collection<Track> tracks = LastFMInterface.album_tracks(album.name, artist.name)
        def simpleTrackObjects = tracks.collect({ track -> [name: track.name] })

        if (tracks) {

            Sql sql = new Sql(dataSource)
            try {
                simpleTrackObjects.each { track ->
//                    println("Album: ${track}")
                    sql.executeInsert("insert into song (name, albumId) values (?,?)", [track.name, album.albumId])
                }
            } finally {
                sql.close()
            }

        }
        //Add lyrics for the songs in an album
        List<GroovyRowResult> result = findSongsForAlbumId(albumId);
        for(int i = 0; i < result.size(); i++) {
            String songID =result?.get(i)?.getAt("songID")?.toString();
            if (StringUtils.isBlank(songID)) {
                throw new IllegalArgumentException("songID is not provided")
            }
            insertLyricsBySongId(songID);
            Thread.sleep(200);
        }

        //Call topic Modeling

        topicModelByAlbum(albumId);

        return result;
    }

    //Added to support Topic Modeling, not used currently
    List<GroovyRowResult>  insertSongLyricsForTM(int albumId) {
        List<GroovyRowResult> result = findSongsForAlbumId(albumId);

        if (result == null) { 

            List<GroovyRowResult> result_song = insertSongsForAlbumId(albumId);
            if (result_song !=null){
                for(int i = 0; i < result_song.size(); i++) {
                    String songID =result_song?.get(i)?.getAt("songID")?.toString();
                    if (StringUtils.isBlank(songID)) {
                        throw new IllegalArgumentException("songID is not provided")
                    }
                    insertLyricsBySongId(songID);
                    Thread.sleep(200);
                }
            }
        }

        else {
            for(int i = 0; i < result.size(); i++) {
                String songID =result?.get(i)?.getAt("songID")?.toString();
                if (StringUtils.isBlank(songID)) {
                    throw new IllegalArgumentException("songID is not provided")
                }
                insertLyricsBySongId(songID);
                Thread.sleep(200);
            }
        }

        return findLyricsByAlbumId(albumId);
    }

    List<GroovyRowResult> findAlbumsForArtist(String artistName) {
        Sql sql = new Sql(dataSource)
        try {
            return sql.rows("select * from Album where artistId = (select artistId from Artist where name=?)", [artistName])
        } finally {
            sql.close();
        }
    }


    List<GroovyRowResult> findSongsForAlbumId(int albumId) {
        Sql sql = new Sql(dataSource)
        try {
            return sql.rows("select * from song where albumId=? ", [albumId])
        } finally {
            sql.close();
        }
    }

    List<GroovyRowResult> findLyricsByAlbumId(int albumId) {
        Sql sql = new Sql(dataSource);

        try {
            return sql.rows("select songId, lyrics FROM song WHERE albumId=? AND lyrics IS NOT NULL", [albumId])
        }
        finally {
            sql.close();
        }
    }

    List<GroovyRowResult> findTagsForSong(String songId) {
        Sql sql = new Sql(dataSource)
        try {
            return sql.rows("select * from Tag_Song where songId=? ", [songId])
        } finally {
            sql.close();
        }
    }

    void addTagForSong(String songId, String name) {
        Sql sql = new Sql(dataSource)
        try {
            sql.execute("insert into Tag_Song (songId, name) values (?,?)", [songId, name])
        } finally {
            sql.close();
        }
    }

    void addTagWeightForSong(String songId, String name, int weight) {
        Sql sql = new Sql(dataSource)
        try {
            sql.execute("insert into Tag_Song (songId, name, weight) values (?,?,?)", [songId, name,weight])
        }
        catch (Exception e){
            System.err.print("Can't insert tag");
        }
        finally {
            sql.close();
        }

    }

    boolean insertLyricsBySongId(String songID) {
        if (StringUtils.isBlank(songID)) {
            throw new IllegalArgumentException("songID is blank")
        }
        //TODO: Make query a string object
        /*****************************************
         String getSongInformationQuery =
         "select Artist.name as artistName, song.name as songName " +
         "FROM song, Album, Artist " +
         "WHERE song.albumId = Album.AlbumId AND song.songId = ? AND Artist.ArtistId = Album.ArtistId";
         *******************************************/
        Sql sql = new Sql(dataSource);

        try {

            //Don't insert if we already have the lyrics
            if (sql.firstRow("select * from song where songId =? and length(trim(lyrics))> 0", [songID]) != null) {
//                println "Already have lyrics for ${songID}"
                return false;
            }

            GroovyRowResult informationResults = sql.firstRow("select Artist.name as artistName, song.name as songName FROM song, Album, Artist WHERE song.albumId = Album.AlbumId AND song.songId = ? AND Artist.ArtistId = Album.ArtistId", [songID]);


            String artistName = informationResults?.getAt("artistName");
            String songName = informationResults?.getAt("songName");

            if (StringUtils.isBlank(artistName) || StringUtils.isBlank(songName)) {
//                println ("Song with id ${songID} not found")
                return;
            }

            LyricScraperIF myScraper = new Scraper2();

            String lyrics = myScraper.getLyrics(artistName, songName);



            if(StringUtils.isBlank(lyrics))
            {
                //Try az lyrics
                myScraper = new Scraper1();
                lyrics = myScraper.getLyrics(artistName, songName);
            }

            if (StringUtils.isBlank(lyrics)) {
//                println "No lyrics found for ${songName} by ${artistName}"
                return false;
            }

//            println "Setting lyrics for ${songName} by ${artistName} to ${lyrics}"

            return sql.execute("UPDATE  song SET lyrics = ? WHERE songID = ?", [lyrics, songID]);
        }
        finally {
            sql.close();
        }
    }

    List<GroovyRowResult> findAllSongIDs() {
        Sql sql = new Sql(dataSource);
        try {
            return sql.rows("SELECT songID FROM song WHERE lyrics IS NULL OR lyrics = ''");
        }
        finally {
            sql.close();
        }
    }

    List<GroovyRowResult> findAllAlbumIDs() {
        Sql sql = new Sql(dataSource);
        try {
            return sql.rows("SELECT AlbumId FROM Album");
        }
        finally {
            sql.close();
        }
    }

    void updateLyricsForSong(String songId, String lyrics) {
        Sql sql = new Sql(dataSource);
        try {
            sql.executeUpdate("update song set lyrics = ? where songId = ?", [lyrics, songId]);
        }
        finally {
            sql.close();
        }
    }

    List<GroovyRowResult> findSongsWithWord(String word) {
        Sql sql = new Sql(dataSource);
        try {
            String query = "select s.albumId, s.name, s.songId, ar.name as artistName from song s, Album al, Artist ar where s.albumId = al.AlbumId and ar.artistId = al.ArtistId and MATCH (lyrics) AGAINST ('${word}' IN BOOLEAN MODE)"
            return sql.rows(query);
        }
        finally {
            sql.close();
        }
    }

    void updateTagWeightForSong(String songID, String name, int value) {
        Sql sql = new Sql(dataSource);
        try {
            sql.executeUpdate("Update Tag_Song SET weight = weight + ? WHERE SongID = ? and Name = ?", [value, songID, name]);
        }
        finally {
            sql.close();
        }
    }

    List<String> getLyricsForArtist(String artistId) {
        Sql sql = new Sql(dataSource);
        try {
            return sql.rows("SELECT lyrics from song, Album where Album.ArtistId=? " +
                    "and song.albumId=Album.AlbumId and lyrics is not null", [artistId])
                    .collect({ GroovyRowResult row -> row.lyrics })
        }
        finally {
            sql.close()
        }
    }

    Long countAllSongs() {
        Sql sql = new Sql(dataSource);
        try {
            return sql.firstRow("SELECT count(*) as count from song").count;

        }
        finally {
            sql.close()
        }
    }

    Long countSongsWithWord(String word) {
        Sql sql = new Sql(dataSource);
        try {
            String query = "select count(*) as count from song where MATCH (lyrics) AGAINST ('${word}' IN BOOLEAN MODE)"
            return sql.firstRow(query).count;
        }
        finally {
            sql.close();
        }
    }

   /*select DISTINCT Tag_Song.name, Tag_Song.weight from Tag_Song where Tag_Song.SongID in  
                            (select song.songID from song where song.albumId in                             
                            (select Album.albumId from Album where Album.artistId=3)) and Tag_Song.weight  >= ALL(
                            (select  Tag_Song.weight from Tag_Song where Tag_Song.SongID in  
                            (select song.songID from song where song.albumId in                             
                            (select Album.albumId from Album where Album.artistId=3))  group by 1 ))*/

    List<GroovyRowResult> findTopTagsOfArtist(String artistId) {
        Sql sql = new Sql(dataSource);
        try {
           
            return sql.rows("select DISTINCT Tag_Song.name, Tag_Song.weight from Tag_Song where Tag_Song.SongID in " +  
                            "(select song.songID from song where song.albumId in  " +                            
                            "(select Album.albumId from Album where Album.artistId=?)) and Tag_Song.weight  >= ALL(" +
                            "(select  Tag_Song.weight from Tag_Song where Tag_Song.SongID in " + 
                            "(select song.songID from song where song.albumId in " +                             
                            "(select Album.albumId from Album where Album.artistId=?))  group by 1 ))", [artistId,artistId]);
        }
        finally {
            sql.close();
        }
    }

    /*select DISTINCT Tag_Song.name, Tag_Song.weight from Tag_Song where Tag_Song.SongID in  
                            (select song.songID from song where song.albumId=835)                           
                            and Tag_Song.weight  >= ALL(
                            (select  Tag_Song.weight from Tag_Song where Tag_Song.SongID in 
                            (select song.songID from song where song.albumId =835)  group by 1 ))*/

    List<GroovyRowResult> findTopTagsOfAlbum(String albumId) {
        Sql sql = new Sql(dataSource);
        try {        
            return sql.rows("select DISTINCT Tag_Song.name, Tag_Song.weight from Tag_Song where Tag_Song.SongID in " +  
                            "(select song.songID from song where song.albumId=?)" +                           
                            "  and Tag_Song.weight  >= ALL(" +
                            "(select  Tag_Song.weight from Tag_Song where Tag_Song.SongID in " +  
                            "(select song.songID from song where song.albumId =?)  group by 1 ))", [albumId,albumId]);
        }
        finally {
            sql.close();
        }
    }

    void topicModelByAlbum(int albumId) {
        List<GroovyRowResult> songs = findLyricsByAlbumId(albumId);
        List <String> topics = new ArrayList<String>();
        //topics.add("test");
        if(songs != null){
            //This is just for early integration and testing purposes
            TopicModelGenerator TopicGenerator = new TopicModelGenerator();
            for(int i = 0; i < songs.size(); i++) {
                //songs.get(i).getAt("lyrics").toString();
                TopicGenerator.addSong(songs.get(i).getAt("songId"),songs.get(i).getAt("lyrics").toString());
            }

            TopicGenerator.runLDA();
            List<AlbumTopic> docs = TopicGenerator.getDocuments();
            for(int docId = 0; docId < docs.size(); docId++ ){
                long songId = docs.get(docId).getSongId();
                HashMap<String,Integer> topicWeight = docs.get(docId).getTopicWeight();
                for(String topicName : topicWeight.keySet()){
                    topics.add(topicName)
                    int weight = topicWeight.get(topicName);
                    addTagWeightForSong(Long.toString(songId),topicName,weight);
                }
            }
        }
    }

    

}
