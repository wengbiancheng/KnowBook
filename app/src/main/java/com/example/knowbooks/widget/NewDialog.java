package com.example.knowbooks.widget;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by qq on 2016/5/12.
 */
public class NewDialog extends AlertDialog {
    protected NewDialog(Context context) {
        super(context);
    }

    protected NewDialog(Context context, int theme) {
        super(context, theme);
    }

    protected NewDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


}
