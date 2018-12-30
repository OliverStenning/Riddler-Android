package co.stenning.riddler.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;

import androidx.fragment.app.DialogFragment;
import co.stenning.riddler.R;

public class ConsentDialog extends DialogFragment {

    public interface ConsentDialogListener {
        void onConsentAccept(DialogFragment dialog);
        void onConsentDecline(DialogFragment dialog);
    }

    private ConsentDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_consent, null);
        builder.setView(dialogView);

        //update consent to PERSONALISED and close dialog
        Button acceptButton = dialogView.findViewById(R.id.consentAcceptButton);
        acceptButton.setOnClickListener(view -> {
            listener.onConsentAccept(ConsentDialog.this);
            dismiss();
        });

        //display decline dialog when user clicks decline
        Button declineButton = dialogView.findViewById(R.id.consentDeclineButton);
        declineButton.setOnClickListener(view ->  {
            ConsentDeclineDialog declineDialog = new ConsentDeclineDialog();
            declineDialog.setCancelable(false); //stop dialog from closing when user touches outside dialog
            declineDialog.show(getFragmentManager(), "ConsentDeclineDialogFragment");
            declineDialog.setConsentDeclineDialogListener(dialog ->  {
                    declineDialog.dismiss();
                    dismiss();
                    ConsentInformation.getInstance(getActivity()).setConsentStatus(ConsentStatus.NON_PERSONALIZED);
                    listener.onConsentDecline(ConsentDialog.this);
                });
        });

        //display more info dialog when user clicks link
        TextView moreInfoLink = dialogView.findViewById(R.id.consentMoreInfoText);
        moreInfoLink.setOnClickListener(view -> {
            ConsentMoreInfoDialog moreInfoDialog = new ConsentMoreInfoDialog();
            moreInfoDialog.setCancelable(false); //stop dialog from closing when user touches outside dialog
            moreInfoDialog.show(getFragmentManager(), "ConsentMoreInfoDialogFragment");
        });

        return builder.create();
    }

    public void setConsentDialogListener(ConsentDialogListener listener) {
        this.listener = listener;
    }
}
