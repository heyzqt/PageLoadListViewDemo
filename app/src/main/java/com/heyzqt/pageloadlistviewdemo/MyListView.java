package com.heyzqt.pageloadlistviewdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Created by heyzqt on 2018/5/14.
 */

public class MyListView extends ListView implements AbsListView.OnScrollListener {

	private View headerView;
	private ImageView refreshView;
	private int headHeight;
	private int downY;
	private int moveY;
	private int paddingTop;
	private int firstVisibleItem;

	private View footerView;
	private int visibleLastIndex;
	private boolean isEnd;

	private final int PULL_DOWN_REFRESH = 0;
	private final int REFRESHING = 1;
	private final int RELEASE_REFRESH = 2;
	private int currentState = PULL_DOWN_REFRESH;
	private LoadMoreStatus mLoadMoreStatus = LoadMoreStatus.CLICK_TO_LOAD;

	private RotateAnimation rotateAnimation;

	private OnRefreshListener mRefreshListener;

	private final int HEADER_VIEW_SCROLL = 80;

	private static final String TAG = "MyListView";

	public static enum LoadMoreStatus {
		/**
		 * Load more
		 */
		CLICK_TO_LOAD,
		/**
		 * Loading
		 */
		LOADING,
		/**
		 * NO more content need to load.
		 */
		LOADED_ALL
	}

	public MyListView(Context context) {
		this(context, null);
	}

	public MyListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initHeaderView(context);
		initFooterView(context);
		setOnScrollListener(this);
	}

	void initHeaderView(Context context) {
		headerView = LayoutInflater.from(context).inflate(R.layout.header, null, false);
		refreshView = headerView.findViewById(R.id.refresh_circle);
		headerView.measure(0, 0);
		headHeight = headerView.getMeasuredHeight();
		addHeaderView(headerView);
	}

	void initFooterView(Context context) {
		footerView = LayoutInflater.from(context).inflate(R.layout.footer, null, false);
		footerView.setVisibility(View.GONE);
		addFooterView(footerView);
	}

	public static void startRotateAnimation(View animateView) {
		RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		rotate.setDuration(1000);
		rotate.setRepeatCount(Animation.INFINITE);
		rotate.setInterpolator(new LinearInterpolator());
		animateView.startAnimation(rotate);
	}

	public static void stopRotateAnmiation(View animatedView) {
		if (animatedView.getAnimation() != null) {
			animatedView.getAnimation().cancel();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		System.out.println("scroll state = " + scrollState);
		if (scrollState == SCROLL_STATE_IDLE) {
			if (isEnd && mLoadMoreStatus == LoadMoreStatus.CLICK_TO_LOAD
					&& currentState != REFRESHING) {
				mLoadMoreStatus = LoadMoreStatus.LOADING;
				footerView.setVisibility(View.VISIBLE);
				mRefreshListener.pullUpLoad();
			}
		}
	}

	public void showFooterView(boolean isShow) {
		if (isShow) {
			footerView.setVisibility(View.VISIBLE);
		} else {
			footerView.setVisibility(View.GONE);
		}
	}

	public void finishLoad(boolean loadAll) {
		if (loadAll) {
			mLoadMoreStatus = LoadMoreStatus.LOADED_ALL;
		} else {
			mLoadMoreStatus = LoadMoreStatus.CLICK_TO_LOAD;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
			int totalItemCount) {
		this.firstVisibleItem = firstVisibleItem;
		this.visibleLastIndex = firstVisibleItem + visibleItemCount - 1;

		if ((firstVisibleItem + visibleItemCount) >= (totalItemCount - 1)) {
			isEnd = true;
		} else {
			isEnd = false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				Log.i(TAG, "onTouchEvent: ACTION_DOWN");
				downY = (int) ev.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				moveY = (int) ev.getY();
				paddingTop = (moveY - downY) - HEADER_VIEW_SCROLL;

				if (firstVisibleItem == 0 && paddingTop > -headHeight) {
					Log.i(TAG, "onTouchEvent: 11111111 state = " + currentState);
					if (paddingTop > 0 && currentState == PULL_DOWN_REFRESH) {
						currentState = RELEASE_REFRESH;
						changeHeaderViewState();

					} else if (paddingTop < 0 && currentState == RELEASE_REFRESH) {
						Log.i(TAG, "onTouchEvent: 22222");
						currentState = PULL_DOWN_REFRESH;
						changeHeaderViewState();
					}

					headerView.setPadding(0, paddingTop, 0, 0);
				}
				break;
			case MotionEvent.ACTION_UP:
				if (currentState == RELEASE_REFRESH) {
					Log.i(TAG, "onTouchEvent: 333333");
					headerView.setPadding(0, 0, 0, 0);
					currentState = REFRESHING;
					if (mRefreshListener != null) {
						mRefreshListener.pullDownRefresh();
					}
					changeHeaderViewState();
				} else if (currentState == PULL_DOWN_REFRESH) {
					Log.i(TAG, "onTouchEvent: 444444");
					headerView.setPadding(0, -HEADER_VIEW_SCROLL, 0, 0);
				}
				break;
		}
		return super.onTouchEvent(ev);
	}

	public interface OnRefreshListener {
		void pullDownRefresh();

		void pullUpLoad();
	}

	public void setOnRefreshListener(OnRefreshListener listener) {
		mRefreshListener = listener;
	}

	private void changeHeaderViewState() {
		switch (currentState) {
			case PULL_DOWN_REFRESH:
				startRotateAnimation(refreshView);
				break;
			case REFRESHING:
				startRotateAnimation(refreshView);
				break;
			case RELEASE_REFRESH:
				startRotateAnimation(refreshView);
				break;
		}
	}

	public void hideHeaderView() {
		headerView.setPadding(0, -HEADER_VIEW_SCROLL, 0, 0);
		currentState = PULL_DOWN_REFRESH;
	}
}
