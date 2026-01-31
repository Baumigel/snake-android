package com.example.snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class SnakeView extends SurfaceView implements Runnable, SensorEventListener, SurfaceHolder.Callback {

    private Thread gameThread;
    private boolean isPlaying;
    private SurfaceHolder surfaceHolder;
    private Paint paint;
    private Canvas canvas;

    private final int BLOCK_SIZE = 30;
    private final int GAME_SPEED = 150;
    private int COLS, ROWS;

    private ArrayList<int[]> snake;
    private int[] food;
    private int direction;
    private int nextDirection;
    private int score;
    private boolean gameOver;

    private Random random;
    private Handler handler;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float lastX, lastY;

    public SnakeView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        paint = new Paint();
        random = new Random();
        handler = new Handler();

        COLS = getResources().getDisplayMetrics().widthPixels / BLOCK_SIZE;
        ROWS = getResources().getDisplayMetrics().heightPixels / BLOCK_SIZE;

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (COLS > 0 && ROWS > 0) {
            initGame();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        COLS = Math.max(1, width / BLOCK_SIZE);
        ROWS = Math.max(1, height / BLOCK_SIZE);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pause();
    }

    private void initGame() {
        snake = new ArrayList<>();
        snake.add(new int[]{COLS / 2, ROWS / 2});
        snake.add(new int[]{COLS / 2, ROWS / 2 + 1});
        snake.add(new int[]{COLS / 2, ROWS / 2 + 2});

        direction = 0;
        nextDirection = 0;
        score = 0;
        gameOver = false;

        spawnFood();
    }

    private void restartGame() {
        snake.clear();
        initGame();
    }

    private void spawnFood() {
        boolean validPosition = false;
        while (!validPosition) {
            int x = random.nextInt(COLS);
            int y = random.nextInt(ROWS);
            
            if (x >= 0 && x < COLS && y >= 0 && y < ROWS) {
                food = new int[]{x, y};
                validPosition = true;
                
                for (int[] segment : snake) {
                    if (segment[0] == food[0] && segment[1] == food[1]) {
                        validPosition = false;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            try {
                Thread.sleep(GAME_SPEED);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        if (gameOver || snake == null) return;

        direction = nextDirection;
        int[] head = snake.get(0).clone();

        switch (direction) {
            case 0:
                head[1]--;
                break;
            case 1:
                head[1]++;
                break;
            case 2:
                head[0]--;
                break;
            case 3:
                head[0]++;
                break;
        }

        if (head[0] < 0 || head[0] >= COLS || head[1] < 0 || head[1] >= ROWS) {
            gameOver = true;
            return;
        }

        for (int[] segment : snake) {
            if (head[0] == segment[0] && head[1] == segment[1]) {
                gameOver = true;
                return;
            }
        }

        snake.add(0, head);

        if (head[0] == food[0] && head[1] == food[1]) {
            score += 10;
            spawnFood();
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();

            canvas.drawColor(Color.BLACK);

            paint.setColor(Color.RED);
            for (int[] segment : snake) {
                canvas.drawRect(
                    segment[0] * BLOCK_SIZE,
                    segment[1] * BLOCK_SIZE,
                    (segment[0] + 1) * BLOCK_SIZE,
                    (segment[1] + 1) * BLOCK_SIZE,
                    paint
                );
            }

            paint.setColor(Color.GREEN);
            canvas.drawRect(
                food[0] * BLOCK_SIZE,
                food[1] * BLOCK_SIZE,
                (food[0] + 1) * BLOCK_SIZE,
                (food[1] + 1) * BLOCK_SIZE,
                paint
            );

            paint.setColor(Color.WHITE);
            paint.setTextSize(40);
            canvas.drawText("Score: " + score, 20, 50, paint);

            if (gameOver) {
                paint.setColor(Color.WHITE);
                paint.setTextSize(80);
                canvas.drawText("GAME OVER", 100, canvas.getHeight() / 2, paint);
                paint.setTextSize(40);
                canvas.drawText("Tap to restart", 100, canvas.getHeight() / 2 + 60, paint);
            }

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (gameOver) {
                restartGame();
            } else {
                float x = event.getX();
                float y = event.getY();

                float dx = x - snake.get(0)[0] * BLOCK_SIZE;
                float dy = y - snake.get(0)[1] * BLOCK_SIZE;

                if (Math.abs(dx) > Math.abs(dy)) {
                    if (dx > 0 && direction != 2) {
                        nextDirection = 3;
                    } else if (dx < 0 && direction != 3) {
                        nextDirection = 2;
                    }
                } else {
                    if (dy > 0 && direction != 0) {
                        nextDirection = 1;
                    } else if (dy < 0 && direction != 1) {
                        nextDirection = 0;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && !gameOver) {
            float x = event.values[0];
            float y = event.values[1];
            float sensitivity = 3.0f;

            if (Math.abs(x) > Math.abs(y)) {
                if (x > sensitivity && direction != 3) {
                    nextDirection = 2;
                } else if (x < -sensitivity && direction != 2) {
                    nextDirection = 3;
                }
            } else {
                if (y > sensitivity && direction != 0) {
                    nextDirection = 1;
                } else if (y < -sensitivity && direction != 1) {
                    nextDirection = 0;
                }
            }
        }
    }

    public void pause() {
        isPlaying = false;
        sensorManager.unregisterListener(this);
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        gameThread = new Thread(this);
        gameThread.start();
    }
}
