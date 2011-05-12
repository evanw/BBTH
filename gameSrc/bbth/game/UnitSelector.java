package bbth.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import bbth.game.units.Unit;
import bbth.game.units.UnitManager;
import bbth.game.units.UnitType;

public class UnitSelector {
	private static final float UNIT_SELECTOR_WIDTH = 50;
	private static final float UNIT_SELECTOR_HEIGHT = 80;
	private static final float UNIT_HEIGHT = UNIT_SELECTOR_HEIGHT / 3.f;
	private static final RectF DIMENSIONS = new RectF(BBTHGame.WIDTH - UNIT_SELECTOR_WIDTH, 0, BBTHGame.WIDTH, UNIT_SELECTOR_HEIGHT);

	private UnitType currentUnitType;
	private Paint rectPaint, unitPaint;
	private Unit attacker, defender, uberer;

	public UnitSelector(Team team, UnitManager mom) {
		currentUnitType = UnitType.ATTACKING;
		rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		rectPaint.setARGB(127, 0, 0, 0);

		unitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		unitPaint.setStrokeWidth(2.0f);
		unitPaint.setStrokeJoin(Join.ROUND);
		unitPaint.setStyle(Style.STROKE);
		unitPaint.setAntiAlias(true);
		unitPaint.setStrokeCap(Cap.ROUND);

		attacker = UnitType.ATTACKING.createUnit(mom, team, unitPaint);
		attacker.setPosition(DIMENSIONS.centerX(), UNIT_HEIGHT / 2);
		defender = UnitType.DEFENDING.createUnit(mom, team, unitPaint);
		defender.setPosition(DIMENSIONS.centerX(), UNIT_HEIGHT + UNIT_HEIGHT / 2);
		uberer = UnitType.UBER.createUnit(mom, team, unitPaint);
		uberer.setPosition(DIMENSIONS.centerX(), 2 * UNIT_HEIGHT + UNIT_HEIGHT / 2);
	}

	public UnitType getUnitType() {
		return this.currentUnitType;
	}

	public void setUnitType(UnitType type) {
		this.currentUnitType = type;
	}

	public int checkUnitChange(float x, float y) {
		if (!DIMENSIONS.contains(x, y))
			return -1;

		if (y < UNIT_HEIGHT) {
			return 0;
		} else if (y < UNIT_HEIGHT * 2) {
			return 1;
		} else {
			return 2;
		}
	}

	public void draw(Canvas canvas) {
		canvas.drawRect(DIMENSIONS, rectPaint);

		if (this.currentUnitType == UnitType.ATTACKING) {
			unitPaint.setColor(Color.WHITE);
			attacker.draw(canvas);
			unitPaint.setColor(Color.GRAY);
			defender.draw(canvas);
			uberer.draw(canvas);
		} else if (this.currentUnitType == UnitType.DEFENDING) {
			unitPaint.setColor(Color.WHITE);
			defender.draw(canvas);
			unitPaint.setColor(Color.GRAY);
			attacker.draw(canvas);
			uberer.draw(canvas);
		} else {
			unitPaint.setColor(Color.WHITE);
			uberer.draw(canvas);
			unitPaint.setColor(Color.GRAY);
			attacker.draw(canvas);
			defender.draw(canvas);
		}
	}
}
