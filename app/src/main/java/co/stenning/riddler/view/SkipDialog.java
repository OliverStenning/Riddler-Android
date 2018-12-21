package co.stenning.riddler.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;
import co.stenning.riddler.R;

public class SkipDialog extends DialogFragment {

    public interface SkipDialogListener {
        void onSkipSkipClick(DialogFragment dialog);
    }

    private SkipDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_skip, null);
        builder.setView(dialogView);

        Button skipButton = dialogView.findViewById(R.id.skipSkipButton);
        skipButton.setOnClickListener(view -> listener.onSkipSkipClick(SkipDialog.this));

        Button backButton = dialogView.findViewById(R.id.skipBackButton);
        backButton.setOnClickListener(view -> dismiss());

        return builder.create();
    }

    protected void setSkipDialogListener(SkipDialog.SkipDialogListener listener) {
        this.listener = listener;
    }

}