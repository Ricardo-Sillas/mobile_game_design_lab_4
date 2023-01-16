package edu.utep.cs.cs4381.ballfall;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.utep.cs.cs4381.ballfall.model.Ball;
import edu.utep.cs.cs4381.ballfall.model.Platforms;

public class BFView extends SurfaceView implements Runnable {

    private Ball ball;
    private SurfaceHolder holder;
    private Canvas canvas;
    private Paint paint;

    private SoundEffect soundEffect;

    private Context context;
    private Thread gameThread;
    private boolean playing;
    private int width;
    private int height;
    private long bestScore;
    public static long currentScore;
    private boolean gameEnded;
    private boolean gotPoint;
    private double underGap;
    private boolean playSound;
    private Pause pause;
    private boolean pauseGame;
    private int color;

    public static List<Platforms> platforms = new CopyOnWriteArrayList<>();

    public BFView(Context context, int width, int height) {
        super(context);

        soundEffect = new SoundEffect(context);

        bestScore = HighScoreRecorder.instance(context).retrieve();
        this.context = context;
        this.width = width;
        this.height = height;
        holder = getHolder();
        paint = new Paint();
        startGame();

        pause = new Pause(context);
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    private void update() {
// While game hasn't ended or paused
        if(!gameEnded && !pauseGame) {
// For updating the user's points and updating speed
            for(Platforms floor : platforms) {
                if(Rect.intersects(ball.getHitbox(), floor.getHitbox1()) ||
                        Rect.intersects(ball.getHitbox(), floor.getHitbox2())) {
                    if(Rect.intersects(ball.getHitbox(), floor.getHitbox2()) &&
                            floor.getGapEnd() > (ball.getX()+ball.getBitmap().getWidth()) && floor.getGapStart() < ball.getX()) {
                        if(!gotPoint) {
                            soundEffect.play(SoundEffect.Sound.Point);
                            playSound = true;
                            underGap = (10/floor.getSpeed()) + (ball.getBitmap().getHeight()/floor.getSpeed());
                            currentScore++;
                            gotPoint = true;
                            ball.setFalling(true);
                        }
                    }
                    else {
                        if(playSound) {
                            soundEffect.play(SoundEffect.Sound.Land);
                            playSound = false;
                        }
                        ball.setY(ball.getY() - (int)floor.getSpeed()-4);
                        ball.setFalling(false);
                    }
                }
            }
// Setting up things for the score updater
            underGap--;
            if(underGap <= 0) {
                gotPoint = false;
            }
// Updating ball and platforms
            ball.setY(ball.getY()+4);
            if(ball.getY()+ball.getBitmap().getHeight()<=0) {
                gameEnded = true;
            }
            ball.update();
            for(Platforms floor: platforms) {
                floor.update();
            }
        }
// When game ends
        if(gameEnded) {
            if(currentScore > bestScore) {
                HighScoreRecorder.instance(null).store(currentScore);
                bestScore = currentScore;
            }
        }
    }

    private void draw() {
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();
            canvas.drawColor(Color.argb(255, 0, 0, 0));
            if(!gameEnded && !pauseGame) {
// Updating colors for platforms and drawing them
                for (Platforms floor: platforms) {
                    paint.setColor(pickColor());
                    canvas.drawRect(floor.getHitbox1(), paint);
                    paint.setColor(Color.BLACK);
                    canvas.drawRect(floor.getHitbox2(), paint);
                }
// Drawing ball
                canvas.drawBitmap(ball.getBitmap(),ball.getX(),ball.getY(),paint);
// HUD
                paint.setTextSize(100);
                paint.setTextAlign(Paint.Align.RIGHT);
                paint.setColor(Color.WHITE);
                canvas.drawText("Score: " + currentScore, width - 10, 100, paint);
                canvas.drawBitmap(pause.getBitmap(), 50, 50, paint);
            }
// Info when game paused
            else if(pauseGame) {
                paint.setTextSize(100);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setColor(Color.CYAN);
                canvas.drawText("Score :" + currentScore, width/2, height/2-100, paint);
                canvas.drawText("Highscore :" + bestScore, width/2, height/2, paint);
                canvas.drawText("Tap to continue!", width/2, height/2+100, paint);
// Info when game ended
            }
            else{
                canvas.drawColor(Color.BLACK);
                paint.setTextSize(100);
                paint.setTextAlign((Paint.Align.CENTER));
                paint.setColor(Color.CYAN);
                canvas.drawText("GameOver", width/2, height/2-165, paint);
                canvas.drawText("Score :" + currentScore, width/2, height/2-55, paint);
                canvas.drawText("Highscore :" + bestScore, width/2, height/2+55, paint);
                canvas.drawText("Tap to replay", width/2, height/2+165, paint);
            }
            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
// Tells ball class that the ball should be moving
                ball.setMoving(true);
// For pausing/unpausing game
                if(pauseGame) {
                    pauseGame = false;
                }
                if (Pause.buttons(motionEvent.getX(), motionEvent.getY(), width) == 1) {
                    pauseGame = true;
                }
// Start game again when ended
                if (gameEnded) {
                    startGame();
                }
// Tells ball class that ball should move right if location tapped is right side of ball
                if(motionEvent.getX() > ball.getX()) {
                    ball.setGoRight(true);
                }
// Tells ball class that ball should move left if location tapped is left side of ball
                else if(motionEvent.getX() < ball.getX()) {
                    ball.setGoRight(false);
                }
// Tells ball class that ball shouldn't move if location tapped is at balls location
                else {
                    ball.setMoving(false);
                }
                break;
// No input, so tells ball class that ball shouldn't move
            case MotionEvent.ACTION_UP:
                ball.setMoving(false);
                break;
        }
        return true;
    }

// To start game
    private void startGame() {
        ball = new Ball(context, width, height);
        platforms.clear();
        for(int i = 1; i < 10; i++) {
            platforms.add(new Platforms(context, width, height, (height*i)/9));
        }
// Getting everything ready for the game
        currentScore = 0;
        pauseGame = false;
        gameEnded = false;
        gotPoint = false;
        playSound = false;
    }

// Colors being used for platform
    private static int[] colorsToPickFrom = {
            Color.CYAN,
            Color.WHITE,
            Color.BLUE,
            Color.LTGRAY,
            Color.GREEN,
            Color.YELLOW,
            Color.MAGENTA};

// Picking colors from the color array
    private int pickColor() {
        if (currentScore % 10 == 0) {
            Random random = new Random();
            color = colorsToPickFrom[random.nextInt(colorsToPickFrom.length)];
            return color;
        }
        return color;
    }

    private void control() {
        try {
            gameThread.sleep(17); // in milliseconds
        } catch (InterruptedException e) {
        }
    }
}