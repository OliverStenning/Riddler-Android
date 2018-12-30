package co.stenning.riddler.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;
import co.stenning.riddler.R;

public class ConsentDeclineDialog extends DialogFragment {

    public interface ConsentDeclineDialogListener {
        void onConsentDeclineAcceptClick(DialogFragment dialog);
    }

    ConsentDeclineDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_consent_decline, null);
        builder.setView(dialogView);

        //Remove underline from privacy policy link
        Button privacyPolicyButton = dialogView.findViewById(R.id.consentDeclinePrivacyPolicyButton);
        privacyPolicyButton.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy)));
            startActivity(browserIntent);
        });

        //Pass back accept click to activity
        Button acceptButton = dialogView.findViewById(R.id.consentDeclineAcceptButton);
        acceptButton.setOnClickListener(view -> listener.onConsentDeclineAcceptClick(ConsentDeclineDialog.this));

        //Pass back back click to activity
        Button backButton = dialogView.findViewById(R.id.consentDeclineBackButton);
        backButton.setOnClickListener(view -> dismiss());

        return builder.create();
    }

    public void setConsentDeclineDialogListener(ConsentDeclineDialogListener listener) {
        this.listener = listener;
    }

}
