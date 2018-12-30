package co.stenning.riddler.view;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.ArrayList;

import co.stenning.riddler.R;
import co.stenning.riddler.data.Player;
import co.stenning.riddler.util.PrefManager;
import co.stenning.riddler.data.Question;
import co.stenning.riddler.data.QuestionModel;
import co.stenning.riddler.dialog.HintDialog;
import co.stenning.riddler.dialog.ReviewDialog;
import co.stenning.riddler.util.Utilities;

public class QuestionActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private static final int CORRECT_ACTIVITY = 1;

    /* Google Play Games Sign in */
    private GoogleSignInAccount signedInAccount;
    private GamesClient gamesClient;
    private AchievementsClient achievementsClient;
    public static final String SCORE_EXTRA = "score";

    /* Firebase Analytics */
    private FirebaseAnalytics firebaseAnalytics;

    /* Game Operation */
    private QuestionModel questionModel;

    /* User Interface */
    private ProgressBar questionProgress;
    private TextView questionProgressText;
    private TextView questionText;
    private Button skipButton;
    private Button hintButton;
    private EditText answerInput;
    private TextView hintText;
    private AdView questionBanner;

    /* Ads */
    private PrefManager prefManager;
    public static final String QUESTIONS_SINCE_AD = "questions-since-ad";
    public static final String AD_WATCHED = "ad-watched";
    private RewardedVideoAd rewardedVideoAd;
    private static String AD_TYPE;
    private static final String AD_HINT = "HINT";
    private static final String AD_SKIP = "SKIP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_question);

        /*// User Interface //*/
        //get references to UI
        questionProgress = findViewById(R.id.questionProgress);
        questionProgressText = findViewById(R.id.questionProgressText);
        questionText = findViewById(R.id.questionText);
        skipButton = findViewById(R.id.skipButton);
        hintButton = findViewById(R.id.hintButton);
        answerInput = findViewById(R.id.answerInput);
        hintText = findViewById(R.id.hintText);
        questionBanner = findViewById(R.id.questionBanner);

        questionBanner.setVisibility(View.INVISIBLE);

        //override keyboard enter method
        answerInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) clickEnter(null);
            return true;
        });

        //initialise firebase analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        /*// Game Operation //*/
        //setup view model
        questionModel = ViewModelProviders.of(this).get(QuestionModel.class);
        questionModel.loadPlayer(this);
        questionModel.loadQuestions(this);

        final Context context = this;
        final Observer<Player> playerObserver = player -> {
            if (player.getQuestionNumber() > questionModel.getQuestions().size() - 1) {
                questionModel.savePlayer(this);

                // log completion event
                int totalTime = (int) ((System.nanoTime() - player.getStartTime()) / 1000000000);
                Utilities.completeLog(firebaseAnalytics, player.getScore(), totalTime,
                        player.getTotalIncorrectGuesses(),
                        player.getTotalHintsUsed(),
                        player.getTotalSkips()
                );

                Intent intent = new Intent(this, CompletionActivity.class);
                intent.putExtra(SCORE_EXTRA, questionModel.getScore());
                startActivity(new Intent(context, CompletionActivity.class));
            } else {
                updateUI(player);
            }
        };
        questionModel.getPlayer().observe(this, playerObserver);

        //set question start time if first start
        questionModel.setFirstStartTime();

        /*// Ads and Consent //*/
        //initialise PrefManager for checking consent
        prefManager = new PrefManager(this);

        //initialise mobile ads
        MobileAds.initialize(this, getString(R.string.app_ad_id));

        //load banner ad
        AdRequest adRequest;
        if (!prefManager.hasConsentPersonalised()) {
            //configure ad request for non personalised ads
            Bundle extras = new Bundle();
            extras.putString("npa", "1");
            adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build();
        } else {
            //default ad request for personalised ads
            adRequest = new AdRequest.Builder().build();
        }
        questionBanner.loadAd(adRequest);

        //get rewarded video ad instance
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);

        //set listener for video ad to activity implemented listener
        rewardedVideoAd.setRewardedVideoAdListener(this);

        //load rewarded video ad
        loadVideoAd();

        // update if user reviewed app in settings
        if (getIntent().getBooleanExtra(MenuActivity.SETTINGS_REVIEWED, false))
            questionModel.setReviewed();

        /*// Play Games Services //*/
        //if signed in on google play setup achievements and leaderboards
        signedInAccount = getIntent().getParcelableExtra(MenuActivity.ACCOUNT_PARCEL);
        if (signedInAccount != null) {
            gamesClient = Games.getGamesClient(this, signedInAccount);
            gamesClient.setViewForPopups(findViewById(R.id.gps_popup));
            achievementsClient = Games.getAchievementsClient(this, signedInAccount);
        }
    }

    /* Question Activity Button Methods */
    public void clickBack(View view) {
        finish();
    }

    public void clickHint(View view) {
        hideKeyboard();
        Player player = questionModel.getPlayer().getValue();
        if (player.getHints() > 0) {
            if (player.getQuestionHintsUsed() < questionModel.getQuestions().get(player.getQuestionNumber()).getHint().length) {
                //log hint event
                Player tempPlayer = questionModel.getPlayer().getValue();
                int time = (int) ((System.nanoTime() - tempPlayer.getQuestionTimeStarted()) / 1000000000);
                Utilities.hintLog(firebaseAnalytics, tempPlayer.getQuestionNumber(),
                        tempPlayer.getQuestionIncorrectGuesses(), time);

                questionModel.useHint();
            } else {
                StyleableToast.makeText(this, getString(R.string.hints_expended), R.style.infoToast).show();
            }
        } else {
            HintDialog hintDialog = new HintDialog();
            hintDialog.show(getSupportFragmentManager(), "HintDialogFragment");

            hintDialog.setHintDialogListener(dialog -> {
                displayVideoAd(AD_HINT);
                hintDialog.dismiss();
            });
        }
    }

    public void clickSkip(View view) {
        displayVideoAd(AD_SKIP);
    }

    public void clickEnter(View view) {
        hideKeyboard();
        if (!questionModel.guess(answerInput.getText().toString())) {
            /* Answer is wrong */
            //shake EditText to show its wrong
            answerInput.setAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));

            //clear answer EditText
            answerInput.setText("");
        } else {
            /* Answer is correct */
            //clear answer EditText
            answerInput.setText("");

            //update score and submit to leaderboard
            int questionScore = updateScore();

            //log question event
            Player tempPlayer = questionModel.getPlayer().getValue();
            int time = (int) ((System.nanoTime() - tempPlayer.getQuestionTimeStarted()) / 1000000000);
            Utilities.questionLog(firebaseAnalytics, tempPlayer.getQuestionNumber(),
                    false,
                    tempPlayer.getQuestionHintsUsed(),
                    tempPlayer.getQuestionIncorrectGuesses() + 1, //add one for correct guess
                    questionScore,
                    time);

            //update other misc achievements
            updateOtherAchievements();

            //pass question score to Correct Activity
            startCorrectActivity(questionScore);
        }
    }

    private void startCorrectActivity(int questionScore) {
        Intent intent = new Intent(this, CorrectActivity.class);
        intent.putExtra(SCORE_EXTRA, questionScore);
        intent.putExtra(QUESTIONS_SINCE_AD, questionModel.getQuestionsSinceAd());
        startActivityForResult(intent, CORRECT_ACTIVITY);
        overridePendingTransition(0, 0);
    }

    /* Play Games Achievements & Leaderboards */
    private boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this) != null;
    }

    private void updateProgressAchievements() {
        //check if user is signed in
        if (isSignedIn()) {
            //get achievement id
            ArrayList<Integer> achievementIds = questionModel.checkProgressAchievements();

            //if there are achievements to unlock
            if (!achievementIds.isEmpty()) {
                for (int i = 0; i < achievementIds.size(); i++) {
                    achievementsClient.unlock(getString(achievementIds.get(i)));
                }
            }
        }
    }

    private void updateOtherAchievements() {
        //check if user is signed in
        if (isSignedIn()) {
            //get achievement id
            ArrayList<Integer> achievementIds = questionModel.checkOtherAchievements();

            //if there are achievements to unlock
            if (!achievementIds.isEmpty()) {
                for (int i = 0; i < achievementIds.size(); i++) {
                    achievementsClient.unlock(getString(achievementIds.get(i)));
                }
            }
        }
    }

    private int updateScore() {
        //get score for question
        int questionScore = questionModel.calculateScore();

        //update overall score
        questionModel.updateScore(questionScore);

        //submit overall score to Google Play leaderboard if signed in
        if (isSignedIn()) {
            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .submitScore(getString(R.string.leaderboard_high_scores), questionModel.getScore());
        }

        //return question score to display on UI
        return questionScore;
    }

    /* User Interface Methods */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(answerInput.getWindowToken(), 0);
    }

    private void updateUI(Player player) {

        Question question = questionModel.getQuestions().get(player.getQuestionNumber());

        questionProgress.setProgress(player.getQuestionNumber());
        questionProgress.setMax(questionModel.getQuestions().size());
        questionProgressText.setText((player.getQuestionNumber() + 1) + "/" + questionModel.getQuestions().size());

        questionText.setText(question.getQuestion());
        hintButton.setText(this.getString(R.string.hints_button) + "  " + player.getHints());

        if (player.getHints() == 0) {
            if (!rewardedVideoAd.isLoaded()) {
                hintButton.setEnabled(false);
            }
        }

        String hints = "";
        for (int i = 0; i < player.getQuestionHintsUsed(); i++) {
            hints += question.getHint()[i] + "\n" + "\n";
        }
        hintText.setText(hints);

    }

    /* Lifecycle Methods */
    @Override
    public void onPause() {
        questionModel.savePlayer(this);
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CORRECT_ACTIVITY) {
            //move player to next question
            questionModel.incrementPlayer();

            //check to see if any progress achievements unlocked
            updateProgressAchievements();

            //display review dialog if not shown before
            if (questionModel.shouldReview()) {
                ReviewDialog reviewDialog = new ReviewDialog();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(reviewDialog, "ReviewDialogFragment");
                fragmentTransaction.commitAllowingStateLoss();
                reviewDialog.setListener(new ReviewDialog.Listener() {
                    @Override
                    public void onRateClicked(DialogFragment dialog) {
                        // close dialog
                        dialog.dismiss();

                        // open store page
                        Utilities.openReviewPage(QuestionActivity.this);
                    }
                    @Override
                    public void onNeverClicked(DialogFragment dialog) {
                        // close dialog
                        dialog.dismiss();
                    }
                });
                // update player to store that review dialog shown
                questionModel.setReviewed();
            }

            //check to see if ad was displayed
            if (data != null) {
                if (data.getBooleanExtra(AD_WATCHED, false)) {
                    //set how many questions since last ad to 0
                    questionModel.setQuestionsSinceAd(0);
                }
            }
        }
    }

    /* Ad Methods */
    private void displayVideoAd(String adType) {
        if (rewardedVideoAd.isLoaded()) {
            AD_TYPE = adType;
            rewardedVideoAd.show();
        } else {
            StyleableToast.makeText(this, getString(R.string.ad_load_failed), R.style.errorToast).show();
        }
    }

    private void loadVideoAd() {
        rewardedVideoAd.loadAd(getString(R.string.rewarded_video_id), new AdRequest.Builder().build());
        disableButtons();
    }

    private void disableButtons() {
        skipButton.setEnabled(false);
        if (questionModel.getPlayer().getValue().getHints() == 0)
            hintButton.setEnabled(false);
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        skipButton.setEnabled(true);
        hintButton.setEnabled(true);
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadVideoAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Player tempPlayer;
        switch (AD_TYPE) {
            case (AD_HINT):
                //log get hint event
                tempPlayer = questionModel.getPlayer().getValue();
                Utilities.getHintLog(firebaseAnalytics, tempPlayer.getQuestionNumber(), tempPlayer.getTotalHintsUsed());

                //give the player hints
                if (isSignedIn())
                    achievementsClient.unlock(getString(R.string.achievement_more_help_please));
                questionModel.addHints(Player.HINT_REWARD_AMOUNT);
                break;
            case(AD_SKIP):
                //log skip event
                tempPlayer = questionModel.getPlayer().getValue();
                int time = (int) ((System.nanoTime() - tempPlayer.getQuestionTimeStarted()) / 1000000000);
                Utilities.questionLog(firebaseAnalytics, tempPlayer.getQuestionNumber(), true,
                        tempPlayer.getQuestionHintsUsed(),
                        tempPlayer.getQuestionIncorrectGuesses(), 0, time);

                //set question score to zero as question skipped
                if (isSignedIn())
                    achievementsClient.unlock(getString(R.string.achievement_trying_is_for_losers));
                questionModel.incrementTotalSkips();
                startCorrectActivity(0);
                break;

        }
        AD_TYPE = "";
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        loadVideoAd();
    }

    @Override
    public void onRewardedVideoCompleted() {

    }
}
