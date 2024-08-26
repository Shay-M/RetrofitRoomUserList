package com.easysale.retrofitroomuserlist.utils;

import android.content.Context;
import android.net.Uri;

import java.io.File;

public class AvatarHandler {

    public static Uri getAvatarUri(Context context, Uri selectedPhotoUri, String firstName, String lastName) {
        if (selectedPhotoUri != null) {
            return selectedPhotoUri;
        } else {
            File avatarFile = AvatarGenerator.createAvatarFile(context, firstName, lastName);
            return Uri.fromFile(avatarFile);
        }
    }
}
