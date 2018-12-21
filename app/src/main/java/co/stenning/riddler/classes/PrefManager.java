package co.stenning.riddler.classes;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;

    //Shared preferences name
    private static final String PREF_NAME = "user-preferences";

    //Shared preferences keys
    private static final String CONSENT_PERSONALISED = "consent-personalised";
    private static final String SOUND = "sound";
    private static final String MUSIC = "music";

    public PrefManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void clearPreferences() {
        editor.clear();
        editor.commit();
    }

    public void setConsentPersonalised(boolean consentPersonalised) {
        editor.putBoolean(CONSENT_PERSONALISED, consentPersonalised);
        editor.commit();
    }
    public boolean hasConsentPersonalised() {
        return preferences.getBoolean(CONSENT_PERSONALISED, true);
    }

    public void setSound(boolean sound) {
        editor.putBoolean(SOUND, sound);
        editor.commit();
    }
    public boolean hasSound() {
        return preferences.getBoolean(SOUND, true);
    }

    public void setMusic(boolean music) {
        editor.putBoolean(MUSIC, music);
        editor.commit();
    }
    public boolean hasMusic() {
        return preferences.getBoolean(MUSIC, true);
    }

}
