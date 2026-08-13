package minesweeper;

import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;

public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 64;
    public static int WIDTH = 864; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int FPS = 30;

    public String configPath;

    public static Random random = new Random();

    public static int[][] mineCountColour = new int[][] {
            {0,0,0}, // 0 is not shown
            {0,0,255},
            {0,133,0},
            {255,0,0},
            {0,0,132},
            {132,0,0},
            {0,132,132},
            {132,0,132},
            {32,32,32}
    };

    // Feel free to add any additional methods or attributes you want. Please put classes in different files.

    //图像
    public PImage[] tileImages = new PImage[3];
    public PImage[] mineImages = new PImage[10];
    public PImage flagImage;
    public PImage wallImage;

    //游戏状态
    public boolean[][] board; // 1表示有地雷，0表示无地雷
    public boolean[][] flagged; //表示是否被插上旗子
    public boolean[][] revealed; //表示是是否被揭开
    public boolean gameOver; //表示游戏是否结束
    public boolean victory; //表示游戏是否通关

    //地雷个数，获取命令行参数
    public static String[] inputArgs;



    //爆炸效果所需要的变量
    public boolean explosionStarted;
    public ArrayList<int[]> minesToExplode = new ArrayList<>();
    int[] mineExplosionFrameCounter;  // 用于跟踪每个地雷的爆炸帧数
    int explosionStartFrame = 0;  // 记录第一个地雷开始爆炸的帧数

    // 计时器相关变量
    int startTime;  // 记录游戏开始的时间
    int elapsedTime;  // 记录经过的时间（以秒为单位）
    boolean timerStarted = false;  // 追踪计时器是否已经开始





    public App()
    {
        this.configPath = "config.json";
    }


    @Override
    public void settings()
    {
        size(WIDTH, HEIGHT);
    }


    @Override
    public void setup()
    {
        frameRate(FPS);


        //加载tile的图片
        tileImages[0] = loadImage("minesweeper/tile.png");
        tileImages[1] = loadImage("minesweeper/tile1.png");
        tileImages[2] = loadImage("minesweeper/tile2.png");

        //加载mine的图片
        mineImages[0] = loadImage("minesweeper/mine0.png");
        mineImages[1] = loadImage("minesweeper/mine1.png");
        mineImages[2] = loadImage("minesweeper/mine2.png");
        mineImages[3] = loadImage("minesweeper/mine3.png");
        mineImages[4] = loadImage("minesweeper/mine4.png");
        mineImages[5] = loadImage("minesweeper/mine5.png");
        mineImages[6] = loadImage("minesweeper/mine6.png");
        mineImages[7] = loadImage("minesweeper/mine7.png");
        mineImages[8] = loadImage("minesweeper/mine8.png");
        mineImages[9] = loadImage("minesweeper/mine9.png");

        //加载flag的图片
        flagImage = loadImage("minesweeper/flag.png");

        //加载wall的图片
        wallImage = loadImage("minesweeper/wall0.png");

        //将函数重新初始化

        resetGame();
    }

    public void resetGame()
    {
        board = new boolean[BOARD_HEIGHT][BOARD_WIDTH]; // 初始化网格，false 表示没有地雷
        revealed = new boolean[BOARD_HEIGHT][BOARD_WIDTH]; // 初始化揭示状态，所有格子未揭示
        flagged = new boolean[BOARD_HEIGHT][BOARD_WIDTH]; // 初始化旗子标记状态，所有格子未被标记
        gameOver = false; // 游戏还未结束
        victory = false; // 游戏还未胜利

        //获取地雷个数
        //初始化地雷的个数
        int mineNumber;
        if (inputArgs.length == 0) // 初始化默认地雷个数
        {
            mineNumber = 100;
        }
        else // 初始化添加命令行后的地雷个数
        {
            mineNumber = Integer.parseInt(args[0]);
        }




        // 随机放置地雷
        int placedMines = 0;
        while (placedMines < mineNumber)
        {
            int row = random.nextInt(BOARD_HEIGHT); // 随机生成行
            int col = random.nextInt(BOARD_WIDTH); // 随机生成列
            if (!board[row][col])
            {
                // 如果当前格子没有地雷
                board[row][col] = true; // 放置地雷
                placedMines = placedMines + 1; // 地雷计数加一
            }
        }

        //重置爆炸状态
        // 重置爆炸状态
        explosionStarted = false;
        minesToExplode.clear();
        mineExplosionFrameCounter = null;
        explosionStartFrame = 0;

        // 重置计时器
        timerStarted = true;
        startTime = millis();  // 记录游戏开始时的时间
    }



    @Override
    public void keyPressed(KeyEvent event)
    {
        if (key == 'r' || key == 'R')
        {
            resetGame();  // 调用重置游戏函数
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (gameOver || victory) {
            return;  // 游戏结束时，禁止点击操作
        }

        int col = mouseX / CELLSIZE;  // 计算点击的列
        int row = (mouseY - TOPBAR) / CELLSIZE;  // 计算点击的行，减去顶部栏高度

        if (mouseButton == LEFT) {
            // 检查点击的格子是否已经被揭示或标记，避免重复揭示
            if (row >= 0 && row < BOARD_HEIGHT && col >= 0 && col < BOARD_WIDTH && !revealed[row][col] && !flagged[row][col]) {

                // 检查是否点击到地雷
                if (board[row][col]) {
                    // 点到地雷，游戏结束，启动爆炸
                    gameOver = true;
                    explosionStarted = true;

                    // 搜集所有的地雷，准备启动爆炸
                    minesToExplode.clear();
                    for (int r = 0; r < BOARD_HEIGHT; r++) {
                        for (int c = 0; c < BOARD_WIDTH; c++) {
                            if (board[r][c]) {
                                minesToExplode.add(new int[]{r, c});
                            }
                        }
                    }

                    // 初始化每个地雷的爆炸帧数控制器
                    mineExplosionFrameCounter = new int[minesToExplode.size()];
                    Arrays.fill(mineExplosionFrameCounter, -1);  // 初始化为-1，表示未开始爆炸
                    explosionStartFrame = frameCount;  // 记录第一个地雷开始爆炸的帧数
                }
                else {
                    // 如果没有点击到地雷，递归揭示周围的格子
                    revealSurroundingCells(row, col);
                }
            }
        } else if (mouseButton == RIGHT) {
            // 右键点击标记地雷
            if (!revealed[row][col]) {  // 只有未揭示的格子可以标记
                flagged[row][col] = !flagged[row][col];  // 切换标记状态
            }
        }
    }


    public void revealSurroundingCells(int row, int col) {
        // 如果该格子已经被揭示或者超出边界，直接返回
        if (row < 0 || row >= BOARD_HEIGHT || col < 0 || col >= BOARD_WIDTH || revealed[row][col]) {
            return;
        }

        // 揭示当前格子
        revealed[row][col] = true;

        // 计算当前格子周围的地雷数
        int mineCount = countAdjacentMines(row, col);

        // 如果当前格子周围没有地雷，则递归揭示周围的格子
        if (mineCount == 0) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i != 0 || j != 0) {
                        revealSurroundingCells(row + i, col + j);  // 递归展开相邻的格子
                    }
                }
            }
        }
    }

    public int countAdjacentMines(int row, int col) {
        int mineCount = 0;  // 用于计数相邻地雷数量

        // 遍历当前格子周围的8个邻近格子
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;

                // 确保新的行列没有超出网格的边界
                if (newRow >= 0 && newRow < BOARD_HEIGHT && newCol >= 0 && newCol < BOARD_WIDTH) {
                    if (board[newRow][newCol]) {  // 如果邻近格子有地雷
                        mineCount++;  // 地雷计数加一
                    }
                }
            }
        }

        return mineCount;  // 返回相邻地雷的数量
    }



    @Override
    public void draw()
    {
        //对胜利条件的检查
        if (!gameOver && !victory)
        {
            checkVictory();
        }

        //设置游戏背景
        background(200);

        // 更新并显示计时器
        if (timerStarted && !gameOver && !victory) {
            elapsedTime = (millis() - startTime) / 1000;  // 计算经过的时间（以秒为单位）
        }

        // 显示计时器
        fill(0);
        textSize(20);
        textAlign(RIGHT, TOP);
        text("Time: " + elapsedTime + "s", WIDTH - 10, 10);  // 计时器显示在右上角


        //遍历整个游戏网络，画出每个格子的状态
        for (int row = 0; row < BOARD_HEIGHT; row = row + 1)
        {
            for (int col = 0; col < BOARD_WIDTH; col = col + 1)
            {
                int x = col * CELLSIZE;
                int y = row * CELLSIZE + TOPBAR;

                if (flagged[row][col]) // 如果格子上有旗子，就画个旗子上去
                {
                    image(tileImages[1],x,y,CELLSIZE,CELLSIZE);
                    image(flagImage,x,y,CELLSIZE,CELLSIZE);
                }
                else if (!revealed[row][col]) //如果格子还没有被揭开，就画深蓝tile上去
                {
                    if (mouseX >= x && mouseX < x + CELLSIZE && mouseY >= y && mouseY < y + CELLSIZE)
                    {
                        image(tileImages[2],x,y,CELLSIZE,CELLSIZE);
                    }
                    else
                    {
                        image(tileImages[1],x,y,CELLSIZE,CELLSIZE);
                    }

                }
                else //格子已经被揭开了
                {
                    if (board[row][col])//如果该格子是地雷
                    {
                        image(mineImages[0],x,y,CELLSIZE,CELLSIZE);
                    }
                    else //该格子里面没有地雷
                    {
                        // 如果不是地雷，显示已揭示的空白格子，并显示相邻地雷数量
                        image(wallImage, x, y, CELLSIZE, CELLSIZE);
                        int mineCount = countAdjacentMines(row, col);
                        if (mineCount > 0)
                        {
                            // 显示相邻地雷的数量
                            fill(mineCountColour[mineCount][0], mineCountColour[mineCount][1], mineCountColour[mineCount][2]);
                            textSize(20);
                            textAlign(CENTER, CENTER);
                            text(mineCount, x + CELLSIZE / 2, y + CELLSIZE / 2);
                        }
                    }
                }



            }
        }

        // 如果游戏结束或胜利，显示对应的消息
        if (gameOver)
        {
            fill(255, 0, 0);
            textSize(32);
            textAlign(CENTER, CENTER);
            text("You Lost!", WIDTH / 2, TOPBAR / 2);
        }
        else if (victory)
        {
            fill(0, 255, 0);
            textSize(32);
            textAlign(CENTER, CENTER);
            text("You Win!", WIDTH / 2, TOPBAR / 2);
        }

        // 爆炸逻辑：每3帧开始一个新的地雷爆炸
        if (explosionStarted) {
            for (int i = 0; i < minesToExplode.size(); i++) {
                int[] minePos = minesToExplode.get(i);
                int row = minePos[0];
                int col = minePos[1];
                int x = col * CELLSIZE;
                int y = row * CELLSIZE + TOPBAR;

                // 每隔3帧开始一个新的地雷爆炸
                if (frameCount >= explosionStartFrame + i * 3) {
                    if (mineExplosionFrameCounter[i] == -1) {
                        mineExplosionFrameCounter[i] = 0;  // 开始爆炸
                    }

                    // 显示爆炸动画
                    if (mineExplosionFrameCounter[i] < mineImages.length) {
                        image(mineImages[mineExplosionFrameCounter[i]], x, y, CELLSIZE, CELLSIZE);
                        mineExplosionFrameCounter[i]++;
                    } else {
                        // 保持最后一帧的爆炸图片
                        image(mineImages[mineImages.length - 1], x, y, CELLSIZE, CELLSIZE);
                    }
                }
            }
        }
    }

    public void checkVictory()
    {
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (!board[row][col] && !revealed[row][col]) {
                    return;  // 有未揭示的非地雷格子，尚未获胜
                }
            }
        }
        victory = true; // 所有非地雷格子都被揭示，玩家获胜
    }



    public static void main(String[] args) {
        inputArgs = args;
        PApplet.main("minesweeper.App",args);

    }

}