package com.recore.tishkconnection.Activity;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.text.format.*;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.recore.tishkconnection.Model.Comment;
import com.recore.tishkconnection.R;

import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailActivity extends AppCompatActivity {

    ImageView imgPost;
    CircleImageView imgUserPost, imgCurrentUser;
    TextView txtPostTitle, txtPostDescription, txtPostDate;
    EditText editTextComment;
    Button btnAddComment;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;

    String postKey;

    boolean isDark = false;
    NestedScrollView nestedScrollViewRoot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        nestedScrollViewRoot = findViewById(R.id.nestedScrollView);

        imgPost = findViewById(R.id.post_detail_image);
        imgUserPost = findViewById(R.id.post_detail_user_img);
        imgCurrentUser = findViewById(R.id.post_detail_currentuser_img);

        txtPostTitle = findViewById(R.id.post_detail_title);
        txtPostDescription = findViewById(R.id.post_detail_desc);
        txtPostDate = findViewById(R.id.post_detail_date_name);

        editTextComment = findViewById(R.id.post_detail_comment_edit_text);

        btnAddComment = findViewById(R.id.post_detail_add_comment_button);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAddComment.setVisibility(View.INVISIBLE);
                DatabaseReference databaseReference = firebaseDatabase.getReference("Comments").child(postKey).push();
                String commentContent = editTextComment.getText().toString();
                String uid = firebaseUser.getUid();
                String uname = firebaseUser.getDisplayName();
                String uimg = firebaseUser.getPhotoUrl().toString();

                Comment comment = new Comment(uid, uimg, uname, commentContent);

                databaseReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        btnAddComment.setVisibility(View.VISIBLE);
                        showMessage("Comment Added");
                        editTextComment.setText("");


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Failed" + e.getMessage());

                    }
                });
            }
        });

        String postImage = getIntent().getExtras().getString("postImage");
        Glide.with(this).load(postImage).into(imgPost);

        String postTitle = getIntent().getExtras().getString("title");
        txtPostTitle.setText(postTitle);
        String postDesc = getIntent().getExtras().getString("desc");
        txtPostDescription.setText(postDesc);

        String postDate = timeStampToString(getIntent().getExtras().getLong("date"));
        txtPostDate.setText(postDate);

        String postUserImage = getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(postUserImage).into(imgUserPost);

        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imgCurrentUser);

        //getPostId So we can know later which post is commented
        postKey = getIntent().getExtras().getString("postKey");

        isDark = getThemeState();

        if (isDark) {
            nestedScrollViewRoot.setBackgroundColor(getResources().getColor(R.color.dark));
            txtPostTitle.setTextColor(getResources().getColor(R.color.white));
            txtPostDescription.setTextColor(getResources().getColor(R.color.white));
            txtPostDate.setTextColor(getResources().getColor(R.color.white));


        } else {
            nestedScrollViewRoot.setBackgroundColor(getResources().getColor(R.color.white));
        }

    }

    private boolean getThemeState() {
        SharedPreferences getTheme = getApplication().getSharedPreferences("myPref", MODE_PRIVATE);
        boolean isDark = getTheme.getBoolean("isDark", false);
        return isDark;
    }

    private void showMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private String timeStampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", calendar).toString();
        return date;

    }


}
