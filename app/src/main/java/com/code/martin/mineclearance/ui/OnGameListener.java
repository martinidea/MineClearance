package com.code.martin.mineclearance.ui;

/**
 * Created by Martin on 2016/7/29.
 */
public interface OnGameListener {
    void gameStart();
    void gameFailure();
    void gameSuccess();
    void gameFlagChange(int num);
}
