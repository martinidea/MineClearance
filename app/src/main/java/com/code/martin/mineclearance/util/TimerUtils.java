package com.code.martin.mineclearance.util;

import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Martin on 2016/7/24.
 */
public class TimerUtils {
    private Timer timer;
    private TimerTask task;

    private static long sendTime;
    private static long pauseTime;
    private int time;

    private static boolean pauseState;

    private TimeChangeListener onTimeChangeListener;
    private Handler handler;

    private boolean isRunning;

    public TimerUtils() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1)
                    onTimeChangeListener.timeChange(++time);
                super.handleMessage(msg);
            }
        };
    }

    public void setOnTimeChangeListener(TimeChangeListener onTimeChangeListener) {
        this.onTimeChangeListener = onTimeChangeListener;
    }

    public void start() {
        time = 0;
        pauseState = false;
        if (timer == null) {
            timer = new Timer(true);
            initTimerTask();
            timer.schedule(task, 0, 1000);
            isRunning = true;
        }
        sendTime = System.currentTimeMillis();
    }

    private void initTimerTask() {
        task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };
    }

    public int getTime() {
        return time;
    }

    public void pause() {
        if (isRunning) {
            if (!pauseState) {
                timer.cancel();
                timer = null;
                pauseTime = System.currentTimeMillis();
                pauseState = true;
            } else {
                timer = new Timer(true);
                initTimerTask();
                timer.schedule(task, 1000 - (pauseTime - sendTime) % 1000, 1000);
                pauseState = false;
            }
        }
    }

    public void stop() {
        if (isRunning) {
            timer.cancel();
            timer = null;
            time = 0;
            task = null;
            isRunning = false;
        }
    }

}
