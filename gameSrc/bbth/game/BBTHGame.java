package bbth.game;

import android.app.Activity;
import bbth.engine.core.Game;
import bbth.engine.ui.*;
import bbth.engine.util.Envelope;
import bbth.engine.util.Envelope.OutOfBoundsHandler;

public class BBTHGame extends Game {
	// This is the viewport width and height
	public static final float WIDTH = 320;
	public static final float HEIGHT = 530;
	public static final boolean DEBUG = false;

	public BBTHGame(Activity activity) {
		UINavigationController controller = new UINavigationController(null);
		currentScreen = controller;
		
		controller.push(new TitleScreen(controller));
		
//		controller.push(new BBTHAITest(this));
//		controller.push(new MusicTestScreen(activity));
//		controller.push(new NetworkTestScreen());
//		controller.push(new TransitionTest());
//		controller.push(new GameSetupScreen());
//		controller.push(new CombatTest(this));
	}

	@Override
	public float getWidth() {
		return WIDTH;
	}

	@Override
	public float getHeight() {
		return HEIGHT;
	}
	
	public static final Transition MOVE_LEFT_TRANSITION;
	public static final Transition MOVE_RIGHT_TRANSITION;
	public static final Transition FADE_OUT_FADE_IN_TRANSITION;
	static {
		DefaultTransition transition = new DefaultTransition(.5f);
		{
			Envelope newAlpha = new Envelope(0f, OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
			newAlpha.addLinearSegment(.5f, 255f);
			transition.setNewAlpha(newAlpha);
			
			Envelope oldAlpha = new Envelope(255f, OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
			oldAlpha.addLinearSegment(.5f, 0f);
			transition.setOldAlpha(oldAlpha);
			
			Envelope newX = new Envelope(BBTHGame.WIDTH, OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
			newX.addLinearSegment(.5f, 0f);
			transition.setNewX(newX);
			
			Envelope oldX = new Envelope(0f, OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
			oldX.addLinearSegment(.5f, -BBTHGame.WIDTH);
			transition.setOldX(oldX);
		}
		MOVE_LEFT_TRANSITION = transition;
		
		transition = new DefaultTransition(.5f);
		{
			Envelope newAlpha = new Envelope(0f, OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
			newAlpha.addLinearSegment(.5f, 255f);
			transition.setNewAlpha(newAlpha);
			
			Envelope oldAlpha = new Envelope(255f, OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
			oldAlpha.addLinearSegment(.5f, 0f);
			transition.setOldAlpha(oldAlpha);
			
			Envelope newX = new Envelope(-BBTHGame.WIDTH, OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
			newX.addLinearSegment(.5f, 0f);
			transition.setNewX(newX);
			
			Envelope oldX = new Envelope(0f, OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
			oldX.addLinearSegment(.5f, BBTHGame.WIDTH);
			transition.setOldX(oldX);
		}
		MOVE_RIGHT_TRANSITION = transition;
		
		transition = new DefaultTransition(.5f);
		{
			Envelope newAlpha = new Envelope(0f, OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
			newAlpha.addFlatSegment(.25f);
			newAlpha.addLinearSegment(.25f, 255f);
			transition.setNewAlpha(newAlpha);
			
			Envelope oldAlpha = new Envelope(255f, OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
			oldAlpha.addLinearSegment(.25f, 0f);
			transition.setOldAlpha(oldAlpha);
		}
		FADE_OUT_FADE_IN_TRANSITION = transition;
		
		
	}
}
