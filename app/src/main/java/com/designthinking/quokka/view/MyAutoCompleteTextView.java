package com.designthinking.quokka.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class MyAutoCompleteTextView extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {
    public MyAutoCompleteTextView(Context context) {
        super(context);
    }

    public MyAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int key_code, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
            this.clearFocus();

        return super.onKeyPreIme(key_code, event);
    }
}
