package bbth.game;

import java.util.Collection;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Paint.Align;
import android.util.DisplayMetrics;
import bbth.engine.achievements.AchievementInfo;
import bbth.engine.achievements.AchievementView;
import bbth.engine.achievements.Achievements;
import bbth.engine.ui.Anchor;
import bbth.engine.ui.UIButton;
import bbth.engine.ui.UIButtonDelegate;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UINavigationController;
import bbth.engine.ui.UIScrollView;
import bbth.game.achievements.BBTHAchievement;
import bbth.game.achievements.BBTHAchievementManager;

/**
 * A list of achievements with locked / unlocked status and descriptions
 * @author Justin
 *
 */
public class AchievementsScreen extends UIScrollView implements UIButtonDelegate {

	private final static int ACHIEVEMENT_HEIGHT = 70;
	
	private Bitmap _lockedImage;

	// descriptions maps achievement names to full descriptions for unlocked achievements
	public AchievementsScreen(UINavigationController navController) {
		super(null);

		setScrollsHorizontal(false);

		Map<Integer, Integer> activations = Achievements.INSTANCE.getAll();
		Collection<BBTHAchievement> achievements = BBTHAchievementManager.INSTANCE.getAchievements();
		setSize(BBTHGame.WIDTH, BBTHGame.HEIGHT);
		
		// add title
		UILabel titleLabel = new UILabel("Achievements", null);
		titleLabel.setTextSize(30.f);
		titleLabel.setAnchor(Anchor.CENTER_CENTER);
		titleLabel.setPosition(BBTHGame.WIDTH / 2, 80);
		titleLabel.setTextAlign(Align.CENTER);
		addSubview(titleLabel);
		
		// add achievement subviews
		float y = BBTHGame.HEIGHT / 2 - 65;
		for (BBTHAchievement achievement : achievements) {
			AchievementInfo info = achievement.achievementInfo;
			Integer id = info.id;
			Bitmap image;
			AchievementView view;

			if (activations.containsKey(id)) {
				// if fully unlocked
				if (activations.get(id) == info.maxActivations) {
					image = info.image;
				} else {
					image = getLockedImage();
				}
			} else {
				// add to sharedPreferences if its not yet there
				Achievements.INSTANCE.lock(info);
				image = getLockedImage();
			}
			view = new AchievementView(info, activations.get(id), image);
			view.setAnchor(Anchor.CENTER_LEFT);
			view.setPosition(0, y);
			view.setSize(BBTHGame.WIDTH, ACHIEVEMENT_HEIGHT);
			addSubview(view);
			y += ACHIEVEMENT_HEIGHT;
		}
		
		Achievements.INSTANCE.commit();
	}
	
	public final Bitmap getLockedImage() {
		if (_lockedImage == null) {
			Options options = new Options();
			options.inTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
			Bitmap image = BitmapFactory.decodeResource(BBTHActivity.instance.getResources(), R.drawable.padlock);
			_lockedImage = Bitmap.createScaledBitmap(image, 32, 32, true);
		}
		
		return _lockedImage;
	}

	@Override
	public void onClick(UIButton button) {
	}

}
