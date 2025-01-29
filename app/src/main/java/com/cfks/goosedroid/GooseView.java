/**
 * @Author 
 * @AIDE AIDE+
*/
package com.cfks.goosedroid;

import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import com.cfks.goosedroid.GooseDesktop.*;
import com.cfks.goosedroid.SamEngine.*;

public class GooseView extends View
 {
	private Context ctx;
	private ConfigureActivity ca;
	private boolean canvasInit = false;
	private Handler handler;
	
    public GooseView(Context context,ConfigureActivity ca,int frameRefreshRate) {
        super(context);
		this.ctx = context;
		this.ca = ca;
		this.handler = new Handler();
		handler.postDelayed(new Runnable(){
			@Override
			public void run(){
				postInvalidate();
				handler.postDelayed(this,frameRefreshRate);
			}
		},frameRefreshRate);
    }
    
    public GooseView(Context context,ConfigureActivity ca) {
	    this(context,ca,1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
		if(!canvasInit){
			TheGoose.Init(ctx,canvas,ca);
			canvasInit = true;
		}
		Time.TickTime();
		TheGoose.Tick();
		TheGoose.Render();
    }

	@Override
	protected void onDetachedFromWindow()
	{
		// TODO: Implement this method
		super.onDetachedFromWindow();
		handler.removeCallbacksAndMessages(null);
	}
}

