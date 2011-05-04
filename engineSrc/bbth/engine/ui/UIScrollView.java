package bbth.engine.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import bbth.engine.util.MathUtils;

public class UIScrollView extends UIView {
	
	protected float dx, dy, _x, _y, pos_x, pos_y, max_x, max_y, max_offset_y, max_offset_x;
	protected boolean isScrolling, isDown, scrollsHorizontal = true, scrollsVertical = true, scrollEnabled = true;
	protected RectF _content_bounds, _v_scroll_handle_rect, _v_track_rect, _h_track_rect, _h_scroll_handle_rect;
	protected Paint _scroll_paint, _track_paint;
	
	public UIScrollView(Object tag) {
		super(tag);
		
		_content_bounds = new RectF();
		_v_scroll_handle_rect = new RectF();
		_v_track_rect = new RectF();
		_h_scroll_handle_rect = new RectF();
		_h_track_rect = new RectF();
		
		_scroll_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		_scroll_paint.setColor(Color.DKGRAY);
		_scroll_paint.setAlpha(255);
		
		_track_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		_track_paint.setColor(Color.WHITE);
		_track_paint.setAlpha(200);
		
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.save();

		if(!_hasAppeared)
			willAppear(true);
		
		canvas.clipRect(_rect, Region.Op.INTERSECT);
		canvas.translate(-pos_x, -pos_y);
		int idx = subviewCount;
    	while(idx-- > 0){
    		UIView e = subviews.get(idx);
    		if(e._rect.intersects(_rect.left + pos_x, _rect.top + pos_y, _rect.right + pos_x, _rect.bottom + pos_y))
    			e.onDraw(canvas);
    	}
    	canvas.restore();
		
		
		if(isScrolling && scrollEnabled)
		{
			if(scrollsVertical)
			{
				canvas.drawRoundRect(_v_track_rect, 5,5, _track_paint);
				canvas.drawRoundRect(_v_scroll_handle_rect, 5, 5, _scroll_paint);
			}
			
			if(scrollsHorizontal)
			{
				canvas.drawRoundRect(_h_track_rect, 5,5, _track_paint);
				canvas.drawRoundRect(_h_scroll_handle_rect, 5, 5, _scroll_paint);
			}
		}
	}

	@Override
	public void onTouchDown(float x, float y) {
		boolean ignore = false;
		
		int idx = subviewCount;
		while(idx-- > 0)
		{
			UIView e = subviews.get(idx);
			if(e.containsPoint(x+pos_x, y+pos_y))
			{
    		  ignore = true;
    		  e.onTouchDown(x+pos_x, y+pos_y);
			}
		}
		
	    if(!ignore)
	    {
	    	isDown = true;
	    	isScrolling = true;
	    	_x = x;
	    	_y = y;
	    }
	}

	@Override
	public void onTouchUp(float x, float y) {
		if(!(isDown && scrollEnabled))
		{
			super.onTouchUp(x+pos_x, y+pos_y);
		}
		isDown = false;
		//TEMP
		isScrolling = false;
		//END TEMP

	}

	@Override
	public void onTouchMove(float x, float y) {
		if(isDown && scrollEnabled)
		{
			if(scrollsVertical && max_y > 0)
			{
				dy = (_y - y);
				pos_y = MathUtils.clamp(0, max_y, pos_y+dy);
				_v_scroll_handle_rect.offsetTo(_v_scroll_handle_rect.left, _v_track_rect.top + (pos_y/max_y) * max_offset_y);
			}
			if(scrollsHorizontal && max_x > 0)
			{
				dx = (_x - x);
				pos_x = MathUtils.clamp(0, max_x, pos_x + dx);
				_h_scroll_handle_rect.offsetTo(_h_track_rect.left + (pos_x/max_x) * max_offset_x, _h_scroll_handle_rect.top);
			}
		}else
			super.onTouchMove(x + pos_x, y+pos_y);
	}
	
	@Override
	public void addSubview(UIView view)
	{
		_content_bounds.union(view._rect);
		
		_v_scroll_handle_rect.bottom = _v_scroll_handle_rect.top + getVerticalHandleHeight();
		_h_scroll_handle_rect.right = _h_scroll_handle_rect.left + getHorizontalHandleWidth();
		
		max_offset_y = _v_track_rect.height() - _v_scroll_handle_rect.height();
		max_offset_x = _h_track_rect.width() - _h_scroll_handle_rect.width();
		
		super.addSubview(view);
	}
	
