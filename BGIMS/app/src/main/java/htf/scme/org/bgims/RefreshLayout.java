package htf.scme.org.bgims;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by Administrator on 2015/12/7.
 */
public class RefreshLayout extends SwipeRefreshLayout implements AbsListView.OnScrollListener {
    public static int ListViewCurPosion;
    /**
     * 滑动到最下面时的上拉操作
     */
    private int mTouchSlop;
    /**
     * listview实例
     */
    private ListView mListView;
    /**
     * 上拉监听器, 到了最底部的上拉加载操作
     */
    private OnLoadListener mOnLoadListener;
    /**
     * ListView的加载中footer
     */
    private View mListViewFooter;
    /**
     * 按下时的y坐标
     */
    private int mYDown;
    /**
     * 抬起时的y坐标, 与mYDown一起用于滑动到底部时判断是上拉还是下拉
     */
    private int mLastY;
    /**
     * 是否在加载中 ( 上拉加载更多 )
     */
    private boolean isLoading = false;
    /**
     * @param context
     */
    public RefreshLayout(Context context) {
        this(context, null);
    }
    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mListViewFooter = LayoutInflater.from(context).inflate(R.layout.listview_footer, null,false);
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 初始化ListView对象
        if (mListView == null) {
            getListView();
        }
    }
    /**
     * 获取ListView对象
     */
    private void getListView() {
        int childs = getChildCount();
        if (childs > 0) {
            for ( int i=0; i< childs;i++) {
                View childView = getChildAt(i);
                if (childView instanceof ListView) {
                    mListView = (ListView) childView;
                    // 设置滚动监听器给ListView, 使得滚动的情况下也可以自动加载
                    mListView.setOnScrollListener(this);
                    Log.d("在当前页面找到了LISTview", "### 找到listview");
                    return;
                }
            }
        }
    }
    /*
     * (non-Javadoc)
     * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 按下
                mYDown = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 移动
                mLastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                // 抬起
                if (canLoad()) {
                    loadData();
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }
    /**
     * 是否可以加载更多, 条件是到了最底部, listview不在加载中, 且为上拉操作.
     * @return
     */
    private boolean canLoad() {
        /*boolean isb = isBottom();
        if (isb)
            Log.d("是否最底部", "是");
        else
            Log.d("是否最底部", "不是");
        if (isLoading)
            Log.d("是否调用", "是");
        else
            Log.d("是否调用", "是");
        boolean isp = isPullUp();
        if (isp)
            Log.d("是否加载", "是");
        else
            Log.d("是否加载", "是");
        boolean rb = isb && !isLoading && isp;
        return rb;*/
        return isBottom() && !isLoading && isPullUp();
    }
    /**
     * 判断是否到了最底部
     */
    private boolean isBottom() {
        if (mListView != null && mListView.getAdapter() != null) {
            int p = mListView.getLastVisiblePosition();
            int c =mListView.getAdapter().getCount() - 1;
            if (p==c) {
                Log.d("到最底部了", "很好");
                return true;
            } else {
                Log.d("没有到最底部", "不很好");
                return false;
            }
        }
        return false;
    }

    /**
     * 是否是上拉操作
     * @return
     */
    private boolean isPullUp() {
        return (mYDown - mLastY) >= mTouchSlop;
    }
    /**
     * 如果到了最底部,而且是上拉操作.那么执行onLoad方法
     */
    private void loadData() {
        if (mOnLoadListener != null) {
            // 设置状态
            setLoading(true);
            //
            mOnLoadListener.onLoad();
        }
    }
    /**
     * @param loading
     */
    public void setLoading(boolean loading) {
        isLoading = loading;

        if (isLoading) {
            mListView.addFooterView(mListViewFooter);
        } else {
            mListView.removeFooterView(mListViewFooter);
            mYDown = 0;
            mLastY = 0;
        }
    }
    /**
     * @param loadListener
     */
    public void setOnLoadListener(OnLoadListener loadListener) {
        mOnLoadListener = loadListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
       if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
           int curP = mListView.getFirstVisiblePosition();
           ListViewCurPosion = curP;
           Log.d("滚动到了positions", curP+"");
        }
    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        // 滚动时到了最底部也可以加载更多
        if (canLoad()) {
            loadData();
        }

    }
    /**
     * 加载更多的监听器
     * @author mrsimple
     */
    public static interface OnLoadListener {
        public void onLoad();
    }
}
