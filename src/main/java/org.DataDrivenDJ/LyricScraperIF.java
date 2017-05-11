package org.DataDrivenDJ;


import java.io.IOException;

public interface LyricScraperIF {
	
	 String getLyrics(String arist, String song) throws  IOException;

}
