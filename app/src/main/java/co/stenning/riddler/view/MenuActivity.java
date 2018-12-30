package co.stenning.riddler.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.muddzdev.styleabletoast.StyleableToast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import co.stenning.riddler.R;
import co.stenning.riddler.util.PrefManager;
import co.stenning.riddler.dialog.ConsentDialog;
import co.stenning.riddler.dialog.SettingsDialog;
import co.stenning.riddler.util.Utilities;

public class MenuActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

    /* Google Play Games Services */
    private GoogleSignInClient signInClient;
    private GoogleSignInAccount signedInAccount;
    private GamesClient gamesClient;

    /* Firebase Analytics */
    private FirebaseAnalytics firebaseAnalytics;

    //arbitrary numbers to determine activity result origin
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_ACHIEVEMENT_UI = 9003;
    private static final int RC_LEADERBOARD_UI = 9004;
    public static final String ACCOUNT_PARCEL = "account";
    public static final String SETTINGS_REVIEWED = "reviewed";

    /* Consent */
    private final boolean CONSENT_CHECK = false;
    private PrefManager prefManager;

    /* Settings Dialog */
    private SettingsDialog settingsDialog;
    private boolean isSettingsDisplayed;
    private boolean settingsReviewed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);

        prefManager = new PrefManager(this);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //update version text
        TextView versionText = findViewById(R.id.versionText);
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionString = "v" + pInfo.versionName;
            versionText.setText(versionString);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //update consent status
        updateConsent(false);

        //create and update settings dialog with correct button states
        settingsDialog = new SettingsDialog();
        settingsDialog.setPlaySignedIn(isSignedIn());

    }

    /* Google Play Games Services Methods */
    private boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this) != null;
    }

    private void signIn() {
        //get the sign in client and configure for games
        signInClient = GoogleSignIn.getClient(this, new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());

        //try to sign user in silently
        signInSilently();

        //if silent sign in doesn't work display UI sign in
        if (!isSignedIn())
            startActivityForResult(signInClient.getSignInIntent(), RC_SIGN_IN);

    }

    private void signInSilently() {
        signInClient.silentSignIn().addOnCompleteListener(this,
                task -> {
                    if (task.isSuccessful()) {
                        signedInAccount = task.getResult();
                        settingsDialog.setPlaySignedIn(true);
                        updateSettingsIfDisplaying();
                    } else {
                        settingsDialog.setPlaySignedIn(false);
                        updateSettingsIfDisplaying();
                    }
                });
    }

    private boolean signInIfNotAlready() {
        if (!isSignedIn()) {
            signIn();
            return true;
        } else {
            return false;
        }
    }

    private void signOut() {
        signInClient.signOut().addOnCompleteListener(this,
                task -> {
                    StyleableToast.makeText(this, "Signed out", R.style.infoToast).show();
                    settingsDialog.setPlaySignedIn(false);
                    updateSettingsIfDisplaying();
                });
    }

    private void setPopUpView() {
        gamesClient = Games.getGamesClient(this, signedInAccount);
        gamesClient.setViewForPopups(findViewById(R.id.gps_popup_welcome));
    }

    //return from sign in activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                signedInAccount = result.getSignInAccount();
                settingsDialog.setPlaySignedIn(true);
                updateSettingsIfDisplaying();
                setPopUpView();
            } else {
                StyleableToast.makeText(this, getString(R.string.sign_in_failed), R.style.errorToast).show();
                settingsDialog.setPlaySignedIn(false);
                updateSettingsIfDisplaying();
            }
        }
    }

    /* Menu Activity Button Methods */
    public void clickStart(View view) {
        // log navigation event
        Utilities.navigateLog(firebaseAnalytics, "start");

        //switch to question activity
        Intent intent = new Intent(this, QuestionActivity.class);
        intent.putExtra(ACCOUNT_PARCEL, signedInAccount);
        intent.putExtra(SETTINGS_REVIEWED, settingsReviewed);
        startActivity(intent);
    }

    public void clickSettings(View view) {
        // log navigation event
        Utilities.navigateLog(firebaseAnalytics, "settings");

        // create settings dialog
        isSettingsDisplayed = true;
        settingsDialog.show(getSupportFragmentManager(), "SettingsDialogFragment");
        settingsDialog.setSettingsDialogListener(new SettingsDialog.Listener() {
            @Override
            public void onPlayGamesClicked(DialogFragment dialog) {
                if (isSignedIn())
                    signOut();
                else
                    signIn();
            }
            @Override
            public void onPrivacySettingsClicked(DialogFragment dialog) {
                settingsDialog.dismiss();
                updateConsent(true);
            }
            @Override
            public void onReviewClicked(DialogFragment dialog) {
                Utilities.openReviewPage(MenuActivity.this);
                settingsReviewed = true;
            }
        });
    }

    public void clickAchievement(View view) {
        // log navigation event
        Utilities.navigateLog(firebaseAnalytics, "achievements");

        //sign in if not already signed in
        if (!signInIfNotAlready()) {
            //if already signed in display achievements
            Games.getAchievementsClient(this,
                    GoogleSignIn.getLastSignedInAccount(this))
                    .getAchievementsIntent().addOnSuccessListener(
                        intent -> startActivityForResult(intent, RC_ACHIEVEMENT_UI)
            );
        }
    }

    public void clickLeaderboard(View view) {
        // log navigation event
        Utilities.navigateLog(firebaseAnalytics, "leaderboards");

        //sign in if not already signed in
        if (!signInIfNotAlready()) {
            //if already signed in display leaderboard
            Games.getLeaderboardsClient(this,
                    GoogleSignIn.getLastSignedInAccount(this))
                    .getLeaderboardIntent(getString(R.string.leaderboard_high_scores))
                    .addOnSuccessListener(
                            intent -> startActivityForResult(intent, RC_LEADERBOARD_UI)
                    );
        }
    }

    /* Updating Settings Buttons */
    private void updateSettingsIfDisplaying() {
        if (isSettingsDisplayed)
            settingsDialog.updateButtons();
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        //update whether settings is being displayed
        isSettingsDisplayed = false;
    }

    /* Consent Dialog */
    private void updateConsent(boolean forcedUpdate) {
        final Context context = this;
        ConsentInformation consentInformation = ConsentInformation.getInstance(context);
        String[] publisherIds = {"pub-4605466962808569"};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                //check whether consent needs to be updated
                if (ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown() || forcedUpdate || CONSENT_CHECK) {
                    //create dialog to collect consent status if unknown
                    if (consentStatus == ConsentStatus.UNKNOWN || forcedUpdate || CONSENT_CHECK) {
                        // log navigation event
                        Utilities.navigateLog(firebaseAnalytics, "consent");

                        ConsentDialog consentDialog = new ConsentDialog();
                        consentDialog.setCancelable(false); //stop dialog from closing when user touches outside dialog
                        consentDialog.show(getSupportFragmentManager(), "ConsentDialogFragment");
                        consentDialog.setConsentDialogListener(new ConsentDialog.ConsentDialogListener() {
                            @Override
                            public void onConsentAccept(DialogFragment dialog) {
                                ConsentInformation.getInstance(MenuActivity.this).setConsentStatus(ConsentStatus.PERSONALIZED);

                                //start google play sign in
                                signIn();
                            }

                            @Override
                            public void onConsentDecline(DialogFragment dialog) {
                                prefManager.setConsentPersonalised(false);

                                //start google play sign in
                                signIn();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String reason) {
                System.out.println(reason);
            }
        });
    }

}
