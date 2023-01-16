package edu.utep.cs.cs4381.ballfall;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

public class HighScoreRecorder {
    private static final String PREF_FILE = "HighScore";
    private static final String PREF_KEY = "Score";
    private static final long DEFAULT_VALUE = 0;
    private static HighScoreRecorder theInstance;
    private Context context;

    private static SharedPreferences prefs;

    private HighScoreRecorder(Context ctx) {
        prefs = ctx.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        this.context = ctx;
    }

    public void store(long time) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_FILE, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(PREF_KEY, time);
        editor.commit();
    }

    public static long retrieve() {
        long highScore = prefs.getLong(PREF_KEY, DEFAULT_VALUE);
        return highScore;
    }

    static String TotalScore(String label, long score) {
        return String.format("%s:\n %d", label, score);
    }

    public static HighScoreRecorder instance(@Nullable Context ctx) {
        if (theInstance == null) {
            theInstance = new HighScoreRecorder(ctx);
        }
        return theInstance;
    }
}