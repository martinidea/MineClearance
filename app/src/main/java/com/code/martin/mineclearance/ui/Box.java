package com.code.martin.mineclearance.ui;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.code.martin.mineclearance.R;

/**
 * Created by Martin on 2016/7/21.
 */
public class Box extends View implements GestureDetector.OnGestureListener {
    private int x, y;
    private GestureDetector gestureDetector;
    private ClickEvent event = new ClickEvent();

    private OnClickEventListener onClickEventListener;

    public void setOnClickEventListener(OnClickEventListener onClickEventListener) {
        this.onClickEventListener = onClickEventListener;
    }

    public Box(Context context) {
        super(context);
        setBackgroundResource(R.drawable.blank);
        gestureDetector = new GestureDetector(context, this);
        gestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            public boolean onDoubleTap(MotionEvent e) {
                event.setX(x);
                event.setY(y);
                //双击时产生一次
                if (!BoxTable.isOK()) {
                    event.setClickType(ClickEvent.ClickType.FirstClick);
                } else {
                    event.setClickType(ClickEvent.ClickType.DoubleClick);
                }
                onClickEventListener.postEvent(event);
                return false;
            }

            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }

            public boolean onSingleTapConfirmed(MotionEvent e) {
                event.setX(x);
                event.setY(y);
                if (!BoxTable.isOK()) {
                    event.setClickType(ClickEvent.ClickType.FirstClick);
                } else {
                    event.setClickType(ClickEvent.ClickType.SingleClick);
                }
                onClickEventListener.postEvent(event);
                return false;
            }
        });
    }



    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }
}