	@Override
	public void setBounds(float left, float top, float right, float bottom) {
		super.setBounds(left, top, right, bottom);
		dx = 0;
		dy = 0;
		pos_y = 0;
		pos_x = 0;
		
		max_y = Math.abs(_content_bounds.height() - _rect.height());
		if(max_y > 0)
			max_y += 8;
		
		max_x = Math.abs(_content_bounds.width() - _rect.width());
		if(max_x > 0)
			max_x += 8;
		
		_v_track_rect.left = right - 8;
		_v_track_rect.right = right;
		_v_track_rect.top = top;
		_v_track_rect.bottom = bottom - 14;
		
		_v_scroll_handle_rect.left = right - 8;
		_v_scroll_handle_rect.right = right;
		_v_scroll_handle_rect.top = top;
		_v_scroll_handle_rect.bottom = top + getVerticalHandleHeight();
		
		_h_track_rect.left = left;
		_h_track_rect.right = right;
		_h_track_rect.top = bottom - 8;
		_h_track_rect.bottom = bottom;
		
		_h_scroll_handle_rect.left = left;
		_h_scroll_handle_rect.right = right - getHorizontalHandleWidth();
		_h_scroll_handle_rect.top = bottom -8;
		_h_scroll_handle_rect.bottom = bottom;
		
		max_offset_y = _v_track_rect.height() - _v_scroll_handle_rect.height();
		max_offset_x = _h_track_rect.width() - _h_scroll_handle_rect.width();
	}
	
	private float getVerticalHandleHeight()
	{
		return MathUtils.clamp(0, _v_track_rect.height(), _rect.height() / _content_bounds.height() *_v_track_rect.height());
	}
	
	private float getHorizontalHandleWidth()
	{
		return MathUtils.clamp(0, _h_track_rect.width(), _rect.width() / _content_bounds.width() *_h_track_rect.width());
	}
	
	public void setScrollsVertical(boolean scrolls)
	{
		scrollsVertical = scrolls;
	}
	
	public void setScrollsHorizontal(boolean scrolls)
	{
		scrollsHorizontal = scrolls;
	}
	
	public void setScrolls(boolean scrolls)
	{
		scrollEnabled = scrolls;
	}
	
	@Override
	protected void layoutSubviews()
	{
		_content_bounds.set(_rect);
		int idx = subviewCount;
		while(idx-- > 0){
    		UIView e = subviews.get(idx);
    		if(!e._layedOut)
    		{
    			e._rect.offset(_rect.left, _rect.top);
    			e.center.x = e._rect.centerX();
    			e.center.y = e._rect.centerY();
    		}
    		_content_bounds.union(e._rect);
		}
		
		_v_scroll_handle_rect.bottom = _v_scroll_handle_rect.top + getVerticalHandleHeight();
		_h_scroll_handle_rect.right = _h_scroll_handle_rect.left + getHorizontalHandleWidth();
		
		max_y = Math.abs(_content_bounds.height() - _rect.height());
		if(max_y > 0)
			max_y += 8;
		max_offset_y = _v_track_rect.height() - _v_scroll_handle_rect.height();
		
		max_x = Math.abs(_content_bounds.width() - _rect.width());
		if(max_x > 0)
			max_x += 8;
		max_offset_x = _h_track_rect.width() - _h_scroll_handle_rect.width();

	}
	
	
	public void scrollTo(float x, float y)
	{
		pos_x = MathUtils.clamp(0, max_x, x);
		pos_y = MathUtils.clamp(0, max_y, y);
		
		if(max_y > 0)
			_v_scroll_handle_rect.offsetTo(_v_scroll_handle_rect.left, _v_track_rect.top + (pos_y/max_y) * max_offset_y);
		
		if(max_x > 0)
		_h_scroll_handle_rect.offsetTo(_h_track_rect.left + (pos_x/max_x) * max_offset_x, _h_scroll_handle_rect.top);
	}


}