package bbth.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import bbth.engine.fastgraph.Wall;
import bbth.engine.net.bluetooth.Bluetooth;
import bbth.engine.net.bluetooth.State;
import bbth.engine.net.simulation.LockStepProtocol;
import bbth.engine.particles.ParticleSystem;
import bbth.engine.sound.Beat.BeatType;
import bbth.engine.sound.MusicPlayer;
import bbth.engine.sound.MusicPlayer.OnCompletionListener;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UIView;
import bbth.engine.util.Timer;
import bbth.game.Song;

public class InGameScreen extends UIView implements OnCompletionListener {
	private BBTHSimulation sim;
	private Bluetooth bluetooth;
	private Team team;
	private BeatTrack beatTrack;
	private Wall currentWall;
	private ParticleSystem particles;
	private Paint paint;
	private UILabel label;
	private static final boolean USE_UNIT_SELECTOR = false;
	private static final long TAP_HINT_DISPLAY_LENGTH = 3000;

	private Timer entireUpdateTimer = new Timer();
	private Timer simUpdateTimer = new Timer();
	private Timer entireDrawTimer = new Timer();
	private Timer drawParticleTimer = new Timer();
	private Timer drawSimTimer = new Timer();
	private Timer drawUITimer = new Timer();

	public ComboCircle combo_circle;
	private boolean userScrolling;
	private Tutorial tutorial;
	private long tap_location_hint_time;

	public InGameScreen(Team playerTeam, Bluetooth bluetooth, Song song,
			LockStepProtocol protocol) {
		this.team = playerTeam;
		tutorial = new Tutorial(team == Team.SERVER);

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		tap_location_hint_time = 0;

		// Test labels
		label = new UILabel("", null);
		label.setTextSize(10);
		label.setPosition(10, 10);
		label.setSize(BBTHGame.WIDTH - 20, 10);
		label.setTextAlign(Align.CENTER);
		addSubview(label);

		this.bluetooth = bluetooth;
		sim = new BBTHSimulation(playerTeam, protocol, team == Team.SERVER);
		BBTHSimulation.PARTICLES.reset();

		// Set up sound stuff
		beatTrack = new BeatTrack(song, this);

		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(2.0f);
		paint.setStrokeJoin(Join.ROUND);
		paint.setTextSize(20);
		paint.setAntiAlias(true);

		paint.setStrokeWidth(2.f);
		particles = new ParticleSystem(200, 0.5f);
	}

	@Override
	public void onStop() {
		beatTrack.stopMusic();

		// Disconnect when we lose focus
		bluetooth.disconnect();
	}

