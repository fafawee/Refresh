package com.fagawee.refresh.impl;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewOverlay;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.fagawee.refresh.R;
import com.fagawee.refresh.libs.AnimationAdapter;
import com.fagawee.refresh.libs.BaseAnimationAdapter;
import com.fagawee.refresh.libs.Utils;
import com.fagawee.refresh.svg.SvgView;

/*风车加载风格*/
public class WindMillStyleRefreshAdapter extends BaseAnimationAdapter{

	private static final String SVGPATH ="M41 40L236 235L427 45C427 45 523 -72 751 39L556 237L747 431C747 431 854 527 750 747L551 556L353 749C353 749 245 853 43 746L235 554L41 353C41 353 -60 253 41 40Z";
	private static final int EDGE=25;
	private static final int headerCritical=40;
	private static final int footerCritical=40;
	private Context context;
	private FrameLayout headerView;
	private FrameLayout footerView;
	private ObjectAnimator objanimatior_footer;
	private ObjectAnimator objanimatior_header;
	
	public WindMillStyleRefreshAdapter(Context context) {
		super();
		this.context = context;
		init();
	}

	private void init()
	{
		headerView=buildView(0);
		footerView=buildView(1);
	}
	@SuppressLint("NewApi")
	private FrameLayout buildView(int type)
	{
		FrameLayout view=new FrameLayout(context);				
		FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,Utils.dp2px(context, headerCritical));
		view.setLayoutParams(params);
		SvgView svg=new SvgView(context);
		svg.setAlpha(1f);
		FrameLayout.LayoutParams params_svg=new FrameLayout.LayoutParams(Utils.dp2px(context, EDGE), Utils.dp2px(context, EDGE));
		if (type==0) {
			params_svg.gravity=Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM;
			params_svg.bottomMargin=Utils.dp2px(context, 2);
		}
		else
		{
			params_svg.gravity=Gravity.CENTER_HORIZONTAL|Gravity.TOP;
			params_svg.topMargin=Utils.dp2px(context, 2);
		}
		params_svg.bottomMargin=Utils.dp2px(context, 2);
		svg.setLayoutParams(params_svg);
		svg.setSvgData(SVGPATH);
		svg.setGravity(Gravity.CENTER);
		svg.sesScaleType(ScaleType.FIT_XY);
		svg.getPaint().setColor(Color.RED);		
		ImageView img=new ImageView(context);
		img.setImageResource(R.drawable.refresh_windmill_icon);
		FrameLayout.LayoutParams params_img=new FrameLayout.LayoutParams(Utils.dp2px(context, EDGE), Utils.dp2px(context, EDGE));
		if (type==0) {
			params_img.gravity=Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM;
			params_img.bottomMargin=Utils.dp2px(context, 2);
		}
		else
		{
			params_img.gravity=Gravity.CENTER_HORIZONTAL|Gravity.TOP;
			params_img.topMargin=Utils.dp2px(context, 2);
		}		
		
		img.setLayoutParams(params_img);
		img.setAlpha(0f);
		view.addView(img);
		view.addView(svg);
		return view;
	}
	@SuppressLint("NewApi")
	@Override
	public void startHeaderRefresh() {		
		ImageView img=(ImageView) headerView.getChildAt(0);
		SvgView svg=(SvgView) headerView.getChildAt(1);
		img.setAlpha(1f);
		svg.setAlpha(0f);
		if (objanimatior_header==null) {
			objanimatior_header=ObjectAnimator.ofFloat(img, "rotation", -360);
		}
		
		objanimatior_header.setInterpolator(new LinearInterpolator());
		objanimatior_header.setRepeatCount(1000);
		objanimatior_header.setDuration(1000);
		objanimatior_header.start();
		//img.animate().rotation(-360*10000).setInterpolator(new LinearInterpolator()).setDuration(1000*10000).start();
		
	}

	@SuppressLint("NewApi")
	@Override
	public void startFooterRefresh() {
		ImageView img=(ImageView) footerView.getChildAt(0);
		SvgView svg=(SvgView) footerView.getChildAt(1);
		img.setAlpha(1f);
		svg.setAlpha(0f);
		if (objanimatior_footer==null) {
			objanimatior_footer=ObjectAnimator.ofFloat(img, "rotation", -360);
		}	
		objanimatior_footer.setInterpolator(new LinearInterpolator());
		objanimatior_footer.setRepeatCount(1000);
		objanimatior_footer.setDuration(1000);
		objanimatior_footer.start();
		
	}

	@SuppressLint("NewApi")
	@Override
	public void moveHeader(float values) {
		if (objanimatior_header!=null) {
			objanimatior_header.cancel();
		}		
		float scale=values/getHeaderCritical();
		ImageView img=(ImageView) headerView.getChildAt(0);
		SvgView svg=(SvgView) headerView.getChildAt(1);
		if (img.getRotation()!=0) {
			img.setRotation(0);
		}
		if (scale<1) {
			img.setAlpha(0f);
			svg.setAlpha(1f);
			svg.setProgress(scale*svg.getLength());
		}
		else
		{
			img.setAlpha(1f);
			svg.setAlpha(0f);
			
		}
		
	}

	@SuppressLint("NewApi")
	@Override
	public void moveFooter(float values) {
		if (objanimatior_footer!=null) {
			objanimatior_footer.cancel();
		}
		float scale=values/getHeaderCritical();
		ImageView img=(ImageView) footerView.getChildAt(0);
		SvgView svg=(SvgView) footerView.getChildAt(1);
		if (img.getRotation()!=0) {
			img.setRotation(0);
		}
		if (scale<1) {
			img.setAlpha(0f);
			svg.setAlpha(1f);
			svg.setProgress(scale*svg.getLength());
		}
		else
		{
			img.setAlpha(1f);
			svg.setAlpha(0f);
			
		}
		
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

}
