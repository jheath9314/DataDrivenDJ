package org.DataDrivenDJ

import datadrivendj.DatabaseService
import datadrivendj.WordCloudService
import grails.converters.JSON
import groovy.sql.GroovyRowResult
import org.DataDrivenDJ.TopicModeler.AlbumTopic
import org.DataDrivenDJ.TopicModeler.TopicModelGenerator
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired

class ArtistController {
    @Autowired
    DatabaseService databaseService;

    @Autowired
    WordCloudService wordCloudService;

    def insert(String name) {
        GroovyRowResult result = databaseService.insertArtist(name);
        if (result == null) {
            render "Unable to find artist: ${name}";
        } else {
            render "Inserted $name";
        }
    }

    def deleteAlbum(String id) {
        boolean result = databaseService.deleteAlbum(id);
        if (result) {
            render "Delete Done";
        } else {
            render "Delete not done";
        }


    }

    def deleteArtist(String id) {
        boolean result = databaseService.deleteArtist(id);
        if (result) {
            render "Delete Done";
        } else {
            render "Delete not done";
        }


    }

    def searchArtist(String name) {
        render([artist: databaseService.searchArtist(name)] as JSON)
    }

    def findAll() {
        return [artists: databaseService.findAllArtists()];
    }

    def findAllJson() {
        render([artists: databaseService.findAllArtists()] as JSON);

    }

    def findAllAlbumsJson() {
        render([albumns: databaseService.findAllAlbums()] as JSON);

    }


    def findAlbumsByArtistJson(String name) {
        List<GroovyRowResult> albums = databaseService.findAlbumsForArtist(name);

        if (!albums) {
            albums = databaseService.insertAlbumsForArtist(name)
        }

        render([albums: albums] as JSON)
    }

    def findSongsByAlbumIdJson(String albumId) {
        if (StringUtils.isBlank(albumId) || albumId == "undefined") {
            render "Must set ?albumId="
            return;
        }
        List<GroovyRowResult> songs = databaseService.findSongsForAlbumId(Integer.valueOf(albumId));
        if (!songs) {
            songs = databaseService.insertSongsForAlbumId(Integer.valueOf(albumId))
        }

        render([songs: songs] as JSON)
    }

    def wordCloudJson(String artistId) {
        Map<String, Integer> wordCloud = wordCloudService.buildTf(artistId);
        render(wordCloud as JSON)
    }

    def tfIdfWordCloudJson(String artistId) {
        Map<String, Double> wordCloud = wordCloudService.buildTfIdf(artistId);
        render(wordCloud as JSON)
    }


    def topicModelByAlbum(int albumId) {

        //This is the key to insert for all albums
        if (albumId == -1)
        {
            List<GroovyRowResult> albums = databaseService.findAllAlbumIDs()

            for(int j = 0; j < albums.size(); j++)
            {
                List<GroovyRowResult> songs = databaseService.findLyricsByAlbumId(albums.get(j).getAt('AlbumId'));
                List<String> topics = new ArrayList<String>();
                //topics.add("test");
                if (songs != null) {
                    TopicModelGenerator TopicGenerator = new TopicModelGenerator();
                    for (int i = 0; i < songs.size(); i++) {
                        //songs.get(i).getAt("lyrics").toString();
                        TopicGenerator.addSong(songs.get(i).getAt("songId"), songs.get(i).getAt("lyrics").toString());
                    }

                    TopicGenerator.runLDA();
                    List<AlbumTopic> docs = TopicGenerator.getDocuments();
                    for (int docId = 0; docId < docs.size(); docId++) {
                        long songId = docs.get(docId).getSongId();
                        HashMap<String, Integer> topicWeight = docs.get(docId).getTopicWeight();

                        for (String topicName : topicWeight.keySet()) {
                            topics.add(topicName)
                            int weight = topicWeight.get(topicName);
                            databaseService.addTagWeightForSong(songId, topicName, weight);
                        }
                        
                    }
                    render([topics: topics] as JSON)
                }
            }
        }
        else
        {


            List<GroovyRowResult> songs = databaseService.findLyricsByAlbumId(albumId);
            List<String> topics = new ArrayList<String>();
            //topics.add("test");
            if (songs != null) {
                //This is just for early integration and testing purposes
                TopicModelGenerator TopicGenerator = new TopicModelGenerator();
                //System.out.format(" Song size  = %d\n", songs.size()); 
                for (int i = 0; i < songs.size(); i++) {
                    //songs.get(i).getAt("lyrics").toString();
                    TopicGenerator.addSong(songs.get(i).getAt("songId"), songs.get(i).getAt("lyrics").toString());
                }

                TopicGenerator.runLDA();
                List<AlbumTopic> docs = TopicGenerator.getDocuments();
                for (int docId = 0; docId < docs.size(); docId++) {
                    long songId = docs.get(docId).getSongId();
                    HashMap<String, Integer> topicWeight = docs.get(docId).getTopicWeight();
                    for (String topicName : topicWeight.keySet()) {
                        topics.add(topicName)
                        int weight = topicWeight.get(topicName);
                        databaseService.addTagWeightForSong(songId, topicName, weight);
                    }
                    
                }
                render([topics: topics] as JSON)
            }
        }
    }
}
