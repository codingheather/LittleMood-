package edu.northeastern.group26.littlemood;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class EmojiUtil {
    private EmojiUtil() {}
    public static Drawable emojiToDrawable(Context context, String emoji, int sizeInPixels) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(sizeInPixels);
        paint.setTypeface(Typeface.DEFAULT); // Set the typeface you want to use
        paint.setTextAlign(Paint.Align.LEFT);

        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(emoji) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(emoji, 0, baseline, paint);

        return new BitmapDrawable(context.getResources(), image);
    }
}
