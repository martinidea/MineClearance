package com.code.martin.mineclearance.impl;

/**
 * Created by Martin on 2016/7/29.
 */
public interface OnGameListener {
    void gameStart();
    void gameFailure();
    void gameSuccess();
    void gameFlagChange(int num);
}
