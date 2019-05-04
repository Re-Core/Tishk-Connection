package com.recore.tishkconnection.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.SwitchCompat;

import android.view.Gravity;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.recore.tishkconnection.Fragment.HomeFragment;
import com.recore.tishkconnection.Fragment.ProfileFragment;
import com.recore.tishkconnection.Fragment.SearchFragment;
import com.recore.tishkconnection.Fragment.SettingFragment;
import com.recore.tishkconnection.Model.Post;
import com.recore.tishkconnection.R;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUESTIMAGECODE = 2;
    private static final int PReqCode = 2;
    FloatingActionButton fab;
    boolean isDark = false;
    private SwitchCompat drawerSwitch;
    DrawerLayout rootLay;
    NavigationView navigationView;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private Dialog popAddPost;
    private CircleImageView popUpUserImage;
    private ImageView popUpAddPostButton, popUpPostImage;
    private EditText popUpTitleEditText, popUpDescriptionEditText;
    private ProgressBar popUpClickProgressBar;
    private Uri pickedImageAddress = null;

    public static MaterialSearchView searchView;

    //private VisionServiceClient visionServiceClient = new VisionServiceRestClient("84fdb00b8aa045bf8109455883babcb1");

    private String safeContent = "Safe";

    private Bitmap pickedImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setContentView(R.layout.activity_home);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);

        rootLay = findViewById(R.id.drawer_layout);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();


        //initializing popup

        iniPopUp();
        setUpPopUpImageClick();

        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPost.show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();


        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();


        isDark = getThemeState();

        drawerSwitch = (SwitchCompat) navigationView.getMenu().findItem(R.id.switch_dark_mode).getActionView();
        drawerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isDark = !isDark;
                if (isChecked) {
                    rootLay.setBackgroundColor(getResources().getColor(R.color.dark));
                    navigationView.setBackgroundColor(getResources().getColor(R.color.dark_navigation_view));
                    navigationView.setItemTextColor(ColorStateList.valueOf(Color.WHITE));
                    navigationView.setItemIconTintList(ColorStateList.valueOf(Color.WHITE));
                } else {
                    rootLay.setBackgroundColor(getResources().getColor(R.color.white));
                    navigationView.setBackgroundColor(getResources().getColor(R.color.white));
                    navigationView.setItemTextColor(ColorStateList.valueOf(Color.BLACK));
                    navigationView.setItemIconTintList(ColorStateList.valueOf(Color.BLACK));
                }
                saveThemeState(isDark);
            }
        });


        if (isDark) {
            rootLay.setBackgroundColor(getResources().getColor(R.color.dark));
            navigationView.setBackgroundColor(getResources().getColor(R.color.dark_navigation_view));
            navigationView.setItemTextColor(ColorStateList.valueOf(Color.WHITE));
            navigationView.setItemIconTintList(ColorStateList.valueOf(Color.WHITE));
            drawerSwitch.setChecked(true);


        } else {
            rootLay.setBackgroundColor(getResources().getColor(R.color.white));
            navigationView.setBackgroundColor(getResources().getColor(R.color.white));
            drawerSwitch.setChecked(false);

        }


        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                getSupportActionBar().setTitle("Search");
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new SearchFragment()).commit();
                fab.hide();
            }

            @Override
            public void onSearchViewClosed() {

//                fab.show();
//                fab.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        popAddPost.show();
//                    }
//                });
//                getSupportActionBar().setTitle("Home");
//                getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();

            }
        });





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


        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.popup_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        popUpUserImage = popAddPost.findViewById(R.id.popup_user_image);
        popUpAddPostButton = popAddPost.findViewById(R.id.popup_add);
        popUpClickProgressBar = popAddPost.findViewById(R.id.popup_progressBar);
        popUpTitleEditText = popAddPost.findViewById(R.id.pop_up_title_edittext);
        popUpDescriptionEditText = popAddPost.findViewById(R.id.popup_description_edittext);
        popUpPostImage = popAddPost.findViewById(R.id.popup_post_img);


        //Glide.with(this)
        // .load(mCurrentUser.getPhotoUrl())
        // .into(popUpUserImage);

        Glide.with(Home.this)
                .load(mCurrentUser.getPhotoUrl())
                .into(popUpUserImage);

        /**
         * Microsoft AI
         * Safe Content Test
         *
         * */




        popUpAddPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popUpClickProgressBar.setVisibility(View.VISIBLE);
                popUpAddPostButton.setVisibility(View.INVISIBLE);


                /**
                 *
                 * Adult content filter
                 *
                 * */

