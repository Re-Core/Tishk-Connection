package com.recore.tishkconnection.ViewHolder;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.recore.tishkconnection.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ConstraintLayout container;
    public RelativeLayout mRelativeLayout;
    public CircleImageView imgUserComment;
    public TextView txtUserNameComment;
    public TextView txtDescriptionComment;
    public TextView txtDateComment;

    public UsersViewHolder(@NonNull View itemView) {
        super(itemView);

        container = (ConstraintLayout) itemView.findViewById(R.id.commentContainer);
        imgUserComment = (CircleImageView) itemView.findViewById(R.id.imgCommentUser);
        txtUserNameComment = (TextView) itemView.findViewById(R.id.txtCommentTitle);
        txtDescriptionComment = (TextView) itemView.findViewById(R.id.txtCommentDescription);
        txtDateComment = (TextView) itemView.findViewById(R.id.txCommentDate);
        mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);

    }

    @Override
    public void onClick(View view) {

    }
}
