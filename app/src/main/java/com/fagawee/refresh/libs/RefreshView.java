package com.fagawee.refresh.libs;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

/*刷新视图 
 * design by tianjiangwei 2106-03-13*/
public class RefreshView extends FrameLayout implements Refresh{

	/*屏幕宽度*/
	public int screenWidth;
	/*屏幕高度*/
	public  int screenHeight;
	/*默认的动画时间*/
	public static final long ANIMATIONTIME=800;
	/*是否可用*/
	public static final int FLAG_ENABLE=0x000000001;
	/*上拉刷新是否可用*/
	public static final int FLAG_HEADER_ENABLE=0x000000002;
	/*下拉刷新是否可用*/
	public static final int FLAG_FOOTER_ENABLE=0x000000004;
	/*FLAG mode*/
	public int flag_mask=(FLAG_ENABLE|FLAG_HEADER_ENABLE|FLAG_FOOTER_ENABLE);
	/*视图回到初始位置需要的时间*/
	private long duration=ANIMATIONTIME;
	/*刷新结果的监听*/
	private RefreshListener listener;
	/*刷新状态的监听*/
	private RefreshStatusChangeListener statusListener;
	/*当前状态*/
	private Status status=Status.INITIAL;
	/*默认下拉刷新*/
	private Direction direction=Direction.NULL;
	private PointF start = new PointF();
	/*我的content view*/
	private View mChildView;
	//private View childHeaderView;
	//private View childFooterView;
	private BaseAnimationAdapter adapter;
	private Distance distance;
	/*移动动画，平滑过渡*/
	private ValueAnimator animator;
	/*是否在运动*/
	private boolean isRuning=false;
	/*是否按下*/
	private boolean isPress=false;
	/*顶部刷新视图的移动速度比例，如果大于1的话就会移动的快些，这个还要自己看了效果才知道*/
	private float headerSpeedRatio=1;
	/*底部刷新视图的移动速度比例，如果大于1的话就会移动的快些，这个还要自己看了效果才知道*/
	private float footerSpeedRatio=1;
	
	
	
	

