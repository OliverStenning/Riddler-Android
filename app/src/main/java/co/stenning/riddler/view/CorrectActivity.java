package co.stenning.riddler.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import co.stenning.riddler.R;

public class CorrectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_correct);

        int score = getIntent().getIntExtra(QuestionActivity.SCORE_EXTRA, 0);

        TextView scoreText = findViewById(R.id.scoreText);
        scoreText.setText(getString(R.string.correct_score_label) + " " + Integer.toString(score));
    }

    public void clickContinue(View view) {
        finish();
        overridePendingTransition(0, 0);
    }

}
