package co.stenning.riddler.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.ads.consent.AdProvider;
import com.google.ads.consent.ConsentInformation;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.DialogFragment;
import co.stenning.riddler.R;
import co.stenning.riddler.util.URLSpanNoUnderline;

public class ConsentMoreInfoDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_consent_more_info, null);
        builder.setView(dialogView);

        //remove underline from link
        Button moreInfoButton = dialogView.findViewById(R.id.consentMoreInfoLinkButton);
        moreInfoButton.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy)));
            startActivity(browserIntent);
        });

        //make back button close dialog
        Button backButton = dialogView.findViewById(R.id.consentMoreInfoBackButton);
        backButton.setOnClickListener(view -> dismiss());


        List<AdProvider> adProviders = ConsentInformation.getInstance(getActivity()).getAdProviders();
        System.out.println("AdProvider Size: " + adProviders.size());
        ListView adProviderList = dialogView.findViewById(R.id.adProviderList);
        AdProviderAdapter adapter = new AdProviderAdapter(getActivity(), new ArrayList<>(adProviders));
        System.out.println("Adapter Size: " + adapter.getCount());
        adProviderList.setAdapter(adapter);
        adProviderList.setOnItemClickListener((adapterView, view, position, id) -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(adProviders.get(position).getPrivacyPolicyUrlString()));
            startActivity(browserIntent);
        });


        return builder.create();
    }

}
