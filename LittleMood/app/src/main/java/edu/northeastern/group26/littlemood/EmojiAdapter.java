package edu.northeastern.group26.littlemood;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
        Drawable emojiDrawable = EmojiUtil.emojiToDrawable(mContext, entity.emoji,
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

}
