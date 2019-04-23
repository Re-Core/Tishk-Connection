package com.recore.tishkconnection.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.recore.tishkconnection.Model.Comment;
import com.recore.tishkconnection.Model.Post;
import com.recore.tishkconnection.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    List<Comment> mComments;
    boolean isDark = false;
    private Context mContext;

    public CommentAdapter(List<Comment> comments, Context context, boolean isDark) {
        mComments = comments;
        mContext = context;
        this.isDark = isDark;
    }

    public CommentAdapter(List<Comment> comments, Context context) {
        mComments = comments;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View commentsRow = LayoutInflater.from(mContext).inflate(R.layout.items_comment, viewGroup, false);

        return new ViewHolder(commentsRow);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.container.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_scale_animation));

        viewHolder.txtUserNameComment.setText(mComments.get(i).getUname());

        viewHolder.txtDescriptionComment.setText(mComments.get(i).getContent());

        String p = mComments.get(i).getTimeStam().toString();
        long d = Long.parseLong(p);

        viewHolder.txtDateComment.setText(timeStampToString(d));

        Glide.with(mContext).load(mComments.get(i).getUimag()).into(viewHolder.imgUserComment);

    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }


    private String timeStampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("HH:mm\ndd/MM/yyyy ", calendar).toString();
        return date;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout container;
        RelativeLayout mRelativeLayout;
        CircleImageView imgUserComment;
        TextView txtUserNameComment;
        TextView txtDescriptionComment;
        TextView txtDateComment;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            container = (ConstraintLayout) itemView.findViewById(R.id.commentContainer);
            imgUserComment = (CircleImageView) itemView.findViewById(R.id.imgCommentUser);
            txtUserNameComment = (TextView) itemView.findViewById(R.id.txtCommentTitle);
            txtDescriptionComment = (TextView) itemView.findViewById(R.id.txtCommentDescription);
            txtDateComment = (TextView) itemView.findViewById(R.id.txCommentDate);
            mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);

            if (isDark) {
                setDarkTheme();
            }

        }

        private void setDarkTheme() {
            container.setBackground(mContext.getResources().getDrawable(R.drawable.card_bg_dark));
            mRelativeLayout.setBackground(mContext.getResources().getDrawable(R.drawable.card_bg_dark));
        }

    }


}