	@Override
	public void onDraw(Canvas canvas) {
		entireDrawTimer.start();
		super.onDraw(canvas);

		// Draw the game
		drawSimTimer.start();
		canvas.save();
		canvas.translate(BBTHSimulation.GAME_X, BBTHSimulation.GAME_Y);

		sim.draw(canvas);

		paint.setColor(team.getTempWallColor());
		paint.setStrokeCap(Cap.ROUND);
		if (currentWall != null) {
			canvas.drawLine(currentWall.a.x, currentWall.a.y, currentWall.b.x,
					currentWall.b.y, paint);
		}
		paint.setStrokeCap(Cap.BUTT);

		drawParticleTimer.start();
		particles.draw(canvas, paint);
		drawParticleTimer.stop();

		canvas.restore();
		drawSimTimer.stop();

		drawUITimer.start();
		// Overlay the beat track
		beatTrack.draw(canvas);

		// Overlay the unit selector
		if (USE_UNIT_SELECTOR) {
			sim.getMyUnitSelector().draw(canvas);
		}
		drawUITimer.stop();

		if (!tutorial.isFinished()) {
			tutorial.draw(canvas);
		} else if (!sim.isReady()) {
			paint.setColor(Color.WHITE);
			paint.setTextSize(20);
			paint.setTextAlign(Align.CENTER);
			canvas.drawText("Waiting for other player...",
					BBTHSimulation.GAME_X + BBTHSimulation.GAME_WIDTH / 2,
					BBTHSimulation.GAME_Y + BBTHSimulation.GAME_HEIGHT / 2,
					paint);
		}
		
		long time_since_hint_start = System.currentTimeMillis() - tap_location_hint_time;
		if (time_since_hint_start < TAP_HINT_DISPLAY_LENGTH) {
			paint.setColor(Color.WHITE);
			paint.setStyle(Style.STROKE);
			paint.setTextSize(18.0f);
			paint.setAlpha((int) (255 - (time_since_hint_start/4%255)));
			canvas.drawText("Tap further right ", BBTHGame.WIDTH/4.0f, BBTHGame.HEIGHT * .75f + 20, paint);
			canvas.drawText("to make units!", BBTHGame.WIDTH/4.0f, BBTHGame.HEIGHT * .75f + 45, paint);
			canvas.drawRect(BBTHGame.WIDTH/4, BBTHGame.HEIGHT * .75f + 60, BBTHGame.WIDTH/4 + 30, BBTHGame.HEIGHT * .75f + 70, paint);
			canvas.drawLine(BBTHGame.WIDTH/4 + 30, BBTHGame.HEIGHT * .75f + 55, BBTHGame.WIDTH/4 + 30, BBTHGame.HEIGHT * .75f + 65, paint);
			canvas.drawLine(BBTHGame.WIDTH/4 + 30, BBTHGame.HEIGHT * .75f + 55, BBTHGame.WIDTH/4 + 40, BBTHGame.HEIGHT * .75f + 65, paint);
			canvas.drawLine(BBTHGame.WIDTH/4 + 30, BBTHGame.HEIGHT * .75f + 75, BBTHGame.WIDTH/4 + 40, BBTHGame.HEIGHT * .75f + 65, paint);
		}

		if (BBTHGame.DEBUG) {
			// Draw timing information
			paint.setColor(Color.argb(63, 255, 255, 255));
			paint.setTextSize(8);
			int x = 80;
			int y = 30;
			int jump = 11;
			canvas.drawText(
					"Entire update: " + entireUpdateTimer.getMilliseconds()
							+ " ms", x, y += jump, paint);
			canvas.drawText("- Sim update: " + simUpdateTimer.getMilliseconds()
					+ " ms", x, y += jump, paint);
			canvas.drawText(
					"  - Sim tick: " + sim.entireTickTimer.getMilliseconds()
							+ " ms", x, y += jump, paint);
			canvas.drawText(
					"    - AI tick: " + sim.aiTickTimer.getMilliseconds()
							+ " ms", x, y += jump, paint);
			canvas.drawText(
					"      - Controller: "
							+ sim.aiControllerTimer.getMilliseconds() + " ms",
					x, y += jump, paint);
			canvas.drawText(
					"      - Server player: "
							+ sim.serverPlayerTimer.getMilliseconds() + " ms",
					x, y += jump, paint);
			canvas.drawText(
					"      - Client player: "
							+ sim.clientPlayerTimer.getMilliseconds() + " ms",
					x, y += jump, paint);
			canvas.drawText("Entire draw: " + entireDrawTimer.getMilliseconds()
					+ " ms", x, y += jump * 2, paint);
			canvas.drawText("- Sim: " + drawSimTimer.getMilliseconds() + " ms",
					x, y += jump, paint);
			canvas.drawText(
					"- Particles: " + drawParticleTimer.getMilliseconds()
							+ " ms", x, y += jump, paint);
			canvas.drawText("- UI: " + drawUITimer.getMilliseconds() + " ms",
					x, y += jump, paint);
		}

		if (BBTHGame.DEBUG && !sim.isSynced()) {
			paint.setColor(Color.RED);
			paint.setTextSize(40);
			paint.setTextAlign(Align.CENTER);
			canvas.drawText("NOT SYNCED!", BBTHSimulation.GAME_X
					+ BBTHSimulation.GAME_WIDTH / 2, BBTHSimulation.GAME_Y
					+ BBTHSimulation.GAME_HEIGHT / 2, paint);
		}
		entireDrawTimer.stop();
	}

	@Override
	public void onUpdate(float seconds) {
		entireUpdateTimer.start();

		// Show the timestep for debugging
		label.setText("" + sim.getTimestep());

		if (!BBTHGame.IS_SINGLE_PLAYER) {
			// Stop the music if we disconnect
			if (bluetooth.getState() != State.CONNECTED) {
				beatTrack.stopMusic();
				nextScreen = new GameStatusMessageScreen.DisconnectScreen();
			}
		}

		// Update the game
		simUpdateTimer.start();
		sim.onUpdate(seconds);
		simUpdateTimer.stop();

		// Update the tutorial
		if (!tutorial.isFinished()) {
			tutorial.update(seconds);
			if (tutorial.isFinished()) {
				sim.recordCustomEvent(0, 0, BBTHSimulation.TUTORIAL_DONE);
			}
		}

		// Start the music
		if (sim.isReady() && !beatTrack.isPlaying()) {
			beatTrack.startMusic();
		}

		// See whether we won or lost
		if (sim.localPlayer.getHealth() <= 0.f) {
			// We lost the game!
			beatTrack.stopMusic();
			this.nextScreen = new GameStatusMessageScreen.LoseScreen();
		}

		if (sim.remotePlayer.getHealth() <= 0.f) {
			// We won the game!
			beatTrack.stopMusic();
			this.nextScreen = new GameStatusMessageScreen.WinScreen();
		}

		// Get new beats, yo
		beatTrack.refreshBeats();

		// Shinies
		particles.tick(seconds);
		entireUpdateTimer.stop();

		if (BBTHGame.IS_SINGLE_PLAYER) {
			sim.update(seconds);
		}
	}

