package bbth.engine.sound;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * A player for single music track
 * 
 * @author jardini
 * 
 */
public class MusicPlayer {

	public static interface OnCompletionListener {
		void onCompletion(MusicPlayer mp);
	}

	private static final int IDLE = 0;
	private static final int PLAYING = 1;
	private static final int PAUSED = 2;
	private static final int STOPPED = 3;

	private MediaPlayer _mediaPlayer;
	private int _state;
	private long _startTime;

	public MusicPlayer(Context context, int resourceId) {
		_mediaPlayer = MediaPlayer.create(context, resourceId);
		_state = IDLE;
	}

	// passes in a callback that is called when the song ends
	public void setOnCompletionListener(final OnCompletionListener listener) {
		_mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				listener.onCompletion(MusicPlayer.this);
			}
		});
	}

	public int getCurrentPosition() {
		return (int) (System.currentTimeMillis() - _startTime);
	}

	public void seekToPosition(int positionMillis) {
		_mediaPlayer.seekTo(positionMillis);
		_startTime = System.currentTimeMillis() - positionMillis;
	}

	// plays the song once
	public void play() {
		if (_state == IDLE || _state == PAUSED) {
			_mediaPlayer.start();
			_state = PLAYING;
			while (_mediaPlayer.getCurrentPosition() < 1) {
				// wait here, derp
			}
			_startTime = System.currentTimeMillis();
		}
	}

	// loops the song infinitely
	public void loop() {
		_mediaPlayer.setLooping(true);
		play();
	}

	// pauses the song, allowing for continuation from the current point
	public void pause() {
		if (_state == PLAYING) {
			_mediaPlayer.pause();
			_state = PAUSED;
		}
	}

	// stops the song completely
	public void stop() {
		if (_state != STOPPED) {
			_mediaPlayer.stop();
			_state = STOPPED;
		}
	}

	public boolean isLooping() {
		return _mediaPlayer.isLooping();
	}

	public boolean isPlaying() {
		return _state == PLAYING;
	}

	// release the resources associated with the song
	// call this when you are done with the player
	public void release() {
		_mediaPlayer.release();
	}
}
