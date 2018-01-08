package com.em_projects.omdan.ui;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import com.em_projects.omdan.R;

/**
 * Created by eyalmuchtar on 12/18/17.
 */

public class ContactsAutoCompleteTextView extends android.support.v7.widget.AppCompatAutoCompleteTextView {

    public ContactsAutoCompleteTextView(Context context) {
        super(context);
    }

    public ContactsAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContactsAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    public void temp() {
//        performFiltering("",0);
        setText(R.string.root);
    }
}
