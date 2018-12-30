package co.stenning.riddler.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class Utilities {

    /* Firebase Events */
    private static final String NAVIGATION = "navigation";
    private static final String QUESTION = "question";
    private static final String HINT = "hint";
    private static final String GET_HINT = "get_hint";
    private static final String COMPLETION = "completion";

    /* Firebase Parameters */
    private static final String DESTINATION = "destination";

    private static final String QUESTION_NUM = "question-num";
    private static final String SKIP = "skip";
    private static final String HINTS = "hints";
    private static final String GUESSES = "guesses";
    private static final String INCORRECT_GUESSES = "incorrect-guesses";
    private static final String SCORE = "score";
    private static final String TIME = "time";

    private static final String TOTAL_SCORE = "total-score";
    private static final String TOTAL_TIME = "total-time";
    private static final String TOTAL_INCORRECT_GUESSES = "total-incorrect-guesses";
    private static final String TOTAL_HINTS = "total-hints";
    private static final String TOTAL_SKIPS = "total-skips";

    public static void openReviewPage(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    public static void navigateLog(FirebaseAnalytics firebaseAnalytics, String navigation) {
        Bundle bundle = new Bundle();
        bundle.putString(DESTINATION, navigation);
        firebaseAnalytics.logEvent(NAVIGATION, bundle);
    }

    public static void questionLog(FirebaseAnalytics firebaseAnalytics, int questionNum, boolean skip, int hints, int guesses, int score, int time) {
        Bundle bundle = new Bundle();
        bundle.putInt(QUESTION_NUM, questionNum);
        bundle.putBoolean(SKIP, skip);
        bundle.putInt(HINTS, hints);
        bundle.putInt(GUESSES, guesses);
        bundle.putInt(SCORE, score);
        bundle.putInt(TIME, time);
        firebaseAnalytics.logEvent(QUESTION, bundle);
    }

    public static void hintLog(FirebaseAnalytics firebaseAnalytics, int questionNum, int incorrectGuesses, int time) {
        Bundle bundle = new Bundle();
        bundle.putInt(QUESTION_NUM, questionNum);
        bundle.putInt(INCORRECT_GUESSES, incorrectGuesses);
        bundle.putInt(TIME, time);
        firebaseAnalytics.logEvent(HINT, bundle);
    }

    public static void getHintLog(FirebaseAnalytics firebaseAnalytics, int questionNum, int totalHints) {
        Bundle bundle = new Bundle();
        bundle.putInt(QUESTION_NUM, questionNum);
        bundle.putInt(TOTAL_HINTS, totalHints);
        firebaseAnalytics.logEvent(GET_HINT, bundle);
    }

    public static void completeLog(FirebaseAnalytics firebaseAnalytics, int totalScore, int totalTime, int totalIncorrectGuesses, int totalHints, int totalSkips) {
        Bundle bundle = new Bundle();
        bundle.putInt(TOTAL_SCORE, totalScore);
        bundle.putInt(TOTAL_TIME, totalTime);
        bundle.putInt(TOTAL_INCORRECT_GUESSES, totalIncorrectGuesses);
        bundle.putInt(TOTAL_HINTS, totalHints);
        bundle.putInt(TOTAL_SKIPS, totalSkips);
        firebaseAnalytics.logEvent(COMPLETION, bundle);
    }

}
