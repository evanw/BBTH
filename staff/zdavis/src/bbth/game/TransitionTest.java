package bbth.game;

import android.graphics.*;
import bbth.ui.*;
import bbth.util.Envelope;

public class TransitionTest extends UINavigationController {
	UIView view1 = new UIView("view1") {
		@Override
		public void onDraw(Canvas canvas) {
			canvas.drawRGB(62, 107, 172);
			super.onDraw(canvas);
		}
	};
	UIView view2 = new UIView("view2") {
		@Override
		public void onDraw(Canvas canvas) {
			canvas.drawRGB(255, 255, 255);
			super.onDraw(canvas);
		}
	};
	
	DefaultTransition oneToTwo;
	DefaultTransition twoToOne;
	boolean oneInFront = true;
	
	Envelope always255 = new Envelope(255f, Envelope.OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
	
	public TransitionTest() {
		super("transitiontest");
		UILabel label1 = new UILabel("View One", "l1");
		label1.setTextColor(Color.WHITE);
		view1.addSubview(label1);
		
		UILabel label2 = new UILabel("View Two", "l2");
		label2.setTextColor(Color.BLACK);
		view2.addSubview(label2);
		
		push(view1);
		
		oneToTwo = new DefaultTransition(1f);
		Envelope alpha = new Envelope(0f, Envelope.OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
		alpha.addLinearSegment(1f, 255f);
		oneToTwo.setNewAlpha(alpha);
		
		twoToOne = new DefaultTransition(2f);
		Envelope x = new Envelope(BBTHGame.WIDTH, Envelope.OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
		x.addLinearSegment(2f, 0f);
		twoToOne.setNewX(x);
	}
	

	@Override
	public void onTouchDown(float x, float y) {
		if (oneInFront) {
System.err.println("About to push");
			push(view2, oneToTwo);
			oneInFront = false;
		} else {
System.err.println("About to pop");
			pop(twoToOne);
			oneInFront = true;
		}
	}

}
