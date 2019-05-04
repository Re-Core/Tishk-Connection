package com.recore.tishkconnection.Adapter;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.recore.tishkconnection.Activity.PostDetailActivity;
import com.recore.tishkconnection.Model.Post;
import com.recore.tishkconnection.R;

import java.util.List;

public class StaggeredPostAdapter extends RecyclerView.Adapter<StaggeredPostAdapter.ViewHolder> {

    Context mContext;
    List<Post> mPostList;

    public StaggeredPostAdapter(Context context, List<Post> postList) {
        mContext = context;
        mPostList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.staggered_row_post_items, viewGroup, false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.container.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_scale_animation));


        Glide.with(mContext).load(mPostList.get(i).getPictureId()).into(viewHolder.staggeredImgPost);

    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView staggeredImgPost;
        ConstraintLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            staggeredImgPost = (ImageView) itemView.findViewById(R.id.staggered_row_post_img);
            container = (ConstraintLayout) itemView.findViewById(R.id.staggeredContainer);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mContext, PostDetailActivity.class);

                    int position = getAdapterPosition();

                    i.putExtra("title", mPostList.get(position).getTitle());
                    i.putExtra("desc", mPostList.get(position).getDescription());
                    i.putExtra("postImage", mPostList.get(position).getPictureId());

                    i.putExtra("postKey", mPostList.get(position).getPostKey());

                    i.putExtra("userPhoto", mPostList.get(position).getUserPhoto());
                    long timeStamp = (long) mPostList.get(position).getTimeStamp();
                    i.putExtra("date", timeStamp);
                    mContext.startActivity(i);

                }
            });

        }
    }
}
