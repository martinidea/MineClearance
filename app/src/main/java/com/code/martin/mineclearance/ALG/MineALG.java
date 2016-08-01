package com.code.martin.mineclearance.ALG;

import android.util.Log;

import java.util.Random;

/**
 * Created by Martin on 2016/7/21.
 */
public class MineALG {
    private boolean[][] mineTable;
    private short[][] numTableHelp;
    private short[][] numTable;
    private Random random;
    private int x, y, maxX, maxY, mineCount;

    public MineALG(Mode mode) {
        switch (mode) {
            case Primary:
                maxX = 8;
                maxY = 8;
                mineCount = 10;
                break;
            case Middle:
                maxX = 15;
                maxY = 15;
                mineCount = 40;
                break;
            case Expert:
                maxX = 29;
                maxY = 15;
                mineCount = 100;
                break;
        }
        init();
    }

    private void init() {
        random = new Random();
        mineTable = new boolean[maxX + 1][maxY + 1];
        numTableHelp = new short[maxX + 3][maxY + 3];
        numTable = new short[maxX + 1][maxY + 1];
    }

    public void makeMine(int x1, int y1) {
        for (int i = 0; i < mineCount; i++) {
            x = random.nextInt(maxX + 1);
            y = random.nextInt(maxY + 1);
            if (Math.sqrt((x - x1) * (x - x1) + ((y - y1) * (y - y1))) <= Math.sqrt(2))
                i--;
            else if (mineTable[x][y])
                i--;
            else {
                mineTable[x][y] = true;
                aroundAdd1(x, y);
            }
        }
        for (int i = 0; i < numTable.length; i++) {
            for (int j = 0; j < numTable[0].length; j++) {
                if (mineTable[i][j]) {
                    numTableHelp[i + 1][j + 1] = 0;
                }
                numTable[i][j] = numTableHelp[i + 1][j + 1];
            }
        }
    }


    private void aroundAdd1(int x, int y) {
        numTableHelp[x][y]++;
        numTableHelp[x][y + 1]++;
        numTableHelp[x][y + 2]++;
        numTableHelp[x + 1][y]++;
        numTableHelp[x + 1][y + 2]++;
        numTableHelp[x + 2][y]++;
        numTableHelp[x + 2][y + 1]++;
        numTableHelp[x + 2][y + 2]++;
    }

    public boolean[][] getMineArray() {
        return mineTable;
    }

    public short[][] getNumArray() {
        return numTable;
    }
}