//                ByteArrayOutputStream output = new ByteArrayOutputStream();
//                pickedImg.compress(Bitmap.CompressFormat.JPEG,100,output);
//                final ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
//
//
//                //inside post button
//
//                AsyncTask<InputStream,String,String>analyseImg = new AsyncTask<InputStream, String, String>() {
//
//                    ProgressDialog mDialog =new ProgressDialog(Home.this);
//
//                    @Override
//                    protected String doInBackground(InputStream... inputStreams) {
//
//                        try {
//
//                            publishProgress("Detecting...");
//                            String[]features={
//                                    "Adult"
//                            };
//
//                            String[]details={};
//                            AnalysisResult result = visionServiceClient.analyzeImage(inputStreams[0],features,details);
//                            //AnalysisResult v = this.client.describe(inputStream, 1);
//                            String strResult = new Gson().toJson(result);
//                            return strResult;
//                        }catch (Exception e){
//                            return null;
//                        }
//
//                    }
//
//                    @Override
//                    protected void onPreExecute() {
//                        mDialog.show();
//                    }
//
//                    @Override
//                    protected void onPostExecute(String s) {
//                        mDialog.dismiss();
//                        StringBuilder text = new StringBuilder();
//                        AnalysisResult result = new Gson().fromJson(s,AnalysisResult.class);
//                        try {
//
//                            text.append("File Type"+result.metadata.format+"Width:"+result.metadata.width+"Height:"+
//                                    result.metadata.height);
//
//                            Toast.makeText(Home.this, text, Toast.LENGTH_SHORT).show();
//
//                            if (result.adult.isAdultContent==true){
//                                safeContent="NotSafe";
//                            }else{
//                                safeContent="Safe";
//                            }
//
//                        }catch (Exception e){
//                            Log.d("msg",e.getMessage());
//                            Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//
//                        //Toast.makeText(Home.this, text, Toast.LENGTH_SHORT).show();
//
//                    }
//
//                    @Override
//                    protected void onProgressUpdate(String... values) {
//                        mDialog.setMessage(values[0]);
//                    }
//                };
//
//
//                analyseImg.execute(input);


                if (safeContent.equals("Safe")) {


                    if (!popUpTitleEditText.getText().toString().isEmpty() &&
                            !popUpDescriptionEditText.getText().toString().isEmpty() &&
                            pickedImageAddress != null) {


                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("student_post");
                        final StorageReference imageFilePath = storageReference.child(pickedImageAddress.getLastPathSegment());
                        imageFilePath.putFile(pickedImageAddress).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        String imageDownloadLink = uri.toString();

                                        Post post = new Post(popUpTitleEditText.getText().toString(),
                                                popUpDescriptionEditText.getText().toString(),
                                                imageDownloadLink, mCurrentUser.getUid(),
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


                    } else {
                        showToast("please input Title,description and Image");
                        popUpClickProgressBar.setVisibility(View.INVISIBLE);
                        popUpAddPostButton.setVisibility(View.VISIBLE);
                    }

                } else {
                    Toast.makeText(Home.this, "Not Safe Content", Toast.LENGTH_SHORT).show();
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

        int count = getSupportFragmentManager().getBackStackEntryCount();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else if (searchView.isSearchOpen()) {
            searchView.closeSearch();
            getSupportActionBar().setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
            fab.hide();

        } else if (count == 0) {
            super.onBackPressed();

        } else {
            getSupportFragmentManager().popBackStack();

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        searchView.setMenuItem(searchItem);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {


            fab.show();
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popAddPost.show();
                }
            });
            getSupportActionBar().setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();

        } else if (id == R.id.nav_profile) {

            getSupportActionBar().setTitle("Profile");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();
            fab.hide();

        } else if (id == R.id.nav_setting) {

            getSupportActionBar().setTitle("Setting");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new SettingFragment()).commit();
            fab.hide();

        } else if (id == R.id.nav_sign_out) {
            //todo sign out
            FirebaseAuth.getInstance().signOut();
            Intent loginActivity = new Intent(getApplicationContext(), SignInActivity.class);
            startActivity(loginActivity);
            finish();

        } else if (id == R.id.switch_dark_mode) {
            return false;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void saveThemeState(boolean isDark) {

        SharedPreferences saveTheme = getApplicationContext().getSharedPreferences("myPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = saveTheme.edit();
        editor.putBoolean("isDark", isDark);
        editor.commit();


    }

    private boolean getThemeState() {

        SharedPreferences getTheme = getApplication().getSharedPreferences("myPref", MODE_PRIVATE);
        boolean isDark = getTheme.getBoolean("isDark", false);
        return isDark;

    }

    public void updateNavHeader() {

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        TextView navUsername = headerView.findViewById(R.id.nav_username);
        TextView navMail = headerView.findViewById(R.id.nav_user_mail);
        CircleImageView navUserPhoto = headerView.findViewById(R.id.nav_user_photo);

        navUsername.setText(mCurrentUser.getDisplayName());
        navMail.setText(mCurrentUser.getEmail());

        //  Glide.with(this).load(mCurrentUser.getPhotoUrl()).into(navUserPhoto);

        Glide.with(this)
                .load(mCurrentUser.getPhotoUrl())
                .into(navUserPhoto);


    }

    private void checkForUserPermission() {

        if (ContextCompat.checkSelfPermission(Home.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(getApplicationContext(), "please accept the required permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }
        } else {
            openGallery();
        }

    }

    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESTIMAGECODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTIMAGECODE && data != null) {

            pickedImageAddress = data.getData();

            try {
                pickedImg = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pickedImageAddress);
            } catch (Exception ex) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }


            popUpPostImage.setImageURI(pickedImageAddress);


        }
    }


}
