package com.easysale.retrofitroomuserlist.utils.image;

import android.net.Uri;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import lombok.Getter;

public class ImagePickerHelper {
    @Getter
    private Uri selectedPhotoUri;
    private final Fragment fragment;
    private final ActivityResultLauncher<String> pickPhotoLauncher;
    private final ImageLoadCallback callback;

    public ImagePickerHelper(Fragment fragment, ImageLoadCallback callback) {
        this.fragment = fragment;
        this.callback = callback;

        this.pickPhotoLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        selectedPhotoUri = result;
                        callback.onImageSelected(result);
                    }
                });
    }

    public void pickImage() {
        pickPhotoLauncher.launch("image/*");
    }

    public void loadImage(Uri imageUri, ImageView imageView) {
        Glide.with(fragment)
                .load(imageUri)
                .centerCrop()
                .error(android.R.drawable.ic_dialog_info)
                .into(imageView);
    }

    public interface ImageLoadCallback {
        void onImageSelected(Uri imageUri);
    }
}
