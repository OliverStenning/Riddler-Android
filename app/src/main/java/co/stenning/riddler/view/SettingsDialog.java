package co.stenning.riddler.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.fragment.app.DialogFragment;
import co.stenning.riddler.R;

public class SettingsDialog extends DialogFragment {

    public interface SettingsDialogListener {
        void onPlayGamesClick(DialogFragment dialog);
        void onPrivacySettingsClick(DialogFragment dialog);
    }

    private SettingsDialogListener listener;
    private boolean playSignedIn;
    private Button playGamesButton;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_settings, null);
        builder.setView(dialogView);

        ImageButton closeButton = dialogView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(view -> dismiss());

        playGamesButton = dialogView.findViewById(R.id.playGamesButton);
        updateButtons();
        playGamesButton.setOnClickListener(view -> listener.onPlayGamesClick(SettingsDialog.this));

        Button privacySettingsButton = dialogView.findViewById(R.id.privacySettingsButton);
        privacySettingsButton.setOnClickListener(view -> listener.onPrivacySettingsClick(SettingsDialog.this));

        Button bugReportButton = dialogView.findViewById(R.id.bugReportButton);
        bugReportButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL  , new String[] { getString(R.string.bug_email) });
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.bug_subject));
            startActivity(Intent.createChooser(intent, "Email via..."));
        });

        Button privacyPolicyButton = dialogView.findViewById(R.id.privacyPolicyButton);
        privacyPolicyButton.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy)));
            startActivity(browserIntent);
        });

        return builder.create();
    }

    protected void setSettingsDialogListener(SettingsDialog.SettingsDialogListener listener) {
        this.listener = listener;
    }

    public void setPlaySignedIn(boolean playSignedIn) {
        this.playSignedIn = playSignedIn;
    }

    public void updateButtons() {
        //update text of button depending on sign in state
        if (playSignedIn)
            playGamesButton.setText(R.string.play_sign_out_button);
        else
            playGamesButton.setText(R.string.play_sign_in_button);
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }
}