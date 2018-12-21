package co.stenning.riddler.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import co.stenning.riddler.R;
import co.stenning.riddler.classes.PrefManager;

public class MenuActivity extends AppCompatActivity {

    private GoogleSignInClient signInClient;
    private GoogleSignInAccount signedInAccount;

    private static final int RC_SIGN_IN = 9001;
    private static final int RC_ACHIEVEMENT_UI = 9003;

    private static final String ACCOUNT_PARCEL = "account";

    private final boolean CONSENT_CHECK = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);

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
        final Context context = this;
        ConsentInformation consentInformation = ConsentInformation.getInstance(context);
        String[] publisherIds = {"pub-4605466962808569"};
        consentInformation.addTestDevice("8F85985E1F138565EDB3AD4BFCE7C52D");

        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {

                //check whether consent needs to be updated
                if (ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown() || CONSENT_CHECK) {
                    //create dialog to collect consent status if unknown
                    if (consentStatus == ConsentStatus.UNKNOWN || CONSENT_CHECK) {
                        ConsentDialog consentDialog = new ConsentDialog();
                        consentDialog.setCancelable(false); //stop dialog from closing when user touches outside dialog
                        consentDialog.show(getSupportFragmentManager(), "ConsentDialogFragment");
                    }
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String reason) {
                System.out.println(reason);
            }
        });

        //Google play games sign in
        signInClient = GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());
        startActivityForResult(signInClient.getSignInIntent(), RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                System.out.println("Signed in");
                // The signed in account is stored in the result.
                signedInAccount = result.getSignInAccount();
                GamesClient gamesClient = Games.getGamesClient(this, signedInAccount);
                gamesClient.setViewForPopups(findViewById(R.id.gps_popup_welcome));
            } else {
                System.out.println("Didn't sign in");
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                   message = "Sign in error.";
                }
                new AlertDialog.Builder(this).setMessage(message).setNeutralButton(android.R.string.ok, null).show();
            }
        }
    }

    private boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this) != null;
    }

    public void clickStart(View view) {
        Intent intent = new Intent(this, QuestionActivity.class);
        intent.putExtra(ACCOUNT_PARCEL, signedInAccount);
        startActivity(intent);
    }

    public void clickSettings(View view) {
        //TODO create settings menu
    }

    public void clickAchievement(View view) {
        if(isSignedIn())
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)).getAchievementsIntent().addOnSuccessListener(intent -> startActivityForResult(intent, RC_ACHIEVEMENT_UI));
    }

    public void clickLeaderboard(View view) {

    }

    public void clickSignOut(View view) {
        signInClient.signOut().addOnCompleteListener(this, task -> Toast.makeText(MenuActivity.this, "Signed out", Toast.LENGTH_LONG).show());
    }

}
