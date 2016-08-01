package com.code.martin.mineclearance.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.code.martin.mineclearance.ALG.MineALG;
import com.code.martin.mineclearance.ALG.Mode;
import com.code.martin.mineclearance.R;

import java.util.ArrayList;

/**
 * Created by Martin on 2016/7/21.
 */
public class BoxTable extends TableLayout implements CallBack {

    private static Box[][] boxArray;//Box二维数组
    private int boxTableMode;//BoxTable难度级别
    private int boxWidth, boxHeight;//Box视图的宽高
    private TableRow.LayoutParams params;//Box的LayoutParams

    private static boolean OK;//用于Box类调用，是否设置好雷

    private short maxX = 0, maxY = 0;//数组最大X下标。Y下标
    private int remainingFlag = 0;//剩余标记数
    private int remainingBox = 0;//剩余未开Box数

    private boolean[][] mineArray;//是否为雷二维数据
    private boolean[][] flagArray;//是否有标记二维数组
    private boolean[][] openTable;//是否已经打开二维数组
    private short[][] numArray;//数字二维数组，记录当前位置周围雷数
    private short[][] flagNumArray;//标记二维数组，每当标记或取消标记时，更改当前位置周围的值

    private Mode mode;//扫雷算法的构造方法参数

    private OnGameListener onGameListener;//游戏监听

