package co.roguestudios.riddler.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import co.roguestudios.riddler.R;
import co.roguestudios.riddler.classes.PrefManager;

public class MenuActivity extends AppCompatActivity {

    PrefManager prefManager;

    private final boolean CONSENT_CHECK = true;

    private ImageButton soundButton;
    private ImageButton musicButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);

        prefManager = new PrefManager(this);

        //update sound and music enabled icons
        soundButton = findViewById(R.id.soundButton);
        musicButton = findViewById(R.id.musicButton);
        updateUI();

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

        long time = System.nanoTime();
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {

                System.out.println(consentStatus.toString());
                System.out.println((System.nanoTime() - time) / 1000000);

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


    }


    public void updateUI() {

        //update sound button image
        if (prefManager.hasSound()) soundButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_volume_on_36dp, null));
        else soundButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_volume_off_36dp, null));

        //update music button image
        if (prefManager.hasMusic()) musicButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_music_on_36dp, null));
        else musicButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_music_off_36dp, null));

    }

    public void clickStart(View view) { startActivity(new Intent(this, QuestionActivity.class)); }

    public void clickSettings(View view) {
        //TODO create settings menu
    }

    public void clickSound(View view) {
        prefManager.setSound(!prefManager.hasSound());
        updateUI();
    }

    public void clickMusic(View view) {
        prefManager.setMusic(!prefManager.hasMusic());
        updateUI();
    }

}
