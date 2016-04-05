package com.fagawee.refresh.libs;

/*监听刷新状态*/
/*design by tianjiangwei 2106-03-13*/
public interface RefreshStatusChangeListener {
	/*开始下拉...回调*/	
	void onHeaderStart();
	/*正在下拉...回调*/	
	void onHeaderMoving();
	/*正在上拉...回调*/
	void onFooteMoving();
	/*开始上拉...回调*/
	void onFooterStart();
	/*正在刷新...回调*/
	void onRefresh();
	/*刷新完成...回调*/
	void onComplete();
}
