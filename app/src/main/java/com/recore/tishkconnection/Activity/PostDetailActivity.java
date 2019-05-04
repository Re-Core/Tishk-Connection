package com.recore.tishkconnection.Activity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.*;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.recore.tishkconnection.Adapter.CommentAdapter;
import com.recore.tishkconnection.Adapter.PostAdapter;
import com.recore.tishkconnection.Model.Comment;
import com.recore.tishkconnection.Model.Post;
import com.recore.tishkconnection.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class PostDetailActivity extends AppCompatActivity {

    ImageView imgPost;
    CircleImageView imgUserPost, imgCurrentUser;
    TextView txtPostTitle, txtPostDescription, txtPostDate;
    EditText editTextComment;
    Button btnAddComment;
    View seprator;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;

    String postKey;

    boolean isDark = false;
    NestedScrollView nestedScrollViewRoot;


    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    private DatabaseReference commentDatabaseReference;
    private List<Comment> commentList;

    private Dialog viewImgPopUp;
    private ImageView popUpFullImg;
    private String postImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setContentView(R.layout.activity_post_detail);


        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        //Typeface robotoBold = Typeface.createFromAsset(getAssets(),"Roboto-Bold.ttf");
        nestedScrollViewRoot = findViewById(R.id.nestedScrollView);

        LinearLayoutManager lin = new LinearLayoutManager(this);
        lin.setStackFromEnd(true);
        lin.setReverseLayout(true);
        commentRecyclerView = (RecyclerView) findViewById(R.id.commentRecycler);
        commentRecyclerView.setLayoutManager(lin);

        commentRecyclerView.setHasFixedSize(true);
        firebaseDatabase = FirebaseDatabase.getInstance();



        imgPost = findViewById(R.id.post_detail_image);
        imgUserPost = findViewById(R.id.post_detail_user_img);
        imgCurrentUser = findViewById(R.id.post_detail_currentuser_img);
        seprator = findViewById(R.id.view);

        txtPostTitle = findViewById(R.id.post_detail_title);
        txtPostDescription = findViewById(R.id.post_detail_desc);
        txtPostDate = findViewById(R.id.post_detail_date_name);

        editTextComment = findViewById(R.id.post_detail_comment_edit_text);

        btnAddComment = findViewById(R.id.post_detail_add_comment_button);

        //txtPostTitle.setTypeface(robotoBold);

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

        postImage = getIntent().getExtras().getString("postImage");
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

        commentDatabaseReference = firebaseDatabase.getReference().child("Comments").child(postKey);

        isDark = getThemeState();

        if (isDark) {
            nestedScrollViewRoot.setBackgroundColor(getResources().getColor(R.color.dark));
            txtPostTitle.setTextColor(getResources().getColor(R.color.white));
            txtPostDescription.setTextColor(getResources().getColor(R.color.white));
            txtPostDate.setTextColor(getResources().getColor(R.color.white));
            editTextComment.setBackground(getResources().getDrawable(R.drawable.edittext_button_rounded_style_dark));
            btnAddComment.setBackground(getResources().getDrawable(R.drawable.edittext_button_rounded_style_dark));

            seprator.setBackgroundColor(getResources().getColor(R.color.white));
            editTextComment.setHintTextColor(getResources().getColor(R.color.white));
            btnAddComment.setTextColor(getResources().getColor(R.color.white));

        } else {
            nestedScrollViewRoot.setBackgroundColor(getResources().getColor(R.color.white));
        }

        imgPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPopUp();
                viewImgPopUp.show();
            }
        });


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

    @Override
    protected void onStart() {
        super.onStart();

        commentDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                commentList = new ArrayList<>();

                for (DataSnapshot commentsShot : dataSnapshot.getChildren()) {

                    Comment comment = commentsShot.getValue(Comment.class);
                    commentList.add(comment);

                }
                commentAdapter = new CommentAdapter(commentList, PostDetailActivity.this, getThemeState());
                commentRecyclerView.setAdapter(commentAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void initPopUp() {

        viewImgPopUp = new Dialog(this);
        viewImgPopUp.setContentView(R.layout.popup_view_img);
        viewImgPopUp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        viewImgPopUp.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        viewImgPopUp.getWindow().getAttributes().gravity = Gravity.TOP;

        popUpFullImg = viewImgPopUp.findViewById(R.id.full_img);

        Glide.with(PostDetailActivity.this)
                .load(postImage)
                .into(popUpFullImg);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
