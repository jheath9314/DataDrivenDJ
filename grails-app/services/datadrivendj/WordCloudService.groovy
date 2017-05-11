package datadrivendj

import com.gs.collections.impl.factory.Sets
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.DataDrivenDJ.WordCloudBuilder
import org.springframework.boot.autoconfigure.AutoConfigureOrder

@Transactional
class WordCloudService {
    @AutoConfigureOrder
    DatabaseService databaseService;

    WordCloudBuilder wordCloudBuilder = new WordCloudBuilder();

    Map<String, Integer> buildTf(String artistId) {
//        println "Building tf"
        return wordCloudBuilder.buildTF(databaseService.getLyricsForArtist(artistId))
    }

    Map<String, Double> buildIdf(String artistId) {
//        println "Building Idf"
        Long totalSongs = databaseService.countAllSongs()
        Set<String> distinctWords = findDistinctWordsForArtist(artistId);
        Map<String, Double> idf = new HashMap<>(distinctWords.size());

//        println "Found distinct words"
        for (String word: distinctWords) {
            idf.put(word, getIdf(word, totalSongs))
        }

        return idf;
    }

    Map<String, Double> buildTfIdf(String artistId) {
        Map<String, Integer> tfMap = buildTf(artistId)
        Map<String, Double> idfMap = buildIdf(artistId)

        Map<String, Double> tfIdfWordCloud = new HashMap<>(idfMap.size())

        for (String word: Sets.intersect(tfMap.keySet(), idfMap.keySet())) {
            Integer tf = tfMap.get(word)
            Double idf = idfMap.get(word)
            Double tfIdf = tf * idf
//            println "word: ${word} tf: ${tf} idf: ${idf} tfIdf: ${tfIdf}"
            tfIdfWordCloud.put(word, tfIdf)
        }

        return  tfIdfWordCloud.sort({-it.value})
    }


    private Set<String> findDistinctWordsForArtist(String artistId) {
        Set<String> distinctWords = new HashSet<>(1000);

        databaseService.getLyricsForArtist(artistId).each { String song ->
            distinctWords.addAll(wordCloudBuilder.lyricsTokenizer.tokenize(song))
        }

        return distinctWords
    }

//    Map<String, Integer> buildDistinctWords() {
//        List<GroovyRowResult> artists = databaseService.findAllArtists()
//
//        for (GroovyRowResult artist : artists) {
//
//        }
//
//        int totalSongs = databaseService.countAllSongs();
//
//        for (String word: distinctWords){
//            int numSongsWithWord = databaseService.countSongsWithWord(word)
//        }
//        int
//        idf = Math.log()
//
//    }
//
    private double getIdf(String word, Long totalSongs) {
        Long count = databaseService.countSongsWithWord(word)
        if (count == 0L || count == null) {
//            println "Word not in any songs: $word"
            return 0
        }
        double log = Math.log( totalSongs / count )
//        println "word ${word} count ${count} totalSongs ${totalSongs} fraction ${totalSongs / count} log ${log}"
        return log
    }

}
