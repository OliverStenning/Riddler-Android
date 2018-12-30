package co.stenning.riddler.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;
import co.stenning.riddler.R;

public class ReviewDialog extends DialogFragment {

    public interface Listener {
        void onRateClicked(DialogFragment dialog);
        void onNeverClicked(DialogFragment dialog);
    }

    private Listener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_review, null);
        builder.setView(dialogView);

        Button reviewButton = dialogView.findViewById(R.id.reviewButton);
        reviewButton.setOnClickListener(view -> listener.onRateClicked(ReviewDialog.this));

        Button neverButton = dialogView.findViewById(R.id.neverButton);
        neverButton.setOnClickListener(view -> listener.onNeverClicked(ReviewDialog.this));

        return builder.create();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

}