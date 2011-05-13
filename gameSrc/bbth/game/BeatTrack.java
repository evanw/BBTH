package bbth.game;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import bbth.engine.core.GameActivity;
import bbth.engine.sound.Beat;
import bbth.engine.sound.BeatPattern;
import bbth.engine.sound.BeatPatternParser;
import bbth.engine.sound.BeatTracker;
import bbth.engine.sound.MusicPlayer;
import bbth.engine.sound.MusicPlayer.OnCompletionListener;
import bbth.engine.sound.SoundManager;

/**
 * A complete beat track for a single song. Handles music, hit and miss sounds,
 * scoring.
 * 
 * @author jardini
 * 
 */
public class BeatTrack {
	public enum Song {
		DONKEY_KONG,
		RETRO;
	}

	public static final int BEAT_TRACK_WIDTH = 50;
	public static final float BEAT_LINE_X = 25;
	public static final float BEAT_LINE_Y = BBTHGame.HEIGHT - 50;
	public static final float BEAT_CIRCLE_RADIUS = 2.f + BeatTracker.TOLERANCE / 10.f;

	private static final int MAX_SOUNDS = 8;
	private static final int HIT_SOUND_ID = 0;
	private static final int MISS_SOUND_ID = 1;

	private SoundManager soundManager;
	private BeatTracker beatTracker;
	private int combo;
	private int score;
	private String comboStr;
	// scoreStr;
	private BeatPattern beatPattern;
	private MusicPlayer musicPlayer;
	private List<Beat> beatsInRange;
	private Paint paint;

	public BeatTrack(Song song, OnCompletionListener listener) {
		// Setup song-specific stuff
		switch (song) {
		case DONKEY_KONG:
			beatPattern = BeatPatternParser.parse(R.xml.track1);
			musicPlayer = new MusicPlayer(GameActivity.instance, R.raw.bonusroom);
			break;
		case RETRO:
			beatPattern = BeatPatternParser.parse(R.xml.track2);
			musicPlayer = new MusicPlayer(GameActivity.instance, R.raw.retrobit);
			break;
		}

		// Setup general stuff
		musicPlayer.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MusicPlayer mp) {
				beatTracker = new BeatTracker(musicPlayer, beatPattern);
				beatsInRange = new ArrayList<Beat>();
				mp.play();
			}
		});
		musicPlayer.setOnCompletionListener(listener);

		soundManager = new SoundManager(GameActivity.instance, MAX_SOUNDS);
		soundManager.addSound(HIT_SOUND_ID, R.raw.tambourine);
		soundManager.addSound(MISS_SOUND_ID, R.raw.drum2);

		beatTracker = new BeatTracker(musicPlayer, beatPattern);
		beatsInRange = new ArrayList<Beat>();

		// Setup score stuff
		score = 0;
		// scoreStr = String.valueOf(score);
		combo = 0;
		comboStr = String.valueOf(combo);

		// Setup paint
		paint = new Paint();
		paint.setTextSize(10);
		paint.setStrokeCap(Cap.ROUND);
		paint.setAntiAlias(true);
	}

	public void startMusic() {
		musicPlayer.play();
	}

	public void stopMusic() {
		musicPlayer.stop();
	}

	public void draw(Canvas canvas) {
		// paint.setARGB(127, 0, 0, 0);
		// paint.setStrokeWidth(2.f);
		// canvas.drawRect(0, 0, BEAT_TRACK_WIDTH, BBTHGame.HEIGHT, paint);

		paint.setARGB(127, 255, 255, 255);
		paint.setStrokeWidth(2.f);
		canvas.drawLine(BEAT_LINE_X, 0, BEAT_LINE_X, BEAT_LINE_Y - BEAT_CIRCLE_RADIUS, paint);
		canvas.drawLine(BEAT_LINE_X, BEAT_LINE_Y + BEAT_CIRCLE_RADIUS, BEAT_LINE_X, BBTHGame.HEIGHT, paint);

		beatTracker.drawBeats(beatsInRange, BEAT_LINE_X, BEAT_LINE_Y, canvas, paint);

		paint.setStyle(Style.STROKE);
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(2.f);
		canvas.drawCircle(BEAT_LINE_X, BEAT_LINE_Y, BEAT_CIRCLE_RADIUS, paint);
		paint.setStyle(Style.FILL);

		paint.setColor(Color.WHITE);
		// canvas.drawLine(50, 0, 50, BBTHGame.HEIGHT, paint);
		canvas.drawText(comboStr, 35, BBTHGame.HEIGHT - 10, paint);
		// canvas.drawText(_scoreStr, 25, HEIGHT - 2, _paint);
	}

	public void refreshBeats() {
		// Get beats in range
		beatsInRange = beatTracker.getBeatsInRange(-700, 5000);
	}

	public Beat.BeatType checkTouch(BBTHSimulation sim, float x, float y) {
		Beat.BeatType beatType = beatTracker.onTouchDown();
		boolean isOnBeat = (beatType != Beat.BeatType.REST);
		if (isOnBeat) {
			soundManager.play(HIT_SOUND_ID);
			++score;
			// NOTE: Combos should also be tracked in bbthSimulation
			++combo;
			// scoreStr = String.valueOf(score);
			comboStr = "x" + String.valueOf(combo);
		} else {
			soundManager.play(MISS_SOUND_ID);
			combo = 0;
			comboStr = "x" + String.valueOf(combo);
		}

		return beatType;
	}

	public float getCombo() {
		return combo;
	}

	public boolean isPlaying() {
		return musicPlayer.isPlaying();
	}
}
