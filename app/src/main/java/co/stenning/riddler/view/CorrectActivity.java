package co.stenning.riddler.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import co.stenning.riddler.R;
import co.stenning.riddler.util.PrefManager;

public class CorrectActivity extends AppCompatActivity {

    private static final int QUESTIONS_BETWEEN_ADS = 1;
    private InterstitialAd correctAd;
    private boolean adShownIfMeant = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_correct);

        //initialise PrefManager to check consent
        /* Ads */
        PrefManager prefManager = new PrefManager(this);

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

            InterstitialAd.load(this, getString(R.string.question_interstitial_id), adRequest, new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            System.out.println(loadAdError);
                            correctAd = null;
                        }

                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            super.onAdLoaded(interstitialAd);
                            correctAd = interstitialAd;

                            correctAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdClicked() {
                                    super.onAdClicked();
                                }

                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    super.onAdFailedToShowFullScreenContent(adError);
                                    adShownIfMeant = true;
                                }

                                @Override
                                public void onAdImpression() {
                                    super.onAdImpression();
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    super.onAdShowedFullScreenContent();
                                    correctAd = null;

                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra(QuestionActivity.AD_WATCHED, true);
                                    setResult(Activity.RESULT_OK, resultIntent);
                                    adShownIfMeant = true;
                                }
                            });

                            Activity activityContext = CorrectActivity.this;
                            correctAd.show(activityContext);

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
