package co.roguestudios.riddler.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.ads.consent.AdProvider;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;

import java.util.List;

import androidx.fragment.app.DialogFragment;
import co.roguestudios.riddler.R;

public class ConsentDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_consent, null);
        builder.setView(dialogView);

        List<AdProvider> adProviders = ConsentInformation.getInstance(getActivity()).getAdProviders();
        for (AdProvider adProvider : adProviders) {
            System.out.println(adProvider.getName());
            System.out.println(adProvider.getId());
            System.out.println(adProvider.getPrivacyPolicyUrlString());
        }


        // Update consent to PERSONALISED and close dialog
        Button acceptButton = dialogView.findViewById(R.id.consentAcceptButton);
        acceptButton.setOnClickListener(view -> {
            ConsentInformation.getInstance(getActivity()).setConsentStatus(ConsentStatus.PERSONALIZED);
            dismiss();
        });

        // Display decline dialog when user clicks decline
        Button declineButton = dialogView.findViewById(R.id.consentDeclineButton);
        declineButton.setOnClickListener(view ->  {
            ConsentDeclineDialog declineDialog = new ConsentDeclineDialog();
            declineDialog.show(getFragmentManager(), "ConsentDeclineDialogFragment");
        });

        TextView moreInfoLink = dialogView.findViewById(R.id.consentMoreInfoText);
        moreInfoLink.setOnClickListener(view -> {
            System.out.println("More Info Clicked!");
        });

        return builder.create();
    }

}
