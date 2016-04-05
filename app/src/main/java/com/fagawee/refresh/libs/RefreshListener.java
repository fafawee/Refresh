package com.fagawee.refresh.libs;

/*监听刷新事件*/
/*design by tianjiangwei 2106-03-13*/
public interface RefreshListener {

	/*上拉刷新回调*/
	void onHeaderRefresh();
	/*下拉刷新回调*/
	void onFooterRefresh();
	
}
