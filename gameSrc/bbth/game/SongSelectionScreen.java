package bbth.game;

import bbth.engine.net.bluetooth.Bluetooth;
import bbth.engine.net.simulation.LockStepProtocol;
import bbth.engine.ui.Anchor;
import bbth.engine.ui.UIButton;
import bbth.engine.ui.UIButtonDelegate;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UIScrollView;
import bbth.engine.ui.UIView;

public class SongSelectionScreen extends UIScrollView implements
		UIButtonDelegate {
	private Team playerTeam;
	private Bluetooth bluetooth;
	private LockStepProtocol protocol;

	public SongSelectionScreen(Team playerTeam, Bluetooth bluetooth,
			LockStepProtocol protocol) {
		super(null);

		this.playerTeam = playerTeam;
		this.bluetooth = bluetooth;
		this.protocol = protocol;

		this.setSize(BBTHGame.WIDTH, BBTHGame.HEIGHT);

		UILabel title = new UILabel("Song Selection", null);
		title.setAnchor(Anchor.TOP_CENTER);
		title.setTextSize(30.f);
		title.setPosition(BBTHGame.WIDTH / 2.f, 80);
		this.addSubview(title);

		this.addSubview(makeButton(Song.DONKEY_KONG, "Donkey Kong", 0));
		this.addSubview(makeButton(Song.RETRO, "Retro", 1));
		this.addSubview(makeButton(Song.MISTAKE_THE_GETAWAY,
				"Mistake the Getaway", 2));
		this.addSubview(makeButton(Song.JAVLA_SLADDER, "Javla Sladder", 3));
		this.addSubview(makeButton(Song.ODINS_KRAFT, "Odin's Kraft", 4));
		this.addSubview(makeButton(Song.MIGHT_AND_MAGIC, "Might and Magic", 5));
	}

	private UIButton makeButton(Song song, String title, int idx) {
		UIButton button = new UIButton(title, song);
		button.setAnchor(Anchor.CENTER_CENTER);
		button.setSize(BBTHGame.WIDTH * 0.75f, 45);
		button.setPosition(BBTHGame.WIDTH / 2, BBTHGame.HEIGHT / 2 + (idx - 1)
				* 65);
		button.setButtonDelegate(this);
		return button;
	}

	@Override
	public void onTouchUp(UIView sender) {
		// Don't do anything
	}

	@Override
	public void onTouchDown(UIView sender) {
		// Don't do anything
	}

	@Override
	public void onTouchMove(UIView sender) {
		// Don't do anything
	}

	@Override
	public void onClick(UIButton button) {
		nextScreen = new InGameScreen(playerTeam, bluetooth, (Song) button.tag,
				protocol);
	}
}
