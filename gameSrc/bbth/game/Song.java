package bbth.game;

/**
 * Each song is made up of a song and a beat track
 * @author jardini
 *
 */
public enum Song {
	DONKEY_KONG(R.raw.bonusroom, R.xml.donkey_kong, -1),
	GERUDO(R.raw.gerudovalley, R.xml.track2, -2),
	SONG_OF_STORMS_TECHNO(R.raw.stormoftechno, R.xml.track2, -3),
	RETRO(R.raw.retrobit, R.xml.track1, -4),
	MISTAKE_THE_GETAWAY(R.raw.mistakethegetaway, R.xml.mistake_the_getaway, -5),
	JAVLA_SLADDER(R.raw.javlasladdar, R.xml.track2, -6),
	ODINS_KRAFT(R.raw.odinskraft, R.xml.track2, -7),
	MIGHT_AND_MAGIC(R.raw.mightandmagic, R.xml.track2, -8);
	
	private Song(int song, int track, int id) {
		songId = song;
		trackId = track;
		
		this.id = id;
	}

	public final int id;
	public final int songId;
	public final int trackId;
	
	public static Song fromInt(int i) {
		switch (i) {
		case -1:
			return Song.DONKEY_KONG;
		case -2:
			return Song.GERUDO;
		case -3:
			return Song.SONG_OF_STORMS_TECHNO;
		case -4:
			return Song.RETRO;
		case -5:
			return Song.MISTAKE_THE_GETAWAY;
		case -6:
			return Song.JAVLA_SLADDER;
		case -7:
			return Song.ODINS_KRAFT;
		case -8:
			return Song.MIGHT_AND_MAGIC;
		default:
			return null;
		}
	}
}