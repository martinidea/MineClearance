package com.code.martin.mineclearance.impl;

/**
 * Created by Martin on 2016/7/30.
 */
public interface CallBack {
    void firstClick(int x,int y);
    void DoubleClickNum(int x, int y);
    void openZero(int x,int y);
    boolean isOk();
    void openSuccess();
    void clickMine();
    void flagChange(boolean make);
}
