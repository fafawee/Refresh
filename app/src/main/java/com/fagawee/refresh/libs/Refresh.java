package com.fagawee.refresh.libs;

import com.fagawee.refresh.libs.RefreshView.Direction;
import com.fagawee.refresh.libs.RefreshView.Status;

/*刷新视图的必要的实现接口*/
/*design by tianjiangwei 2106-03-13*/
public interface Refresh {

	/*上拉刷新*/
	void pullToHeaderRefresh();
	/*下拉刷新*/
	void pullToFooterRefresh();
	/*刷新完成*/
	void completeRefresh();
	/*设置是否禁用上拉刷新*/
	void setHeaderRefreshEnable(boolean able);
	/*设置是否禁用下拉刷新*/
	void setFooterRefreshEnable(boolean able);
	/*设置是否禁用上拉、下拉刷新*/
	void setEnable(boolean able);
	/*获得当前状态*/
	Status getStatus();
	/*获得当前方向*/
	Direction getDirection();
	
	
}