    private boolean boxClickAble;//是否可点击

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (boxClickAble)
        return super.dispatchTouchEvent(ev);
        else  return false;
    }

    /**
     * 用于实现游戏周期
     *
     * @param onGameListener 设置游戏回调监听
     */
    public void setOnGameListener(OnGameListener onGameListener) {
        this.onGameListener = onGameListener;
    }

    public BoxTable(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BoxTable);
        boxTableMode = ta.getInteger(R.styleable.BoxTable_mode, 0);
        boxWidth = (int) ta.getDimension(R.styleable.BoxTable_boxwidth, 30);
        boxHeight = (int) ta.getDimension(R.styleable.BoxTable_boxheight, 30);
        ta.recycle();
        params = new TableRow.LayoutParams(boxWidth, boxHeight);
        init();
    }

    /**
     * 重新开始
     */
    public void restart() {
        this.removeAllViews();
        init();
        onGameListener.gameFlagChange(getRemainingFlag());
    }

    /**
     * 初始化
     */
    private void init() {
        OK = false;
        boxClickAble = true;
        switch (boxTableMode) {
            case 0:
                mode = Mode.Primary;
                maxX = 8;
                maxY = 8;
                remainingFlag = 10;
                break;
            case 1:
                mode = Mode.Middle;
                maxX = 15;
                maxY = 15;
                remainingFlag = 40;
                break;
            case 2:
                mode = Mode.Expert;
                maxX = 29;
                maxY = 15;
                remainingFlag = 100;
                break;
        }
        remainingBox = (maxX + 1) * (maxY + 1) - remainingFlag;
        boxArray = new Box[maxX + 1][maxY + 1];
        createdBox();
    }

    /**
     * 根据难度级别，创建Box
     */
    private void createdBox() {
        for (int i = 0; i < boxArray.length; i++) {
            TableRow tr = new TableRow(getContext());
            for (int j = 0; j < boxArray[0].length; j++) {
                boxArray[i][j] = new Box(getContext(), i, j, this);
                tr.addView(boxArray[i][j], params);
            }
            addView(tr);
        }
    }

    /**
     * @return 返回剩余可标记数
     */
    public int getRemainingFlag() {
        return remainingFlag;
    }


    /**
     * 用MineALG算法初始化是否为黑的二维数组
     *
     * @param x 点击的Box的X值
     * @param y 点击的Box的Y值
     */
    private void initMine(int x, int y) {
        MineALG mineALG = new MineALG(mode);
        mineALG.makeMine(x, y);
        mineArray = mineALG.getMineArray();
        numArray = mineALG.getNumArray();
        for (int i = 0; i < boxArray.length; i++) {
            for (int j = 0; j < boxArray[0].length; j++) {
                boxArray[i][j].setMine(mineArray[i][j]);
                boxArray[i][j].setNum(numArray[i][j]);
            }
        }
    }

    /**
     * @param mode 设置难度
     */
    public void setMode(int mode) {
        this.boxTableMode = mode;
    }

    /**
     * @return 返回游戏难度
     */
    public int getBoxTableMode() {
        return boxTableMode;
    }

    /**
     * 游戏胜利
     */
    private void success() {
        onGameListener.gameSuccess();
        boxClickAble = false;
    }

    /**
     * 游戏失败
     */
    private void failure() {
        onGameListener.gameFailure();
        boxClickAble = false;
        for (int i = 0; i < boxArray.length; i++) {
            for (int j = 0; j < boxArray[0].length; j++) {
                if (!boxArray[i][j].isOpen()) {
                    if (boxArray[i][j].isMine()) {
                        if (!boxArray[i][j].isFlag()) {
                            boxArray[i][j].setBackgroundResource(R.drawable.mine);
                        }
                    } else if (boxArray[i][j].isFlag())
                        boxArray[i][j].setBackgroundResource(R.drawable.mine1);
                }
            }
        }
    }

    /**
     * 计算点的周围
     *
     * @param x X坐标
     * @param y Y坐标
     * @return 周围的点
     */
    private ArrayList<Point> calculateAround(int x, int y) {
        ArrayList<Point> points = new ArrayList<>();
        if (x != 0) {
            points.add(new Point(x - 1, y));
            if (x != maxX) {
                points.add(new Point(x + 1, y));
                if (y != 0) {
                    points.add(new Point(x, y - 1));
                    points.add(new Point(x - 1, y - 1));
                    points.add(new Point(x + 1, y - 1));
                    if (y != maxY) {
                        points.add(new Point(x - 1, y + 1));
                        points.add(new Point(x, y + 1));
                        points.add(new Point(x + 1, y + 1));
                    }
                } else {
                    points.add(new Point(x - 1, 1));
                    points.add(new Point(x, 1));
                    points.add(new Point(x + 1, 1));
                }
            } else {
                if (y != 0) {
                    points.add(new Point(x, y - 1));
                    points.add(new Point(x - 1, y - 1));
                    if (y != maxY) {
                        points.add(new Point(x - 1, y + 1));
                        points.add(new Point(x, y + 1));
                    }
                } else {
                    points.add(new Point(x - 1, 1));
                    points.add(new Point(x, 1));
                }
            }
        } else {
            points.add(new Point(1, y));
            if (y != 0) {
                points.add(new Point(0, y - 1));
                points.add(new Point(1, y - 1));
                if (y != maxY) {
                    points.add(new Point(0, y + 1));
                    points.add(new Point(1, y + 1));
                }
            } else {
                points.add(new Point(0, 1));
                points.add(new Point(1, 1));
            }
        }
        return points;
    }


    @Override
    public void firstClick(int x, int y) {
        initMine(x, y);
        onGameListener.gameStart();
        OK = true;
    }

    @Override
    public void DoubleClickNum(int x, int y) {
        int num = boxArray[x][y].getNum();
        ArrayList<Point> points = calculateAround(x, y);
        ArrayList<Point> points2 = new ArrayList<>();
        for (Point point : points) {
            if (boxArray[point.x][point.y].isFlag()) {
                num--;
            } else {
                if (!boxArray[point.x][point.y].isOpen())
                    points2.add(point);
            }
        }
        if (num == 0) {
            for (Point point : points2) {
                boxArray[point.x][point.y].open(false);
            }
        }
    }

    @Override
    public void openZero(int x, int y) {
        ArrayList<Point> points = calculateAround(x, y);
        for (Point point : points) {
            boxArray[point.x][point.y].open(false);
        }
    }

    @Override
    public boolean isOk() {
        return OK;
    }

    @Override
    public void openSuccess() {
        if (--remainingBox == 0) {
            success();
        }
    }

    @Override
    public void clickMine() {
        failure();
    }

    @Override
    public void flagChange(boolean make) {
        if (make) {
            remainingFlag--;
        } else {
            remainingFlag++;
        }
        onGameListener.gameFlagChange(remainingFlag);
    }
}
