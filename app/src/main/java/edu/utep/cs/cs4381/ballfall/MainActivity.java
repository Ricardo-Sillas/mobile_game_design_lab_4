package edu.utep.cs.cs4381.ballfall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button playButton = findViewById(R.id.playButton);
        final TextView textFastestTime = (TextView)findViewById(R.id.textHighScore);
        playButton.setOnClickListener(view -> {
            startActivity(new Intent(this, GameActivity.class));
            finish();
        });
        long fastestTime = HighScoreRecorder.instance(this).retrieve();
        textFastestTime.setText(HighScoreRecorder.TotalScore("HighScore", fastestTime));
    }
}