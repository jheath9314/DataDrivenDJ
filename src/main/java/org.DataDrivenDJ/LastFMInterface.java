package org.DataDrivenDJ;

import de.umass.lastfm.*;
import java.util.ArrayList;
import java.util.Collection;

class LastFMInterface {
	private static final String API_KEY = "HIDDEN";
	private static final String USER = "HIDDEN";

	public LastFMInterface() {

	}

	public LastFMInterface(boolean debug_mode) {
    	Caller.getInstance().setDebugMode(debug_mode);
	}

	/*Get top artists from a country, count specifies the number of artists*/

	public static Collection<Artist> get_artists(String country) {
		Collection<Artist> artists = Geo.getTopArtists(country, API_KEY);

		return artists;
	}

	public static Collection<Artist> topArtists () {
		return Chart.getTopArtists(API_KEY).getPageResults();
	}

	/*Search for a given artist name and get a collection of matching artists */
	public static Collection<Artist> search_artist(String artist_name) {
		Collection<Artist> artists = Artist.search(artist_name,API_KEY);
		return artists;
	}

   /*get top albums of a given artist*/
	public static Collection<Album> artist_albums(String artist_name) {
		Collection<Album> albums = Artist.getTopAlbums(artist_name,API_KEY);
		return albums;
	}

	/*search for an album by name*/
	public static Collection<Album> search_albums(String album_name) {
		Collection<Album> albums = Album.search(album_name,API_KEY);
		return albums;
	}

	/*Get all tracks in an album*/
	public static Collection<Track> album_tracks(String album_name, String artist_name) {
		Album album_data = Album.getInfo(artist_name,album_name,API_KEY);
		//System.out.println(album_data);
		return album_data.getTracks();
	}

	/*search for a track by name*/
	public static Collection<Track> search_tracks(String track_name) {
		Collection<Track> tracks = Track.search(track_name,API_KEY);
		return tracks;
	}
    
    /*get top tags for a track*/

	public static Collection<Tag> tracks_tags(String track_name, String artist_name) {
		Collection<Tag> tags = Track.getTopTags(track_name,artist_name,API_KEY);
		return tags;
	}   

	
}