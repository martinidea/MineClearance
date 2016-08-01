package com.code.martin.mineclearance.ui;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.code.martin.mineclearance.R;
import com.code.martin.mineclearance.impl.CallBack;

/**
 * Created by Martin on 2016/7/21.
 */
public class Box extends View implements GestureDetector.OnGestureListener {
    private int x, y, num;
    private GestureDetector gestureDetector;

    private final int[] RES_NUM = {
            R.drawable.box0, R.drawable.box1, R.drawable.box2,
            R.drawable.box3, R.drawable.box4, R.drawable.box5,
            R.drawable.box6, R.drawable.box7, R.drawable.box8
    };//存放资源图片ID

    private boolean open, flag, mine;
    private CallBack callBack;

    public boolean isOpen() {
        return open;
    }

    public void makeFlag() {
        if (!isOpen()) {
            if (isFlag()) {
                flag = false;
                setBackgroundResource(R.drawable.blank);
            } else {
                flag = true;
                setBackgroundResource(R.drawable.flag);
            }
            callBack.flagChange(flag);
        }
    }

    public boolean isFlag() {
        return flag;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    /**
     * @param doubleClick 是否是双击事件触发
     * @return 是否打开了雷
     */
    public void open(boolean doubleClick) {
        if (isOpen()) {
            if (doubleClick) {
                callBack.DoubleClickNum(x, y);
            }
        } else {
            open = true;
            if (isMine()) {
                callBack.clickMine();
                setBackgroundResource(R.drawable.mine2);
                return;
            }
            callBack.openSuccess();
            setBackgroundResource(RES_NUM[getNum()]);
            if (getNum() == 0) {
                callBack.openZero(x, y);
            }
        }

    }

    public Box(Context context, final int x, final int y, final CallBack callBack) {
        super(context);
        this.x = x;
        this.y = y;
        this.callBack = callBack;
        setBackgroundResource(R.drawable.blank);
        gestureDetector = new GestureDetector(context, this);
        gestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            public boolean onDoubleTap(MotionEvent e) {
                //双击时产生一次
                if (!callBack.isOk()) {
                    callBack.firstClick(x, y);
                }
                if (!isFlag())
                    open(true);
                return false;
            }

            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }

            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (!callBack.isOk()) {
                    callBack.firstClick(x, y);
                    open(false);
                } else {
                    makeFlag();
                }
                return false;
            }
        });
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
