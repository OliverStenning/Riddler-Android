package co.stenning.riddler.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import co.stenning.riddler.R;
import co.stenning.riddler.util.PrefManager;

public class CorrectActivity extends AppCompatActivity {

    /* Ads */
    private PrefManager prefManager;
    private static final int QUESTIONS_BETWEEN_ADS = 1;
    private InterstitialAd correctAd;
    private boolean adShownIfMeant = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_correct);

        //initialise PrefManager to check consent
        prefManager = new PrefManager(this);

        //initialise mobile ads
        MobileAds.initialize(this, getString(R.string.app_ad_id));

        //initialise interstitial ad
        correctAd = new InterstitialAd(this);
        //TODO change to release ad id
        correctAd.setAdUnitId(getString(R.string.question_interstitial_id_test));

        //get questions since ad from intent pass to activity
        int questionsSinceAd = getIntent().getIntExtra(QuestionActivity.QUESTIONS_SINCE_AD, 0);
        if (questionsSinceAd >= QUESTIONS_BETWEEN_ADS) {
            AdRequest adRequest;
            if (!prefManager.hasConsentPersonalised()) {
                Bundle extras = new Bundle();
                extras.putString("npa", "1");

                adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build();
            } else {
                adRequest = new AdRequest.Builder().build();
            }
            correctAd.loadAd(adRequest);

            //tell Question Activity add was loaded in order to reset questions since ad
            correctAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    correctAd.show();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(QuestionActivity.AD_WATCHED, true);
                    setResult(Activity.RESULT_OK, resultIntent);
                    adShownIfMeant = true;
                }
            });
        } else {
            adShownIfMeant = true;
        }

        //get score from intent passed to activity
        int score = getIntent().getIntExtra(QuestionActivity.SCORE_EXTRA, 0);

        //display intent on UI
        TextView scoreText = findViewById(R.id.scoreText);
        scoreText.setText(getString(R.string.score_label) + " " + Integer.toString(score));
        //display skipped if score is 0
        if (score == 0) {
            TextView correctText = findViewById(R.id.correctLabel);
            correctText.setText(R.string.correct_skipped);
        }
    }

    public void clickContinue(View view) {
        if (adShownIfMeant) {
            finish();
            overridePendingTransition(0, 0);
        }
    }

}
