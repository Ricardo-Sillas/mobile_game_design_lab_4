package edu.utep.cs.cs4381.ballfall.model;

import android.content.Context;
import android.graphics.Rect;

import java.util.Random;

import edu.utep.cs.cs4381.ballfall.BFView;

public class Platforms {

    private int x;
    private int y;
    private int maxX;
    private int maxY;
    private int minX;
    private int minY;
    private double speed;
    private int gapStart;
    private int gapEnd;
    private Rect hitbox1;
    private Rect hitbox2;

    private static final Random random = new Random();

    public Platforms(Context context, int screenX, int screenY, int setY) {
        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = 0;
        speed = 2;
        y = setY;
        gapStart = random.nextInt((2*screenX)/3)+(screenX/12);
        gapEnd = gapStart + screenX/8;
        hitbox1 = new Rect(0, y, screenX, y+40);
        hitbox2 = new Rect(gapStart, y, gapEnd, y+40);
    }

    public void update() {
// Setting speed accordingly to players score
        if(BFView.currentScore > 0 && BFView.currentScore % 10 == 0) {
            speed += (1.0/10.0);
        }
// Brings platform to top of screen
        y -= speed;
        if (y < minY-40) {
            gapStart = random.nextInt((2*maxX)/3)+(maxX/12);
            gapEnd = gapStart + maxX/8;
            y = maxY;
        }
// Hitboxes
        hitbox1.set(0, y, maxX, y+40);
        hitbox2.set(gapStart, y, gapEnd, y+40);
    }

// Setters and getters

    public double getSpeed() {
        return speed;
    }

    public int getY() {
        return y;
    }

    public int getGapStart() {
        return gapStart;
    }

    public int getGapEnd() {
        return gapEnd;
    }

    public Rect getHitbox1() {
        return hitbox1;
    }

    public Rect getHitbox2() {
        return hitbox2;
    }
}
