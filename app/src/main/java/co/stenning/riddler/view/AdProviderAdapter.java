package co.stenning.riddler.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.ads.consent.AdProvider;

import java.util.ArrayList;

import co.stenning.riddler.R;

public class AdProviderAdapter extends ArrayAdapter<AdProvider> {

    private Activity context;

    protected AdProviderAdapter(Activity context, ArrayList<AdProvider> adProviders) {
        super(context, 0, adProviders);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AdProvider adProvider = getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_ad_provider, null);
        }

        TextView adProviderText = convertView.findViewById(R.id.adProviderText);
        adProviderText.setText(getItem(position).getName());

        return convertView;
    }

}
