package com.easysale.retrofitroomuserlist.utils.avatar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class AvatarGenerator {

    private static final int AVATAR_SIZE = 200; // Size of the avatar

    public static File createAvatarFile(Context context, String firstName, String lastName) {
        // Generate initials
        String initials = getInitials(firstName, lastName);

        // Create a blank bitmap and canvas
        Bitmap bitmap = Bitmap.createBitmap(AVATAR_SIZE, AVATAR_SIZE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw background
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(getRandomColor()); // Background color
        canvas.drawRect(0, 0, AVATAR_SIZE, AVATAR_SIZE, backgroundPaint);

        // Draw initials
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE); // Text color
        textPaint.setTextSize(100); // Text size
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(initials, (float) AVATAR_SIZE / 2, (float) AVATAR_SIZE / 2 + textPaint.getTextSize() / 4, textPaint);

        // Save bitmap to file
        File avatarFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "avatar_" + System.currentTimeMillis() + ".png");
        try (FileOutputStream out = new FileOutputStream(avatarFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            Log.e("AvatarGenerator", "Error saving avatar image", e);
        }

        return avatarFile;
    }

    private static String getInitials(String firstName, String lastName) {
        StringBuilder initials = new StringBuilder();
        if (firstName != null && !firstName.isEmpty()) {
            initials.append(firstName.toUpperCase().charAt(0));
        }
        if (lastName != null && !lastName.isEmpty()) {
            initials.append(lastName.toUpperCase().charAt(0));
        }
        return initials.toString();
    }

    private static int getRandomColor() {
        Random random = new Random();
        return Color.argb(
                255,  // Alpha
                random.nextInt(128), // Red
                random.nextInt(128), // Green
                random.nextInt(128)  // Blue
        );
    }
}
