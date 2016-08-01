package com.code.martin.mineclearance.ui;

/**
 * Created by Martin on 2016/7/28.
 */
public class ClickEvent {
    private int x;
    private int y;
    private ClickType clickType;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ClickType getClickType() {
        return clickType;
    }

    public void setClickType(ClickType clickType) {
        this.clickType = clickType;
    }

    public enum ClickType {
        SingleClick,
        DoubleClick,
        FirstClick
    }
}
