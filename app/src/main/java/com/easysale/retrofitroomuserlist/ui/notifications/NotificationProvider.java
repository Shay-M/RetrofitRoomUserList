package com.easysale.retrofitroomuserlist.ui.notifications;


import android.view.View;

public interface NotificationProvider {
    void showNotification(String message);

    void setParentView(View viewById);
}
