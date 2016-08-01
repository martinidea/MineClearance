package com.code.martin.mineclearance.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.code.martin.mineclearance.ALG.MineALG;
import com.code.martin.mineclearance.ALG.Mode;
import com.code.martin.mineclearance.R;

import java.util.ArrayList;

/**
 * Created by Martin on 2016/7/21.
 */
public class BoxTable extends TableLayout implements BoxClickListener, OnClickEventListener {

    private static Box[][] boxArray;//Box二维数组
    private int boxTableMode;//BoxTable难度级别
    private int boxWidth, boxHeight;//Box视图的宽高
    private TableRow.LayoutParams params;//Box的LayoutParams

    private static boolean OK;//用于Box类调用，是否设置好雷

    private final int[] RES_NUM = {
            R.drawable.box0, R.drawable.box1, R.drawable.box2,
            R.drawable.box3, R.drawable.box4, R.drawable.box5,
            R.drawable.box6, R.drawable.box7, R.drawable.box8
    };//存放资源图片ID

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

    public BoxTable(Context context) {
        this(context, null);
    }


    /**
     * 用于实现游戏周期
     *
     * @param onGameListener 设置游戏回调监听
     */
    public void setOnGameListener(OnGameListener onGameListener) {
        this.onGameListener = onGameListener;
    }

    /**
     * @return 是否已经设置好雷
     */
    public static boolean isOK() {
        return OK;
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
        flagArray = new boolean[maxX + 1][maxY + 1];
        openTable = new boolean[maxX + 1][maxY + 1];
        flagNumArray = new short[maxX + 1][maxY + 1];
        createdBox();
    }

    /**
     * 根据难度级别，创建Box
     */
    private void createdBox() {
        for (int i = 0; i < boxArray.length; i++) {
            TableRow tr = new TableRow(getContext());
            for (int j = 0; j < boxArray[0].length; j++) {
                boxArray[i][j] = new Box(getContext());
                boxArray[i][j].setX(i);
                boxArray[i][j].setY(j);
                boxArray[i][j].setOnClickEventListener(this);
                tr.addView(boxArray[i][j], params);
            }
            addView(tr);
        }
    }

    /**
     * OnClickEventListener回调方法，用于让Box传递事件回到BoxTable中
     *
     * @param event：点击事件
     */
    public void postEvent(ClickEvent event) {
        int x = event.getX(), y = event.getY();
        switch (event.getClickType()) {
            case DoubleClick:
                onDoubleClick(x, y);
                break;
            case SingleClick:
                onSingleClick(x, y);
                break;
            case FirstClick:
                onFirstClick(x, y);
                break;
        }
    }


    /**
     * @param event
     * @return false 不拦截Touch事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
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
        OK = true;
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
     * Box打开方法
     *
     * @param x 打开的X坐标
     * @param y 打开的Y坐标
     */
    private void open(int x, int y) {
        if (!openTable[x][y])
            open1(x, y);
        else {
            openDoubleClick(x, y);
        }
    }


    /**
     * 普通Box打开方法
     * @param x 打开的X坐标
     * @param y 打开的Y坐标
     */
    private void open1(int x, int y) {
        if (flagArray[x][y]) return;
        else {
            openTable[x][y] = true;
            remainingBox--;
            if (mineArray[x][y]) {
                failure();
                boxArray[x][y].setBackgroundResource(R.drawable.mine2);
            } else {
                if (remainingBox == 0) {
                    success();
                }
                boxArray[x][y].setBackgroundResource(RES_NUM[numArray[x][y]]);
                if (numArray[x][y] == 0) {
                    openAround(x, y);
                }
            }
        }
    }

    /**
     * 双击打开调用
     * @param x 打开的X坐标
     * @param y 打开的Y坐标
     */
    private void open2(int x, int y) {
        if (!openTable[x][y])
            open1(x, y);
    }

    /**
     * 打开周围Box
     *
     * @param x 打开的X坐标
     * @param y 打开的Y坐标
     */
    private void openAround(int x, int y) {
        for (Point point : calculateAround(x, y)) {
            open(point.x, point.y);
        }
    }

    /**
     * 双击数字打开Box
     *
     * @param x 打开的X坐标
     * @param y 打开的Y坐标
     */
    private void openDoubleClick(int x, int y) {
        if (flagNumArray[x][y] == numArray[x][y] && flagNumArray[x][y] != 0) {
            for (Point point : calculateAround(x, y)) {
                open2(point.x, point.y);
            }
        }
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
        for (int i = 0; i < mineArray.length; i++) {
            for (int j = 0; j < mineArray[0].length; j++) {
                if (mineArray[i][j]) {
                    if (!flagArray[i][j])
                        boxArray[i][j].setBackgroundResource(R.drawable.mine);
                } else if (flagArray[i][j]) {
                    boxArray[i][j].setBackgroundResource(R.drawable.mine1);
                }
            }
        }
    }

    /**
     * 设置标记
     * @param x 打开的X坐标
     * @param y 打开的Y坐标
     */
    private void makeFlag(int x, int y) {
        if (openTable[x][y])
            return;
        flagArray[x][y] = !flagArray[x][y];
        if (flagArray[x][y]) {
            boxArray[x][y].setBackgroundResource(R.drawable.flag);
            onGameListener.gameFlagChange(--remainingFlag);
            for (Point point : calculateAround(x, y)) {
                flagNumArray[point.x][point.y]++;
            }
        } else {
            boxArray[x][y].setBackgroundResource(R.drawable.blank);
            onGameListener.gameFlagChange(++remainingFlag);
            for (Point point : calculateAround(x, y)) {
                flagNumArray[point.x][point.y]--;
            }
        }
    }

    /**
     * 计算点的周围
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


    /**
     * 单击事件
     * @param x  x坐标
     * @param y  y坐标
     */
    @Override
    public void onSingleClick(int x, int y) {
        if (boxClickAble)
            makeFlag(x, y);
    }

    /**
     * 双击事件
     * @param x  x坐标
     * @param y  y坐标
     */
    @Override
    public void onDoubleClick(int x, int y) {
        if (boxClickAble) open(x, y);
    }

    /**
     * 首欠点击事件，开始设置雷
     * @param x  x坐标
     * @param y  y坐标
     */
    @Override
    public void onFirstClick(int x, int y) {
        if (boxClickAble) {
            initMine(x, y);
            open(x, y);
            onGameListener.gameStart();
        }
    }
}
