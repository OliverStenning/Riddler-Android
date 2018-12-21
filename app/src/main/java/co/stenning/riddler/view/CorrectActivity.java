package co.stenning.riddler.view;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import co.stenning.riddler.R;

public class CorrectActivity extends AppCompatActivity {

    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_correct);

        continueButton = findViewById(R.id.continueButton);
        continueButton.setAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse));

    }

    public void clickContinue(View view) {
        finish();
        overridePendingTransition(0, 0);
    }

}
