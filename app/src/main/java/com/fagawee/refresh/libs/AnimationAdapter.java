package com.fagawee.refresh.libs;

import android.view.View;

/*动画适配器*/
/*design by tianjiangwei 2106-03-13*/
public interface AnimationAdapter {

	/*开始刷新顶部动画*/
	void startHeaderRefresh();
	/*开始刷新底部动画*/
	void startFooterRefresh();
	/*headerView移动动画的数据更新*/
	void moveHeader(float values);
	/*footerView移动动画的数据更新*/
	void moveFooter(float values);
	/*获得顶部视图*/
	View getHeaderView();
	/*获得底部视图*/
	View getFooterView();
	/*获得顶部滑动临界值*/
	int getHeaderCritical();
	/*获得顶部滑动临界值*/
	int getFooterCritical();
}
