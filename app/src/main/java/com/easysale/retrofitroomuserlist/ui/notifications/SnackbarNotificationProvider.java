package com.easysale.retrofitroomuserlist.ui.notifications;

import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import lombok.Setter;

public class SnackbarNotificationProvider implements NotificationProvider {
    private static SnackbarNotificationProvider instance;
    private Snackbar snackbar;
    @Setter
    private View parentView;

    private SnackbarNotificationProvider() {
        // Private constructor to prevent instantiation
    }

    public static synchronized SnackbarNotificationProvider getInstance() {
        if (instance == null) {
            instance = new SnackbarNotificationProvider();
        }
        return instance;
    }

    @Override
    public void showNotification(String message) {
        if (parentView != null) {
            if (snackbar != null) {
                snackbar.dismiss();
            }
            snackbar = Snackbar.make(parentView, message, Snackbar.LENGTH_LONG);
            snackbar.show();
        } else {
            throw new IllegalStateException("Parent view must be set before showing Snackbar.");
        }
    }


}
