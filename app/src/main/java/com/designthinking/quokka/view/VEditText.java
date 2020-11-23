package com.designthinking.quokka.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.designthinking.quokka.R;

import androidx.appcompat.widget.AppCompatEditText;

public class VEditText extends AppCompatEditText {

    private LinearLayout parent;
    private TextView errorText;

    public VEditText(Context context) {
        super(context);
    }

    public VEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setErrorMessage(String msg){
        if(!(getParent() instanceof LinearLayout)) return;
        parent = (LinearLayout) getParent();

        if(errorText == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            errorText = (TextView) inflater.inflate(R.layout.error_text_view, parent, false);
        }

        errorText.setText(msg);

        int idx = 0;
        for(int i = 0; i < parent.getChildCount(); i++){
            if(parent.getChildAt(i) == this){
                idx = i;
                break;
            }
        }

        parent.addView(errorText, idx + 1);
    }

    public void removeErrorMessage(){
        if(errorText == null) return;
        parent.removeView(errorText);
    }
}
