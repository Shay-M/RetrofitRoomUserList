package com.easysale.retrofitroomuserlist.utils.messages;


import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class SnackBarMessage implements MessageDisplayer {

    private static View rootView;

    public SnackBarMessage(View view) {
        rootView = view;
    }

    @Override
    public void showMessage(String message) {
        if (rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
        } else {
            throw new IllegalStateException("Root view not initialized. Call initialize() method first.");
        }
    }
}
