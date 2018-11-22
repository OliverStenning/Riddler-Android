package co.roguestudios.riddler.util;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class ClickableSpanNoUnderline extends ClickableSpan {

    public interface ClickableSpanListener {
         void onSpanClicked(View view);
    }

    ClickableSpanListener listener;

    public ClickableSpanNoUnderline() {
        super();
    }

    public void updateDrawState(TextPaint drawState) {
        super.updateDrawState(drawState);
        drawState.setUnderlineText(false);
    }

    public void onClick(View view) {
        listener.onSpanClicked(view);
    }

    public void setClickableSpanListener(ClickableSpanListener listener) {
        this.listener = listener;
    }

}
