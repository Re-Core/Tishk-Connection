package com.recore.tishkconnection.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.recore.tishkconnection.Fragment.HomeFragment;
import com.recore.tishkconnection.Fragment.ProfileFragment;
import com.recore.tishkconnection.Fragment.SettingFragment;
import com.recore.tishkconnection.Model.Post;
import com.recore.tishkconnection.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private Dialog popAddPost;

    private CircleImageView popUpUserImage;
    private ImageView popUpAddPostButton , popUpPostImage;
    private EditText popUpTitleEditText, popUpDescriptionEditText;
    private ProgressBar popUpClickProgressBar;

    FloatingActionButton fab;
    boolean isDark = false;
    DrawerLayout rootLay;
    NavigationView navigationView;

    private static final int REQUESTIMAGECODE =2;
    private static final int PReqCode =2;
    private Uri pickedImageAddress =null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_home);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rootLay =(DrawerLayout) findViewById(R.id.drawer_layout);

        mAuth =FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        System.out.println(mCurrentUser.getPhotoUrl().toString());
        System.out.println(mCurrentUser.getDisplayName());

        //initializing popup

        iniPopUp();
        setUpPopUpImageClick();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              popAddPost.show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();



        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();


        isDark =getThemeState();
        if (isDark){
            rootLay.setBackgroundColor(getResources().getColor(R.color.dark_navigation_view));
            navigationView.setBackgroundColor(getResources().getColor(R.color.dark));
            navigationView.setItemTextColor(ColorStateList.valueOf(Color.WHITE));
            navigationView.setItemIconTintList(ColorStateList.valueOf(Color.WHITE));
        }else{
            rootLay.setBackgroundColor(getResources().getColor(R.color.white));
            navigationView.setBackgroundColor(getResources().getColor(R.color.white));


        }

    }

    private void setUpPopUpImageClick() {

        popUpPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkForUserPermission();

            }
        });

    }

    private void iniPopUp() {

        popAddPost =new Dialog(this);
        popAddPost.setContentView(R.layout.popup_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity= Gravity.TOP;

        popUpUserImage =(CircleImageView)popAddPost.findViewById(R.id.popup_user_image);
        popUpAddPostButton = (ImageView)popAddPost.findViewById(R.id.popup_add);
        popUpClickProgressBar=(ProgressBar)popAddPost.findViewById(R.id.popup_progressBar);
        popUpTitleEditText =(EditText)popAddPost.findViewById(R.id.pop_up_title_edittext);
        popUpDescriptionEditText =(EditText)popAddPost.findViewById(R.id.popup_description_edittext);
        popUpPostImage =(ImageView)popAddPost.findViewById(R.id.popup_post_img);


        //Glide.with(this)
        // .load(mCurrentUser.getPhotoUrl())
        // .into(popUpUserImage);

        Glide.with(Home.this)
                .load(mCurrentUser.getPhotoUrl())
                .into(popUpUserImage);

        popUpAddPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpClickProgressBar.setVisibility(View.VISIBLE);
                popUpAddPostButton.setVisibility(View.INVISIBLE);

                if (!popUpTitleEditText.getText().toString().isEmpty()&&
                        !popUpDescriptionEditText.getText().toString().isEmpty()&&
                        pickedImageAddress!=null){


                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("student_post");
                    final StorageReference imageFilePath =storageReference.child(pickedImageAddress.getLastPathSegment());
                    imageFilePath.putFile(pickedImageAddress).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String imageDownloadLink =uri.toString();

                                    Post post = new Post(popUpTitleEditText.getText().toString(),
                                            popUpDescriptionEditText.getText().toString(),
                                            imageDownloadLink,mCurrentUser.getUid(),
                                            mCurrentUser.getPhotoUrl().toString());



                                    addPost(post);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showToast(e.getMessage());
                                    popUpClickProgressBar.setVisibility(View.INVISIBLE);
                                    popUpAddPostButton.setVisibility(View.VISIBLE);

                                }
                            });

                        }
                    });



                }else{
                    showToast("please input Title,description and Image");
                    popUpClickProgressBar.setVisibility(View.INVISIBLE);
                    popUpAddPostButton.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    private void addPost(Post post) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference().child("posts").push();
        String key = myRef.getKey();

        post.setPostKey(key);

        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                showToast("Posted");
                popUpAddPostButton.setVisibility(View.VISIBLE);
                popUpClickProgressBar.setVisibility(View.INVISIBLE);

                popUpTitleEditText.setText("");
                popUpDescriptionEditText.setText("");

                popUpPostImage.setImageURI(null);

                popAddPost.dismiss();


            }
        });


    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new SettingFragment()).commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            fab.setEnabled(true);
            fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_create_black_24dp));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popAddPost.show();
                }
            });
            getSupportActionBar().setTitle("Home");

            getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();

        } else if (id == R.id.nav_profile) {
            getSupportActionBar().setTitle("Profile");
            fab.setEnabled(false);
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new ProfileFragment()).commit();


        } else if (id == R.id.nav_setting) {
            fab.setEnabled(true);
            fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_dark_mode_black_24dp));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isDark =!isDark;
                    if (isDark){
                        rootLay.setBackgroundColor(getResources().getColor(R.color.dark));
                        navigationView.setBackgroundColor(getResources().getColor(R.color.dark_navigation_view));
                        navigationView.setItemTextColor(ColorStateList.valueOf(Color.WHITE));
                        navigationView.setItemIconTintList(ColorStateList.valueOf(Color.WHITE));
                    }else{
                        rootLay.setBackgroundColor(getResources().getColor(R.color.white));
                        navigationView.setBackgroundColor(getResources().getColor(R.color.white));
                        navigationView.setItemTextColor(ColorStateList.valueOf(Color.BLACK));
                        navigationView.setItemIconTintList(ColorStateList.valueOf(Color.BLACK));


                    }
                    saveThemeState(isDark);

                }
            });
            getSupportActionBar().setTitle("Setting");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new SettingFragment()).commit();

        } else if (id == R.id.nav_sign_out) {
            //todo sign out
            FirebaseAuth.getInstance().signOut();
            Intent loginActivity = new Intent(getApplicationContext(),SignInActivity.class);
            startActivity(loginActivity);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void saveThemeState(boolean isDark) {

        SharedPreferences saveTheme = getApplicationContext().getSharedPreferences("myPref",MODE_PRIVATE);
        SharedPreferences.Editor editor =saveTheme.edit();
        editor.putBoolean("isDark",isDark);
        editor.commit();


    }

    private boolean getThemeState(){

        SharedPreferences getTheme = getApplication().getSharedPreferences("myPref",MODE_PRIVATE);
        boolean isDark = getTheme.getBoolean("isDark",false);
        return isDark;

    }

    public void updateNavHeader(){

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        TextView navUsername = (TextView)headerView.findViewById(R.id.nav_username);
        TextView navMail =(TextView)headerView.findViewById(R.id.nav_user_mail);
        CircleImageView navUserPhoto = (CircleImageView) headerView.findViewById(R.id.nav_user_photo);

        navUsername.setText(mCurrentUser.getDisplayName());
        navMail.setText(mCurrentUser.getEmail());

      //  Glide.with(this).load(mCurrentUser.getPhotoUrl()).into(navUserPhoto);

        Glide.with(this)
                .load(mCurrentUser.getPhotoUrl())
                .into(navUserPhoto);


    }

    private void checkForUserPermission() {

        if (ContextCompat.checkSelfPermission(Home.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(getApplicationContext(), "please accept the required permission", Toast.LENGTH_SHORT).show();
            }else{
                ActivityCompat.requestPermissions(Home.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PReqCode);
            }
        }else {
            openGallery();
        }

    }

    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESTIMAGECODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK && requestCode==REQUESTIMAGECODE&&data!=null){

            pickedImageAddress =data.getData();
            popUpPostImage.setImageURI(pickedImageAddress);


        }
    }
}