	@Override
	public void onTouchDown(float x, float y) {
		if (!BBTHGame.DEBUG && !tutorial.isFinished()) {
			tutorial.touchDown(x, y);
			return;
		}

		if (USE_UNIT_SELECTOR) {
			int unitType = sim.getMyUnitSelector().checkUnitChange(x, y);
			if (unitType >= 0) {
				if (BBTHGame.IS_SINGLE_PLAYER) {
					sim.simulateCustomEvent(0, 0, unitType, true);
				} else {
					sim.recordCustomEvent(0, 0, unitType);
				}
				return;
			}
		}

		BeatType beatType = beatTrack.checkTouch(x, y);

		// Unpack!
		boolean isHold = (beatType == BeatType.HOLD);
		boolean isOnBeat = (beatType != BeatType.REST);

		x -= BBTHSimulation.GAME_X;
		y -= BBTHSimulation.GAME_Y;
		
		if (x < 0) {
			// Display a message saying they should tap in-bounds
			tap_location_hint_time = System.currentTimeMillis();
		}

		if (isOnBeat && isHold && x > 0 && y > 0) {
			currentWall = new Wall(x, y, x, y);
		}

		if (BBTHGame.IS_SINGLE_PLAYER) {
			sim.simulateTapDown(x, y, true, isHold, isOnBeat);
		} else {
			sim.recordTapDown(x, y, isHold, isOnBeat);
		}
	}

	@Override
	public void onTouchMove(float x, float y) {
		if (!tutorial.isFinished()) {
			tutorial.touchMove(x, y);
			return;
		}

		// We moved offscreen!
		x -= BBTHSimulation.GAME_X;
		y -= BBTHSimulation.GAME_Y;

		if (x < 0 || y < 0) {
			simulateWallGeneration();
		} else if (currentWall != null) {
			currentWall.b.set(x, y);
		}

		if (BBTHGame.IS_SINGLE_PLAYER) {
			sim.simulateTapMove(x, y, true);
		} else {
			sim.recordTapMove(x, y);
		}
	}

	@Override
	public void onTouchUp(float x, float y) {
		if (!tutorial.isFinished()) {
			tutorial.touchUp(x, y);
			return;
		}

		if (userScrolling) {
			userScrolling = false;
			return;
		}

		x -= BBTHSimulation.GAME_X;
		y -= BBTHSimulation.GAME_Y;

		simulateWallGeneration();

		if (BBTHGame.IS_SINGLE_PLAYER) {
			sim.simulateTapUp(x, y, true);
		} else {
			sim.recordTapUp(x, y);
		}
	}

	public void simulateWallGeneration() {
		if (currentWall == null)
			return;

		currentWall.updateLength();

		if (currentWall.length >= BBTHSimulation.MIN_WALL_LENGTH) {
			sim.generateParticlesForWall(currentWall, this.team);
		}

		currentWall = null;
	}

	/**
	 * Stupid method necessary because of android's weird context/activity mess.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode) {
		bluetooth.onActivityResult(requestCode, resultCode);
	}

	@Override
	public void onCompletion(MusicPlayer mp) {
		float myHealth = sim.localPlayer.getHealth();
		float theirHealth = sim.remotePlayer.getHealth();

		if (myHealth < theirHealth) {
			beatTrack.stopMusic();
			nextScreen = new GameStatusMessageScreen.LoseScreen();
		} else if (myHealth > theirHealth) {
			beatTrack.stopMusic();
			nextScreen = new GameStatusMessageScreen.WinScreen();
		} else {
			beatTrack.stopMusic();
			nextScreen = new GameStatusMessageScreen.TieScreen();
		}
	}
}
