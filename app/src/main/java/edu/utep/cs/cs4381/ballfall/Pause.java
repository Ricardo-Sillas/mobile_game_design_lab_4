package edu.utep.cs.cs4381.ballfall;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Pause {

    private Bitmap bitmap;

    public Pause(Context context) {
        bitmap = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.pause);
    }

// If location tapped is where location of button is, return 1
    public static int buttons(float x, float y, int width) {
        if(isIn(x, y, 80, 80, 50))
            return 1;
        return -1;
    }

// Checks if location tapped is where location of button is
    public static boolean isIn(float x, float y, float cX, float cY, float radius) {
        float dx = x - cX;
        float dy = y - cY;
        return dx * dx + dy * dy <= radius * radius;
    }

// Setters and getters
    public Bitmap getBitmap() {
        return bitmap;
    }
}
