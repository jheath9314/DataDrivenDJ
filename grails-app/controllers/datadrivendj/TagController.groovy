package datadrivendj
import datadrivendj.DatabaseService
import de.umass.lastfm.Album
import grails.converters.JSON
import groovy.sql.GroovyRowResult
import org.springframework.beans.factory.annotation.Autowired
import grails.converters.JSON

class TagController {

    DatabaseService databaseService

    def list(String songId) {
       render ([tags:  databaseService.findTagsForSong(songId)] as JSON)
    }


    def add(String songId, String tag) {
        databaseService.addTagWeightForSong(songId, tag,1);
        render "Added ${songId}, ${tag}"
    }

    def upvoteTag (String songId, String tag)
    {
        databaseService.updateTagWeightForSong(songId, tag, 1);
        render "Upvoted ${songId}, ${tag}"
    }

    def downvoteTag (String songId, String tag)
    {
        databaseService.updateTagWeightForSong(songId, tag, -1);
        render "Downvoted ${songId}, ${tag}"
    }

    def topTagsArtist (String artistId)
    {
        List<GroovyRowResult> tags = databaseService.findTopTagsOfArtist(artistId);
        render([tags: tags] as JSON)
    }

    def topTagsAlbum (String albumId)
    {
        List<GroovyRowResult> tags = databaseService.findTopTagsOfAlbum(albumId);
        render([tags: tags] as JSON)
    }
}
