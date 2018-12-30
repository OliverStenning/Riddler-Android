package co.stenning.riddler.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;
import co.stenning.riddler.R;

public class HintDialog extends DialogFragment {

    public interface HintDialogListener {
        void onHintWatchAdClick(DialogFragment dialog);
    }

    private HintDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_hint, null);
        builder.setView(dialogView);

        Button watchAdButton = dialogView.findViewById(R.id.hintWatchAdButton);
        watchAdButton.setOnClickListener(view -> listener.onHintWatchAdClick(HintDialog.this));

        Button backButton = dialogView.findViewById(R.id.hintBackButton);
        backButton.setOnClickListener(view -> dismiss());

        return builder.create();
    }

    public void setHintDialogListener(HintDialog.HintDialogListener listener) {
        this.listener = listener;
    }

}