package com.dazone.crewemail.customviews;

/**
 * Created by THANHTUNG on 17/12/2015.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dazone.crewemail.R;

/**
 * Created by mgod on 5/27/15.
 *
 * Simple custom view example to show how to get selected events from the token
 * view. See ContactsCompletionView and contact_token.xml for usage
 */
public class TokenLayout extends LinearLayout {

    public TokenLayout(Context context) {
        super(context);
    }

    public TokenLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);

        TextView v = findViewById(R.id.txtMailCreatePersonNameOrMail);
        if (selected) {
            v.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.add_delete_attach_ic, 0);
        } else {
            v.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        }
    }
}
