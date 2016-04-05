package com.fagawee.refresh.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fagawee.refresh.R;
import com.fagawee.refresh.circularprogressview.CircularProgressView;
import com.fagawee.refresh.libs.AnimationAdapter;
import com.fagawee.refresh.libs.BaseAnimationAdapter;
import com.fagawee.refresh.libs.Utils;

/*箭头经典风格的刷新控件*/
public class ArrowStyleRefreshAdapter extends BaseAnimationAdapter {

	private Context context;
	private View v_header;
	private View v_footer;
	private ImageView header_refresh_arrow_icon;
	private TextView header_refresh_text;
	private CircularProgressView header_refresh_arrow_progress;
	private ImageView footer_refresh_arrow_icon;
	private TextView footer_refresh_text;
	private CircularProgressView footer_refresh_arrow_progress;
	private LinearLayout  lin;
	public ArrowStyleRefreshAdapter(Context context) {
		this.context=context;
		init();
		// TODO Auto-generated constructor stub
	}

	
	private void init()
	{
		v_header=View.inflate(context, R.layout.arrow_style_layout_up, null);
		v_footer=View.inflate(context, R.layout.arrow_style_layout_down, null);
		header_refresh_arrow_icon=(ImageView) v_header.findViewById(R.id.refresh_arrow_icon);
		header_refresh_text=(TextView) v_header.findViewById(R.id.refresh_text);
		header_refresh_arrow_progress=(CircularProgressView) v_header.findViewById(R.id.refresh_arrow_progress);
		header_refresh_arrow_progress.post(new Runnable() {
			
			@SuppressLint("NewApi")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				header_refresh_arrow_progress.setPivotX(header_refresh_arrow_progress.getWidth()/2);
				header_refresh_arrow_progress.setPivotY(header_refresh_arrow_progress.getHeight()/2);
			}
		});
	
		lin=(LinearLayout) v_header.findViewById(R.id.lin);		
		lin.setGravity(Gravity.BOTTOM);
		footer_refresh_arrow_icon=(ImageView) v_footer.findViewById(R.id.refresh_arrow_icon);
		footer_refresh_text=(TextView) v_footer.findViewById(R.id.refresh_text);
		footer_refresh_arrow_progress=(CircularProgressView) v_footer.findViewById(R.id.refresh_arrow_progress);
		footer_refresh_arrow_progress.post(new Runnable() {
			
			@SuppressLint("NewApi")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				footer_refresh_arrow_progress.setPivotX(footer_refresh_arrow_progress.getWidth()/2);
				footer_refresh_arrow_progress.setPivotY(footer_refresh_arrow_progress.getHeight()/2);
			}
		});
	}
	
	
	@Override
	public void startHeaderRefresh() {
		header_refresh_arrow_icon.setVisibility(View.GONE);
		header_refresh_arrow_progress.setVisibility(View.VISIBLE);
		header_refresh_text.setText("正在刷新...");
		
	}
	@Override
	public void startFooterRefresh() {
		
		footer_refresh_arrow_icon.setVisibility(View.GONE);
		footer_refresh_arrow_progress.setVisibility(View.VISIBLE);
		footer_refresh_text.setText("正在刷新...");
		
	}
	@SuppressLint("NewApi")
	@Override
	public void moveHeader(float values) {
		if (v_header.getVisibility()!=View.VISIBLE) {
			v_header.setVisibility(View.VISIBLE);
		}
		if (values>getHeaderCritical()) {
			
			header_refresh_text.setText("松开刷新...");
			header_refresh_arrow_icon.setRotation(180);
			//ObjectAnimator.ofFloat(header_refresh_arrow_icon, "rotation", 180).setDuration(50).start();
			
		}
		else
		{
			header_refresh_text.setText("下拉刷新...");
			header_refresh_arrow_icon.setRotation(0);
			//ObjectAnimator.ofFloat(header_refresh_arrow_icon, "rotation", 0).setDuration(50).start();
			
		}
		
	}
	@SuppressLint("NewApi")
	@Override
	public void moveFooter(float values) {
		// TODO Auto-generated method stub
		if (v_footer.getVisibility()!=View.VISIBLE) {
			v_footer.setVisibility(View.VISIBLE);
		}
		if (values>getFooterCritical()) {
			footer_refresh_text.setText("松开刷新...");
			footer_refresh_arrow_icon.setRotation(180);
			//ObjectAnimator.ofFloat(footer_refresh_arrow_icon, "rotation", 180).setDuration(50).start();
			
		}
		else
		{
			footer_refresh_text.setText("上拉刷新...");
			footer_refresh_arrow_icon.setRotation(0);
			//ObjectAnimator.ofFloat(footer_refresh_arrow_icon, "rotation", 0).setDuration(50).start();
			
		}
	}
	@Override
	public View getHeaderView() {
		header_refresh_arrow_icon.setVisibility(View.VISIBLE);
		header_refresh_arrow_progress.setVisibility(View.INVISIBLE);
		header_refresh_arrow_icon.setImageResource(R.drawable.refresh_arrow_up_icon);
		header_refresh_text.setText("下拉刷新...");
		return v_header;
	}
	@Override
	public View getFooterView() {
		footer_refresh_arrow_icon.setVisibility(View.VISIBLE);
		footer_refresh_arrow_progress.setVisibility(View.INVISIBLE);
		footer_refresh_text.setText("上拉刷新...");
		footer_refresh_arrow_icon.setImageResource(R.drawable.refresh_arrow_down_icon);
		
		return v_footer;
	}
	@Override
	public int getHeaderCritical() {
		
		return Utils.dp2px(context, 50);
	}
	@Override
	public int getFooterCritical() {
		// TODO Auto-generated method stub
		return Utils.dp2px(context, 50);
	}

}
