package bbth.game;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import bbth.engine.achievements.Achievements;
import bbth.engine.core.GameActivity;
import bbth.engine.ui.Anchor;
import bbth.engine.ui.UIButton;
import bbth.engine.ui.UIButtonDelegate;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UINavigationController;
import bbth.engine.ui.UISlider;
import bbth.engine.ui.UISwitch;
import bbth.engine.ui.UIView;

public class SettingsScreen extends UIView {
	
	private static final int Y_OFFSET = 65;
	
	private UISwitch tutorialSwitch, titleScreenMusicSwitch;
	private UISlider aiDifficulty;
	private UILabel tutorial, ai, title, titleScreenMusic;
	private UIButton resetAchievementsButton;
	private boolean showTutorial, playTitleScreenMusic;
	private float ai_level;
	private SharedPreferences _settings;
	private SharedPreferences.Editor _editor;
	
	public SettingsScreen(UINavigationController controller) {
		_settings = BBTHActivity.instance.getSharedPreferences("game_settings", 0);
		_editor = _settings.edit();
		setSize(BBTHGame.WIDTH, BBTHGame.HEIGHT);
		
		title = new UILabel("Settings");
		title.setAnchor(Anchor.TOP_CENTER);
		title.setTextSize(30);
		title.sizeToFit();
		title.setPosition(BBTHGame.WIDTH / 2, BBTHGame.TITLE_TOP);
		
		final float CONTENT_CENTER = BBTHGame.CONTENT_TOP + 8;
		
		tutorial = new UILabel("Show Tutorial");
		tutorial.setAnchor(Anchor.CENTER_LEFT);
		tutorial.setTextSize(16);
		tutorial.sizeToFit();
		tutorial.setPosition(25, CONTENT_CENTER);
		
		tutorialSwitch = new UISwitch();
		tutorialSwitch.setAnchor(Anchor.CENTER_RIGHT);
		tutorialSwitch.setSize(100, 30);
		tutorialSwitch.setPosition(BBTHGame.WIDTH - 25, CONTENT_CENTER);
		tutorialSwitch.setOn(BBTHGame.SHOW_TUTORIAL);
		
		ai = new UILabel("AI Difficulty");
		ai.setAnchor(Anchor.CENTER_LEFT);
		ai.setTextSize(16);
		ai.sizeToFit();
		ai.setPosition(25, CONTENT_CENTER + 2 * Y_OFFSET);
		
		aiDifficulty = new UISlider(0.5f, 1.f, 0.75f);
		aiDifficulty.setAnchor(Anchor.CENTER_RIGHT);
		aiDifficulty.setSize(100, 24);
		aiDifficulty.setPosition(BBTHGame.WIDTH - 25, CONTENT_CENTER + 2 * Y_OFFSET);
		aiDifficulty.setValue(BBTHGame.AI_DIFFICULTY);
		
		titleScreenMusic = new UILabel("Menu Screen Music");
		titleScreenMusic.setAnchor(Anchor.CENTER_LEFT);
		titleScreenMusic.setTextSize(16);
		titleScreenMusic.sizeToFit();
		titleScreenMusic.setPosition(25, CONTENT_CENTER + Y_OFFSET);
		
		titleScreenMusicSwitch = new UISwitch();
		titleScreenMusicSwitch.setAnchor(Anchor.CENTER_RIGHT);
		titleScreenMusicSwitch.setSize(100, 30);
		titleScreenMusicSwitch.setPosition(BBTHGame.WIDTH - 25, CONTENT_CENTER + Y_OFFSET);
		titleScreenMusicSwitch.setOn(BBTHGame.TITLE_SCREEN_MUSIC);

		resetAchievementsButton = new UIButton("Reset Achievements");
		resetAchievementsButton.setAnchor(Anchor.BOTTOM_CENTER);
		resetAchievementsButton.setSize(120, 30);
		resetAchievementsButton.setPosition(BBTHGame.WIDTH / 2, BBTHGame.HEIGHT - 40);
		resetAchievementsButton.setButtonDelegate(new UIButtonDelegate() {
			public void onClick(UIButton button) {
				// Game over dialog
				AlertDialog.Builder confirmReset = new AlertDialog.Builder(GameActivity.instance);
				confirmReset.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Achievements.INSTANCE.lockAll();
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				
				confirmReset.setTitle("Reset all achievement data?");
				AlertDialog dialog = confirmReset.create();
				dialog.show();
			}
		});
		
		addSubview(title);
		addSubview(ai);
		addSubview(aiDifficulty);
		addSubview(tutorial);
		addSubview(tutorialSwitch);
		addSubview(titleScreenMusic);
		addSubview(titleScreenMusicSwitch);
		addSubview(resetAchievementsButton);
	}
	
	@Override
	public void onUpdate(float seconds) {
		super.onUpdate(seconds);
		
		if(showTutorial != tutorialSwitch.isOn())
		{
			showTutorial = tutorialSwitch.isOn();
		    _editor.putBoolean("showTutorial", showTutorial);
		    BBTHGame.SHOW_TUTORIAL = showTutorial;
		    _editor.commit();
		}
		
		if(ai_level != aiDifficulty.getValue())
		{
			ai_level = aiDifficulty.getValue();
		    _editor.putFloat("aiDifficulty", ai_level);
		    BBTHGame.AI_DIFFICULTY = ai_level;
		    _editor.commit();
		}
		
		if(playTitleScreenMusic != titleScreenMusicSwitch.isOn())
		{
			playTitleScreenMusic = titleScreenMusicSwitch.isOn();
		    _editor.putBoolean("titleScreenMusic", playTitleScreenMusic);
		    BBTHGame.TITLE_SCREEN_MUSIC = playTitleScreenMusic;
		    if (playTitleScreenMusic) {
		    	BBTHGame.startTitleMusic();
		    }
		    else
		    {
		    	BBTHGame.stopTitleMusic();
		    }
		    _editor.commit();
		}
	}
	
	

}