	public RefreshView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}
	public RefreshView(Context context) {
		super(context);
		init();
		// TODO Auto-generated constructor stub
		
		
	}
	
	
	/*初始化*/
	private void init()
	{
		distance=new Distance(); 

		
		post(new Runnable() {
			
			@Override
			public void run() {
				screenWidth=getWidth();
				screenHeight=getHeight();
				if (getChildCount()>1) {
					return ;
				}
				if (getChildCount()==1) {
					mChildView=getChildAt(0);
					distance.child_top=0;
				}
				
				
				
			}
		});
	}
	
	public void setStatusListener(RefreshStatusChangeListener statusListener) {
		this.statusListener = statusListener;
	}
	public void setRefreshListener(RefreshListener listener) {
		this.listener = listener;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public void setAdapter(BaseAnimationAdapter adapter) {
		this.adapter = adapter;
		if (adapter!=null) {
			if (getChildCount()==3) {
				removeViewAt(getChildCount()-1);
				removeViewAt(getChildCount()-1);
			}
			addHeaderView();
			addFooterView();
		}
		
	}
	

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {				
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			start.y=ev.getY();
			start.x=ev.getX();
			//如果在contentView内才能触摸暂停
			if ((ev.getY()<=distance.canTouchMaxY&&ev.getY()>=distance.canTouchMinY)) {
				if (isRuning) {
					pause();
				}
			}
			
			break;
		case MotionEvent.ACTION_MOVE:
			float dy=start.y-ev.getY();
			float dx=start.x-ev.getX();
			start.y=ev.getY();
			start.x=ev.getX();
			if (dy>0) {
				if (!canChildPullUp()) {
					return true;
				}
			}
			else if(dy<0)
			{
				if (!canChildPullDown()) {
					return true;
				}
			}
			
			
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			
			break;

		default:
			break;
		}
    return super.onInterceptTouchEvent(ev);
		
		
	}
	

	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction()==MotionEvent.ACTION_UP) {
			
			if (status==Status.HEATER_CRITICAL_FRONT) {
				
				moveToInitPositionSmooth();
			}
			else if(status==Status.HEATER_CRITICAL_BEHIND)
			{
				
				moveToHeaderCriticalSmooth();
			}
			else if(status==Status.FOOTER_CRITICAL_FRONT)
			{
				
				moveToInitPositionSmooth();
			}
			else if(status==Status.FOOTER_CRITICAL_BEHIND)
			{
				
				moveToFooterCriticalSmooth();
			}
			isPress=false;
		}
		else if(event.getAction()==MotionEvent.ACTION_DOWN)
		{
	
			
			//若果在刷新状态的话 就不可以滑动了
			if (status==Status.REFRESH) {
				return false;
			}
			//如果在contentView外的话就不可以滑动了
			if (!(event.getY()<=distance.canTouchMaxY&&event.getY()>=distance.canTouchMinY)) {
				return false;
			}
			if (isHaveFlag(FLAG_ENABLE)&&(isHaveFlag(FLAG_FOOTER_ENABLE)||isHaveFlag(FLAG_HEADER_ENABLE))) {
				return true;				
			}
			
			
		}
		else if(event.getAction()==MotionEvent.ACTION_MOVE)
		{
			float dy=start.y-event.getY();
			float dx=start.x-event.getX();
			start.y=event.getY();
			start.x=event.getX();
			moveGustures(dy, dx, event);
		}
		
		return super.onTouchEvent(event);
		
	}
	
	
	
	@Override
	public void pullToHeaderRefresh() {
		if (isHaveFlag(FLAG_ENABLE)&&isHaveFlag(FLAG_HEADER_ENABLE)) {
			moveToSmooth(distance.header_critical,150);
		}
		
		
	}

	@Override
	public void pullToFooterRefresh() {
		if (isHaveFlag(FLAG_ENABLE)&&isHaveFlag(FLAG_FOOTER_ENABLE)) {
			moveToSmooth(distance.footer_critical,150);
		}
		
		
	}

	@Override
	public void completeRefresh() {
		
		if (status==Status.REFRESH) {
			
			moveToInitPositionSmooth();
		}
		if (statusListener!=null) {
			statusListener.onComplete();
		}
	}

	@Override
	public void setHeaderRefreshEnable(boolean able) {
		if (able) {
			addFlag(FLAG_HEADER_ENABLE);
		}
		else
		{
			removeFlag(FLAG_HEADER_ENABLE);
			
		}
		
	}

	@Override
	public void setFooterRefreshEnable(boolean able) {
		if (able) {
			addFlag(FLAG_FOOTER_ENABLE);
		}
		else
		{
			removeFlag(FLAG_FOOTER_ENABLE);
			
		}
		
		
	}

	@Override
	public void setEnable(boolean able) {
		if (able) {
			addFlag(FLAG_ENABLE);
		}
		else
		{
			removeFlag(FLAG_ENABLE);
		}	
	}

	@Override
	public Status getStatus() {
		// TODO Auto-generated method stub
		return status;
	}
	@Override
	public Direction getDirection() {
		// TODO Auto-generated method stub
		return direction;
	}
	
	
	/*判断是否含有某个flag*/
	public boolean isHaveFlag(int flag)
	{
		return (flag_mask&flag)!=0;
	}
	/*添加某个flag*/
	public void addFlag(int flag)
	{
		flag_mask|=flag;
	}
	/*移除某个flag*/
	public void removeFlag(int flag)
	{
		flag_mask&=~flag;
	}
	/*判断一个触摸事件是否是垂直滑动*/
	public boolean isVerticalDrag(MotionEvent event)
	{
		
		

		if (event.getAction()==MotionEvent.ACTION_DOWN) {
			start.x=event.getX();
			start.y=event.getY();
			
		}
		else if(event.getAction()==MotionEvent.ACTION_MOVE)
		{
			float dx=Math.abs(event.getX()-start.x);
			float dy=Math.abs(event.getY()-start.y);
			
			if (dy>dx) {
				return true;
			}
		}
		return false;
	}
	
	/*移动视图的操作*/
	public void moveGustures(float dy,float dx,MotionEvent event)
	{
		 
		if (mChildView==null&&getChildCount()>0) {
			mChildView=getChildAt(0);
			distance.child_top=0;
		}
		
			
				if (dy>0&&isHaveFlag(FLAG_ENABLE)&&isHaveFlag(FLAG_HEADER_ENABLE)) {
					direction=Direction.DOWN;
					if (statusListener!=null&&!isPress) {
						statusListener.onHeaderStart();
						isPress=true;
					}
					if (statusListener!=null) {
						statusListener.onHeaderMoving();
					}
					processHeaderMotionEvent(dy,event);
				}
				else if(isHaveFlag(FLAG_ENABLE)&&isHaveFlag(FLAG_FOOTER_ENABLE))
				{
					processHeaderMotionEvent(dy,event);
					direction=Direction.UP;
					if (statusListener!=null&&!isPress) {
						statusListener.onFooterStart();
						isPress=true;
					}
					if (statusListener!=null) {
						statusListener.onFooteMoving();
					}
					
				}
			
			/*只能识别垂直滑动*/
			if (Math.abs(dy)>Math.abs(dx)) {
				processMotionEvent(event,dy);
			}
	}
	

	
	/*处理状态*/
	private void doStatus()
	{
		/*向下滑动*/
		if (isHaveFlag(FLAG_ENABLE)&&isHaveFlag(FLAG_HEADER_ENABLE)&&distance.child_top>0) {
			direction=Direction.DOWN;
			if (statusListener!=null&&!isPress) {
				
				statusListener.onHeaderStart();
				isPress=true;
			}
			if (statusListener!=null) {
				statusListener.onHeaderMoving();
			}
			
		}
		/*向上滑动*/
		else if(isHaveFlag(FLAG_ENABLE)&&isHaveFlag(FLAG_FOOTER_ENABLE)&&distance.child_top<0)
		{
			direction=Direction.UP;
			if (statusListener!=null&&!isPress) {
				statusListener.onFooterStart();
				isPress=true;
			}
			if (statusListener!=null) {
				statusListener.onFooteMoving();
			}
			
		}	
	}
	
	
	private void processMotionEvent(MotionEvent event,float dy)
	{
		if (mChildView!=null) {
			if (isHaveFlag(FLAG_ENABLE)&&isHaveFlag(FLAG_HEADER_ENABLE)&&dy<0) {
				moveBy(dy);
			}
			else if(isHaveFlag(FLAG_ENABLE)&&isHaveFlag(FLAG_FOOTER_ENABLE)&&dy>0)
			{
				moveBy(dy);
			}
			
			
		}
	}
	/*处理下拉事件*/
	private void processHeaderMotionEvent(float dy,MotionEvent event)
	{
		
		if (mChildView!=null) {
			
			
		}
	}
	/*处理上拉事件*/
	private void processFooterMotionEvent(float dy,MotionEvent event)
	{
		if (mChildView!=null) {
			
			
		}
	}
	
	
	
	
	/*刷新过程中的状态*/
	public enum Status
	{
		/*初始状态*/
		INITIAL
		,/*开始刷新的临界值前状态*/
		HEATER_CRITICAL_FRONT
		/*开始刷新的临界值后状态*/
		,HEATER_CRITICAL_BEHIND
		/*松手的刷新状态*/
		,REFRESH
		/*刷新完成的临界值前状态*/
		,FOOTER_CRITICAL_FRONT
		/*刷新完成的临界值后状态*/
		,FOOTER_CRITICAL_BEHIND
		
	}
	/*刷新方向*/
	public enum Direction
	{
		/*上拉、下拉两种方向*/
		UP,DOWN,NULL
	}
	
	/*添加顶部刷新视图*/
	private void addHeaderView()
	{
				
		if (adapter!=null) {
			
			if (adapter.getHeaderView()==null) {
				 throw new IllegalArgumentException("headerView is can't null");
			}
			addView(adapter.getHeaderView());
			headerSpeedRatio=adapter.getHeaderSpeedRatio();
			adapter.getHeaderView().post(new Runnable() {
				
				@Override
				public void run() {
					if (adapter.getHeaderCritical()<=0) {
						distance.header_critical=adapter.getHeaderView().getHeight();
					}
					else
					{
						distance.header_critical=adapter.getHeaderCritical();
					}
					
					
					distance.header_top=-adapter.getHeaderView().getHeight();					
					ViewHelper.setY(adapter.getHeaderView(), distance.header_top);
				}
			});
			
			
		}
		else 
		{
			throw new IllegalArgumentException("adapter can't null");
		}
		
	}
	/*添加底部刷新视图*/
	private void addFooterView()
	{
		if (adapter!=null) {
			
			if (adapter.getFooterView()==null) {
				 throw new IllegalArgumentException("footerView is can't null");
			}
			addView(adapter.getFooterView());
			footerSpeedRatio=adapter.getFooterSpeedRatio();
			adapter.getFooterView().post(new Runnable() {
				
				@Override
				public void run() {
					if (adapter.getFooterCritical()<=0) {
						distance.footer_critical=adapter.getFooterView().getHeight();
					}
					else
					{
						distance.footer_critical=-adapter.getFooterCritical();
					}
					distance.footer_top=screenHeight;
					distance.canTouchMaxY=screenHeight;
					distance.canTouchMinY=0;
					ViewHelper.setY(adapter.getFooterView(), distance.footer_top);
				}
			});			
			
		}
		else 
		{
			throw new IllegalArgumentException("adapter can't null");
		}
	}
	
	/*全局的距离类*/
	protected class Distance
	{
		
		/*headerview 的移动与屏幕上端的距离*/
		public float header_top;
		/*footerview 的移动与屏幕上端的距离*/
		public float footer_top;
		/*mChildView 的移动与屏幕上端的距离*/
		public float child_top;
		/*headerview移动超过这个临界值后 松开手手就可以刷新，*/
		public float header_critical;
		/*footerview移动超过这个临界值后 松开手手就可以刷新，*/
		public float footer_critical;
		/*可以滑动的最大的Y值*/
		public float canTouchMaxY;
		/*可以滑动的最小的Y值*/
		public float canTouchMinY;
		
		
	}
	/*获取状态栏高度*/
	private int getStatusBarHeight() {
		  int result = 0;
		  int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		  if (resourceId > 0) {
		      result = getResources().getDimensionPixelSize(resourceId);
		  }
		  return result;
		}

	/*回到初始位置*/
	private void moveToInitPosition()
	{
		moveTo(0);
		
	}
	/*平滑的回到初始位置*/
	private void moveToInitPositionSmooth()
	{
		moveToSmooth(0,computeDuration());
		
	}
	/*平滑的回到下拉临界位置*/
	private void moveToHeaderCriticalSmooth()
	{
		moveToSmooth(distance.header_critical,computeDuration());
		
	}
	/*平滑的回到上拉临界位置*/
	private void moveToFooterCriticalSmooth()
	{
		moveToSmooth(distance.footer_critical,computeDuration());
		
	}
	/*整体移动相对的单位距离*/
	private void moveBy(float dy)
	{
		if (adapter==null)
		{
			throw new IllegalArgumentException("adapter can't null");
		}
		if (mChildView==null&&getChildCount()>0) {
			mChildView=getChildAt(0);
			distance.child_top=0;
		}
		
		ViewHelper.setY(mChildView, distance.child_top-=dy);
		
		if (adapter.getHeaderView()!=null) {
			ViewHelper.setY(adapter.getHeaderView(), distance.header_top-=dy*headerSpeedRatio);

		}
		if (adapter.getFooterView()!=null) {
			ViewHelper.setY(adapter.getFooterView(), distance.footer_top-=dy*footerSpeedRatio);
		}
		distance.canTouchMaxY-=dy;
		distance.canTouchMinY-=dy;
		doStatus();
		if (distance.child_top==0) {
			status=Status.INITIAL;
		}
		else
		{
			if (direction==Direction.DOWN) {
				if (adapter!=null) {
					adapter.moveHeader(Math.abs(distance.child_top));
				}
				if (distance.child_top<distance.header_critical&&distance.child_top>0) {
					status=Status.HEATER_CRITICAL_FRONT;
					
				}
				
				else if(distance.child_top>distance.header_critical)
				{
					status=Status.HEATER_CRITICAL_BEHIND;
				}
			}
			else if(direction==Direction.UP)
			{
				if (adapter!=null) {
					adapter.moveFooter(Math.abs(distance.child_top));
				}
				if (distance.child_top>distance.footer_critical&&distance.child_top<0) {
					status=Status.FOOTER_CRITICAL_FRONT;
					
				}
				
				else if(distance.child_top<distance.footer_critical)
				{
					status=Status.FOOTER_CRITICAL_BEHIND;
				}
			}
		}

		isRuning=true;
	}
	private void moveToSmooth(final float position,long duration)
	{
		animator=ValueAnimator.ofFloat(distance.child_top,position);
		animator.setDuration(duration);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator values) {
				float value=(Float)values.getAnimatedValue();
				moveTo(value);
				
			}
		});
		animator.addListener(new Animator.AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator arg0) {
				isRuning=true;
				
			}
			
			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator arg0) {
				isRuning=false;
				
				if (position==distance.header_critical&&distance.child_top==distance.header_critical) {
					status=Status.REFRESH;
					if (listener!=null) {						
						listener.onHeaderRefresh();
					}
					if (statusListener!=null) {
						statusListener.onRefresh();
					}
					if (adapter!=null) {
						adapter.startHeaderRefresh();
					}
				}
				else if(position==distance.footer_critical&&distance.child_top==distance.footer_critical)
				{
					status=Status.REFRESH;
					if (listener!=null) {						
						listener.onFooterRefresh();
					}
					if (statusListener!=null) {
						statusListener.onRefresh();
					}
					if (adapter!=null) {
						adapter.startFooterRefresh();
					}
				}
			}
			
			@Override
			public void onAnimationCancel(Animator arg0) {
				isRuning=false;
				
			}
		});
		animator.start();
		
	}
	/*整体移动到绝对的位置*/
	private void moveTo(float y)
	{
		
		if (adapter==null)
		{
			throw new IllegalArgumentException("adapter can't null");
		}
		
		
		if (mChildView==null&&getChildCount()>0) {
			mChildView=getChildAt(0);
			distance.child_top=0;
		}
		
		ViewHelper.setY(mChildView, distance.child_top=y);
		
		if (adapter.getHeaderView()!=null) {
			ViewHelper.setY(adapter.getHeaderView(), distance.header_top=y*headerSpeedRatio-adapter.getFooterView().getHeight());

		}
		if (adapter.getFooterView()!=null) {
			ViewHelper.setY(adapter.getFooterView(), distance.footer_top=y*footerSpeedRatio+screenHeight);
		}
		distance.canTouchMaxY=distance.footer_top;
		distance.canTouchMinY=distance.child_top;
		
		doStatus();
		
		
		if (distance.child_top==0) {
			status=Status.INITIAL;
		}
		else
		{
			if (direction==Direction.DOWN) {
				if (adapter!=null) {
					adapter.moveHeader(Math.abs(distance.child_top));
				}
				if (distance.child_top<distance.header_critical&&distance.child_top>0) {
					status=Status.HEATER_CRITICAL_FRONT;
					
				}
				
				else if(distance.child_top>distance.header_critical)
				{
					status=Status.HEATER_CRITICAL_BEHIND;
				}
			}
			else if(direction==Direction.UP)
			{
				if (adapter!=null) {
					adapter.moveFooter(Math.abs(distance.child_top));
				}
				if (distance.child_top>distance.footer_critical&&distance.child_top<0) {
					status=Status.FOOTER_CRITICAL_FRONT;
					
				}
				
				else if(distance.child_top<distance.footer_critical)
				{
					status=Status.FOOTER_CRITICAL_BEHIND;
				}
			}
		}

		isRuning=true;
	}
	
	
	/*childview是否可以下滑动,支持ListView gridView （都是AbsListView子类）ScroolView，WebView 以及其他子视图*/
    private  boolean canChildPullDown() {
    	
    	if (mChildView==null&&getChildCount()>0) {
			mChildView=getChildAt(0);
			
		}
    	
        if (mChildView instanceof AbsListView) {
            final AbsListView absListView = (AbsListView) mChildView;
             return absListView.getChildCount() > 0&&(canScrollVertically(mChildView, -1)||absListView.getChildAt(0).getTop()<absListView.getPaddingTop()||absListView.getFirstVisiblePosition()>0);
        }       
        else if (mChildView instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) mChildView;
            View childView = scrollView.getChildAt(0);
            if (childView != null) {
                return canScrollVertically(mChildView, -1)
                        || (scrollView.getScrollY()<0&&childView.getHeight()>scrollView.getHeight());
            }
        }
        else if (mChildView instanceof WebView) {
            WebView webview = (WebView) mChildView;
            return canScrollVertically(mChildView, -1)
                    || (webview.getContentHeight() * webview.getScale()>webview
                            .getHeight() &&webview.getScrollY()<0);
        }
        else {
            return canScrollVertically(mChildView, -1) || mChildView.getScrollY() > 0;
        }
        return true;
    }
    /*childview是否可以上滑动,支持ListView gridView （都是AbsListView子类）ScroolView，WebView 以及其他子视图*/
    private boolean canChildPullUp() {
    	
    	if (mChildView==null&&getChildCount()>0) {
			mChildView=getChildAt(0);
		}	
    	
        if (mChildView instanceof AbsListView) {
            AbsListView absListView = (AbsListView) mChildView;
           
            return canScrollVertically(mChildView, 1)
                    || absListView.getLastVisiblePosition() != absListView.getCount() - 1;
        } else if (mChildView instanceof WebView) {
            WebView webview = (WebView) mChildView;
            return canScrollVertically(mChildView, 1)
                    || webview.getContentHeight() * webview.getScale() != webview
                            .getHeight() + webview.getScrollY();
        } else if (mChildView instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) mChildView;
            View childView = scrollView.getChildAt(0);
            if (childView != null) {
                return canScrollVertically(mChildView, 1)
                        || scrollView.getScrollY() != childView.getHeight()
                                - scrollView.getHeight();
            }
        }else{
            return canScrollVertically(mChildView, 1);
        }
        return true;
    }
 
    /**
     * 用来判断view在竖直方向上能不能向上或者向下滑动
     * @param view v
     * @param direction 方向    负数代表向上滑动 ，正数则反之
     * @return
     */
    private boolean canScrollVertically(View view, int direction) {
        return ViewCompat.canScrollVertically(view, direction);
    }
	/*不通位置回到初始位置的时间不同*/
	private long computeDuration()
	{
		return (long)((Math.abs(distance.child_top)/screenHeight)*duration);
	}
	/*手指触摸屏幕可以停止移动动画*/
	private void pause()
	{
		if (animator!=null&&animator.isRunning()) {
			animator.cancel();
			isRuning=false;
		}
	}

	
	
}
