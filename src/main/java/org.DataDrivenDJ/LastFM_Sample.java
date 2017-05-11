package org.DataDrivenDJ;

import de.umass.lastfm.*;

import java.util.Collection;

class LastFM_Sample {

	public static void main(String args[]){

        LastFMInterface lastfm_handle = new LastFMInterface(true);
		String country = "Canada";
		
        /* Get top artists in Canada, limited to 50)
    	/*Collection<Artist> artists = lastfm_handle.get_artists(country);
		for (Artist artist : artists) {
		    System.out.println(artist.getName());
		} */ 

		
        /*Get all artists will name  bowie*/
		/*Collection<Artist> artists = lastfm_handle.search_artist("bowie");
		for (Artist artist : artists) {
		    System.out.println(artist.getName());
		} */ 

		/*Get Albumns of an artist, may be limited to 50*/
		/*Collection<Album> albums = lastfm_handle.artist_albums("Rihanna");
		for (Album album : albums) {
		    System.out.println(album.getName());
		}  */

		/*Get tracks in an album of a given artist*/
		Collection<Track> tracks = lastfm_handle.album_tracks("Talk that Talk", "Rihanna");
		for (Track track : tracks) {
		    System.out.println(track.getName());
		}  

	}
	

}