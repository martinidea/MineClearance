package com.code.martin.mineclearance.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.code.martin.mineclearance.R;

/**
 * Created by Martin on 2016/7/22.
 */
public class LED extends LinearLayout {
    private ImageView LED1, LED2, LED3;
    private int num;
    private int width,height;
    private LayoutParams params;

    public LED(Context context) {
        this(context,null);
    }

    public LED(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LED(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        TypedArray ta =context.obtainStyledAttributes(attrs,R.styleable.LED);
        num = ta.getInt(R.styleable.LED_num,0);
        width = (int)ta.getDimension(R.styleable.LED_LEDwidth,0);
        height = (int) ta.getDimension(R.styleable.LED_LEDheight,0);
        ta.recycle();
        init();
    }

    private void init() {
        LED1 = new ImageView(getContext());
        LED2 = new ImageView(getContext());
        LED3 = new ImageView(getContext());
        setNum(num);
        params = new LayoutParams(width,height);
        addView(LED1,params);
        addView(LED2,params);
        addView(LED3,params);
    }

    public void setNum(int num) {
        this.num = num;
        if (num > 999) {
            setLED1(9);
            setLED2(9);
            setLED3(9);
            return;
        }
        if (num < 0) {
            num = Math.abs(num);
            setLED1(10);
            if (num > 99) {
                setLED2(9);
                setLED3(9);
            } else {
                setLED3(num % 10);
                num /= 10;
                setLED2(num);
            }
            return;
        }
        setLED3(num % 10);
        num /= 10;
        setLED2(num % 10);
        num /= 10;
        setLED1(num);
    }

    private void setLED1(int num) {
        switch (num) {
            case 10:
                LED1.setImageResource(R.drawable.d10);
                break;
            case 0:
                LED1.setImageResource(R.drawable.d0);
                break;
            case 1:
                LED1.setImageResource(R.drawable.d1);
                break;
            case 2:
                LED1.setImageResource(R.drawable.d2);
                break;
            case 3:
                LED1.setImageResource(R.drawable.d3);
                break;
            case 4:
                LED1.setImageResource(R.drawable.d4);
                break;
            case 5:
                LED1.setImageResource(R.drawable.d5);
                break;
            case 6:
                LED1.setImageResource(R.drawable.d6);
                break;
            case 7:
                LED1.setImageResource(R.drawable.d7);
                break;
            case 8:
                LED1.setImageResource(R.drawable.d8);
                break;
            case 9:
                LED1.setImageResource(R.drawable.d9);
                break;
        }
    }

    private void setLED2(int num) {
        switch (num) {
            case 0:
                LED2.setImageResource(R.drawable.d0);
                break;
            case 1:
                LED2.setImageResource(R.drawable.d1);
                break;
            case 2:
                LED2.setImageResource(R.drawable.d2);
                break;
            case 3:
                LED2.setImageResource(R.drawable.d3);
                break;
            case 4:
                LED2.setImageResource(R.drawable.d4);
                break;
            case 5:
                LED2.setImageResource(R.drawable.d5);
                break;
            case 6:
                LED2.setImageResource(R.drawable.d6);
                break;
            case 7:
                LED2.setImageResource(R.drawable.d7);
                break;
            case 8:
                LED2.setImageResource(R.drawable.d8);
                break;
            case 9:
                LED2.setImageResource(R.drawable.d9);
                break;
        }
    }

    private void setLED3(int num) {
        switch (num) {
            case 0:
                LED3.setImageResource(R.drawable.d0);
                break;
            case 1:
                LED3.setImageResource(R.drawable.d1);
                break;
            case 2:
                LED3.setImageResource(R.drawable.d2);
                break;
            case 3:
                LED3.setImageResource(R.drawable.d3);
                break;
            case 4:
                LED3.setImageResource(R.drawable.d4);
                break;
            case 5:
                LED3.setImageResource(R.drawable.d5);
                break;
            case 6:
                LED3.setImageResource(R.drawable.d6);
                break;
            case 7:
                LED3.setImageResource(R.drawable.d7);
                break;
            case 8:
                LED3.setImageResource(R.drawable.d8);
                break;
            case 9:
                LED3.setImageResource(R.drawable.d9);
                break;
        }
    }
}
