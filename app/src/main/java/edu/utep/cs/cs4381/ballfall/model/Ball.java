package edu.utep.cs.cs4381.ballfall.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import edu.utep.cs.cs4381.ballfall.BFView;
import edu.utep.cs.cs4381.ballfall.R;

public class Ball {

    private int x;
    private int y;
    private int maxX;
    private int maxY;
    private int minX;
    private int minY;
    private double speed;
    private Bitmap bitmap;
    private boolean moving;
    private boolean goRight;
    private Rect hitbox;
    private boolean falling;

    public Ball(Context context, int screenX, int screenY) {
        falling = true;
        x = 50;
        y = 50;
        speed = 8;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball1);
        maxX = screenX - bitmap.getHeight();
        minX = 0;
        maxY = screenY - bitmap.getHeight();
        minY = 0;
        moving = false;
        goRight = false;
        hitbox = new Rect(x, y  , x + bitmap.getWidth(), y + bitmap.getHeight());
    }

// In order to find out if the ball should go right or left
    public void setGoRight(boolean right) {
        goRight = right;
    }

    public void setMoving(boolean flag) {
        moving = flag;
    }

    public void update() {
// Setting speed accordingly to players score
        if(BFView.currentScore > 0 && BFView.currentScore % 10 == 0) {
            speed += (2.0/10.0);
        }
// Moves ball accordingly to players touch input
        if(moving) {
            if(goRight) {
                x += speed;
            }
            else {
                x -= speed;
            }
        }
// limits ball into the screen
        if (y > maxY) {
            y = maxY;
        }
        if(x > maxX) {
            x = maxX;
        }
        if(x < minX) {
            x = minX;
        }
// Moves ball accordingly to platform
        for(Platforms floor: BFView.platforms) {
            if(!falling && Rect.intersects(hitbox, floor.getHitbox1())) {
                if(y < floor.getY()) {
                    y = floor.getY()-bitmap.getHeight();
                }
            }
            if(falling && Rect.intersects(hitbox, floor.getHitbox2())) {
                if(x < floor.getGapStart()) {
                    x = floor.getGapStart()+1;
                }
                else if(x+bitmap.getWidth() > floor.getGapEnd()) {
                    x = floor.getGapEnd()-bitmap.getWidth()-1;
                }
            }
            if(falling && Rect.intersects(hitbox, floor.getHitbox1()) && !Rect.intersects(hitbox, floor.getHitbox2())) {
                y += bitmap.getHeight();
            }
        }
// hitbox
        hitbox.set(x, y, x + bitmap.getWidth(), y + bitmap.getHeight());
    }

// Setters and Getters

    public int getX() {
        return x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Rect getHitbox() {
        return hitbox;
    }

    public void setFalling(boolean falling) {
        this.falling = falling;
    }
}
