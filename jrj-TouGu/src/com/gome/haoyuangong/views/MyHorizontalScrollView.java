package com.gome.haoyuangong.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * 自定义水平移动view，用于资讯栏目标题头滑动更新标签
 * **/
public class MyHorizontalScrollView extends HorizontalScrollView {
  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt)
  {
    super.onScrollChanged(l, t, oldl, oldt);
    if(onScrollListener!=null)
    {
      onScrollListener.onAutoScroll(l, t, oldl, oldt);
    }
  }

  private Handler handler;
  private View view;

  public MyHorizontalScrollView(Context context) {
    super(context);

  }

  public MyHorizontalScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);

  }

  public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

  }

  // 这个获得总的高度
  public int computeVerticalScrollRange()
  {
    return super.computeHorizontalScrollRange();
  }

  public int computeVerticalScrollOffset()
  {
    return super.computeVerticalScrollOffset();
  }
  public boolean isScrollable(){
    if (view.getMeasuredWidth() <= getScrollX() + getWidth())
    {
      return false;
    }else{
      return true;
    }
  }
  
  /**
   * 初始化当前View,设置触摸监听和滚动监听
   * **/
  private void init()
  {

    this.setOnTouchListener(onTouchListener);
    handler = new Handler() {
      @Override
      public void handleMessage(Message msg)
      {
        // process incoming messages here
        super.handleMessage(msg);
        switch (msg.what)
        {
        case 1:
          if (view.getMeasuredWidth() <= getScrollX() + getWidth())
          {
            if (onScrollListener != null)
            {
              onScrollListener.onBottom();
            }

          } else if (getScrollX() == 0)
          {
            if (onScrollListener != null)
            {
              onScrollListener.onTop();
            }
          } else
          {
            if (onScrollListener != null)
            {
              onScrollListener.onScroll();
            }
          }
          break;
        default:
          break;
        }
      }
    };

  }

  OnTouchListener onTouchListener = new OnTouchListener() {

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {

      switch (event.getAction())
      {
      case MotionEvent.ACTION_DOWN:
        break;
      case MotionEvent.ACTION_UP:
        if (view != null && onScrollListener != null)
        {
          handler.sendMessageDelayed(handler.obtainMessage(1), 200);
        }
        break;

      default:
        break;
      }
      return false;
    }

  };

  /**
   * 获得参考的View，主要是为了获得它的MeasuredHeight，然后和滚动条的ScrollY+getHeight作比较。
   */
  public void getView()
  {
    this.view = getChildAt(0);
    if (view != null)
    {
      init();
    }
  }

  /**
   * 定义接口
   * 
   * @author admin
   * 
   */
  public interface OnScrollListener {
    void onBottom();

    void onTop();

    void onScroll();

    void onAutoScroll(int l, int t, int oldl, int oldt);
  }

  private OnScrollListener onScrollListener;

  public void setOnScrollListener(OnScrollListener onScrollListener)
  {
    this.onScrollListener = onScrollListener;
  }
}

