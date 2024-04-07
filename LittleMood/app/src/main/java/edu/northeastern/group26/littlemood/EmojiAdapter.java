package edu.northeastern.group26.littlemood;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class EmojiAdapter extends  RecyclerView.Adapter<EmojiAdapter.SlideViewHolder> {
    private List<JournalEntry> mList = new ArrayList<>();
    private Context mContext;
    private  double mEmojiAllNum;

    public EmojiAdapter(Context context, List<JournalEntry> listData,double emojiAllNum ) {
        mList = listData;
        mContext = context;
        this.mEmojiAllNum=emojiAllNum;
    }

    @NonNull
    @Override
    public EmojiAdapter.SlideViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.item_emoji_statistic, viewGroup, false);
        return new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EmojiAdapter.SlideViewHolder slideViewHolder, int i) {
        JournalEntry entity = mList.get(i);
        Drawable emojiDrawable = emojiToDrawable(mContext, entity.emoji,
                100);
        slideViewHolder.mEmoji.setImageDrawable(emojiDrawable);
        int progress = (int) (new BigDecimal(entity.emailNum /mEmojiAllNum ).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()*100);
        slideViewHolder.mPbEmojiUsage.setProgress(progress);
        slideViewHolder.mTvEmojiStats.setText(entity.emailNum+"（"+progress+"%）");

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class SlideViewHolder extends RecyclerView.ViewHolder {

        private ImageView mEmoji;
        TextView mTvEmojiStats;

        private ProgressBar mPbEmojiUsage;

        private SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            mEmoji = itemView.findViewById(R.id.emoji);
            mPbEmojiUsage = itemView.findViewById(R.id.pbEmojiUsage);
            mTvEmojiStats = itemView.findViewById(R.id.tvEmojiStats);
        }
    }
    public Drawable emojiToDrawable(Context context, String emoji, int sizeInPixels) {
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
