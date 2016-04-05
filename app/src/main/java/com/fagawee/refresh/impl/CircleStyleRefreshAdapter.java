package com.fagawee.refresh.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.fagawee.refresh.R;
import com.fagawee.refresh.circularprogressview.CircularProgressView;
import com.fagawee.refresh.libs.BaseAnimationAdapter;
import com.fagawee.refresh.libs.Utils;

public class CircleStyleRefreshAdapter extends BaseAnimationAdapter {

	private static final int headerCritical=40;
	private static final int footerCritical=40;
	private static final int CIRCLEEDGE=35;
	private Context context;
	private FrameLayout headerView;
	private FrameLayout footerView;
	private CircularProgressView headerCircular_indeterminate;
	private CircularProgressView headerCircular_noindeterminate;
	private CircularProgressView footerCircular_indeterminate;
	private CircularProgressView footerCircular_noindeterminate;

	public CircleStyleRefreshAdapter(Context context) {
		super();
		this.context = context;
		init();
	}
	@SuppressLint("NewApi")
	private FrameLayout buildView(int type)
	{
		CircularProgressView progress_indeterminate=new CircularProgressView(context);
		progress_indeterminate.setVisibility(View.INVISIBLE);
		FrameLayout.LayoutParams params_progress_indeterminate=new FrameLayout.LayoutParams(Utils.dp2px(context, CIRCLEEDGE),Utils.dp2px(context, CIRCLEEDGE));
		if (type==0) {
			params_progress_indeterminate.gravity=Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;				
			headerCircular_indeterminate=progress_indeterminate;
		}
		else
		{
			params_progress_indeterminate.gravity=Gravity.TOP|Gravity.CENTER_HORIZONTAL;
			footerCircular_indeterminate=progress_indeterminate;
		}
		progress_indeterminate.setIndeterminate(true);
		progress_indeterminate.setThickness(Utils.dp2px(context, 3));
		progress_indeterminate.setColor(0xff31bcef);
		progress_indeterminate.setBackgroundResource(R.drawable.circle_bg);
		progress_indeterminate.setLayoutParams(params_progress_indeterminate);
		
		progress_indeterminate.startAnimation();
		CircularProgressView progress_noindeterminate=new CircularProgressView(context);
		progress_noindeterminate.setVisibility(View.INVISIBLE);
		FrameLayout.LayoutParams params_progress_noindeterminate=new FrameLayout.LayoutParams(Utils.dp2px(context, CIRCLEEDGE),Utils.dp2px(context, CIRCLEEDGE));
		if (type==0) {
			params_progress_noindeterminate.gravity=Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;				
			headerCircular_noindeterminate=progress_noindeterminate;
		}
		else
		{
			params_progress_noindeterminate.gravity=Gravity.TOP|Gravity.CENTER_HORIZONTAL;
			footerCircular_noindeterminate=progress_noindeterminate;
		}
		progress_noindeterminate.setIndeterminate(false);
		progress_noindeterminate.setMaxProgress(100);
		progress_noindeterminate.setThickness(Utils.dp2px(context, 3));
		progress_noindeterminate.setColor(0xff31bcef);
		progress_noindeterminate.setProgress(0);	
		progress_noindeterminate.setBackgroundResource(R.drawable.circle_bg);
		progress_noindeterminate.setLayoutParams(params_progress_noindeterminate);

		FrameLayout parent=new FrameLayout(context);
		FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
		
		parent.setLayoutParams(params);
		parent.setBackgroundColor(Color.TRANSPARENT);
		parent.addView(progress_indeterminate);
		parent.addView(progress_noindeterminate);
		return parent;
	}
	private void init()
	{
		headerView=buildView(0);
		footerView=buildView(1);
	}
	@Override
	public void startHeaderRefresh() {
		headerCircular_indeterminate.setVisibility(View.VISIBLE);
		headerCircular_noindeterminate.setVisibility(View.INVISIBLE);

	}

	@Override
	public void startFooterRefresh() {
		footerCircular_indeterminate.setVisibility(View.VISIBLE);
		footerCircular_noindeterminate.setVisibility(View.INVISIBLE);

	}

	@SuppressLint("NewApi")
	@Override
	public void moveHeader(float values) {
		headerCircular_indeterminate.setVisibility(View.INVISIBLE);
		headerCircular_noindeterminate.setVisibility(View.VISIBLE);
		float scale=values/getHeaderCritical();
		headerCircular_noindeterminate.setProgress(scale*headerCircular_noindeterminate.getMaxProgress());

	}

	@SuppressLint("NewApi")
	@Override
	public void moveFooter(float values) {
		footerCircular_indeterminate.setVisibility(View.INVISIBLE);
		footerCircular_noindeterminate.setVisibility(View.VISIBLE);
		float scale=values/getFooterCritical();
		footerCircular_noindeterminate.setProgress(scale*footerCircular_noindeterminate.getMaxProgress());

	}

	@Override
	public View getHeaderView() {
		// TODO Auto-generated method stub
		
		return headerView;
	}

	@Override
	public View getFooterView() {
		// TODO Auto-generated method stub
		
		return footerView;
	}

	@Override
	public int getHeaderCritical() {
		// TODO Auto-generated method stub
		 return Utils.dp2px(context, headerCritical);
	}

	@Override
	public int getFooterCritical() {
		// TODO Auto-generated method stub
		return Utils.dp2px(context, footerCritical);
	}
	@Override
	public float getFooterSpeedRatio() {
		// TODO Auto-generated method stub
		return 2;
	}
	@Override
	public float getHeaderSpeedRatio() {
		// TODO Auto-generated method stub
		return 2;
	}

}
