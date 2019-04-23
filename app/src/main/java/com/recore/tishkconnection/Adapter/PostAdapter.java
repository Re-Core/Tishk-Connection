package com.recore.tishkconnection.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.recore.tishkconnection.Activity.PostDetailActivity;
import com.recore.tishkconnection.Model.Post;
import com.recore.tishkconnection.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    List<Post> mData;
    private Context mContext;

    public PostAdapter(Context context, List<Post> data) {
        mContext = context;
        mData = data;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View postesRow = LayoutInflater.from(mContext).inflate(R.layout.row_post_item, viewGroup, false);

        return new ViewHolder(postesRow);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder viewHolder, int i) {

        viewHolder.container.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_scale_animation));

        viewHolder.postTitle.setText(mData.get(i).getTitle());

        Glide.with(mContext).load(mData.get(i).getPictureId()).into(viewHolder.postImage);

        Glide.with(mContext).load(mData.get(i).getUserPhoto()).into(viewHolder.postUserProfileImage);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout container;
        TextView postTitle;
        ImageView postImage;
        CircleImageView postUserProfileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.contaier1);

            postImage = itemView.findViewById(R.id.row_post_img);
            postUserProfileImage = itemView.findViewById(R.id.row_post_profile_img);
            postTitle = itemView.findViewById(R.id.row_post_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mContext, PostDetailActivity.class);

                    int position = getAdapterPosition();

                    i.putExtra("title", mData.get(position).getTitle());
                    i.putExtra("desc", mData.get(position).getDescription());
                    i.putExtra("postImage", mData.get(position).getPictureId());

                    i.putExtra("postKey", mData.get(position).getPostKey());

                    i.putExtra("userPhoto", mData.get(position).getUserPhoto());
                    long timeStamp = (long) mData.get(position).getTimeStamp();
                    i.putExtra("date", timeStamp);
                    mContext.startActivity(i);

                }
            });
        }
    }
}
