package bbth.game;

/**
 * Each song is made up of a song and a beat track
 * @author jardini
 *
 */
public enum Song {
	DONKEY_KONG(R.raw.bonusroom, R.xml.donkey_kong),
	GERUDO(R.raw.gerudovalley, R.xml.track2),
	SONG_OF_STORMS_TECHNO(R.raw.stormoftechno, R.xml.track2),	
	
	RETRO(R.raw.retrobit, R.xml.retro),
	MISTAKE_THE_GETAWAY(R.raw.mistakethegetaway, R.xml.mistake_the_getaway),
	JAVLA_SLADDER(R.raw.javlasladdar, R.xml.track2),
	ODINS_KRAFT(R.raw.odinskraft, R.xml.track2),
	MIGHT_AND_MAGIC(R.raw.mightandmagic, R.xml.might_and_magic);
	
	private Song(int song, int track) {
		songId = song;
		trackId = track;
	}

	public final int songId;
	public final int trackId;
}