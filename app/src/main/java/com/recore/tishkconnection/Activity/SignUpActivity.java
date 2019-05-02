package com.recore.tishkconnection.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.recore.tishkconnection.R;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class SignUpActivity extends AppCompatActivity {

    private static int PReqCode = 1;
    private static int REQUESTIMAGECODE = 1;
    private LinearLayout topLayout;
    private LinearLayout downLayout;
    private Animation topAnim;
    private Animation downAnim;
    private CircleImageView registrationImage = null;
    private Uri pickedImageAddress = null;

    private Button signUpButton;

    private EditText nameEditText;
    private EditText mailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;

    private ProgressBar loadingPrograss;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sign_up);

        topLayout = findViewById(R.id.top_layout);
        downLayout = findViewById(R.id.down_layout);

        loadScreenAnim();


        mAuth = FirebaseAuth.getInstance();


        registrationImage = findViewById(R.id.profile_image);

        signUpButton = findViewById(R.id.sign_up_button);
        nameEditText = findViewById(R.id.name_edit_text);
        mailEditText = findViewById(R.id.mail_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.conferim_password_edit_text);
        loadingPrograss = findViewById(R.id.sign_up_prograss_bar);

        loadingPrograss.setVisibility(View.INVISIBLE);


        registrationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= 22) {

                    checkForUserPermission();

                } else {
                    openGallery();
                }

            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signUpButton.setVisibility(View.INVISIBLE);
                loadingPrograss.setVisibility(View.VISIBLE);

                final String name = nameEditText.getText().toString();
                final String email = mailEditText.getText().toString();
                final String password = passwordEditText.getText().toString();
                final String confirmPassword = confirmPasswordEditText.getText().toString();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || !password.equals(confirmPassword)) {

                    showMessage("Please Confirm all field");
                    signUpButton.setVisibility(View.VISIBLE);
                    loadingPrograss.setVisibility(View.INVISIBLE);


                } else {
                    createUserAccount(name, email, password);
                }


            }
        });


    }

    private void createUserAccount(final String name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    showMessage("Account created Successfully");

                    updateUserInfo(name, pickedImageAddress, mAuth.getCurrentUser());

                } else {
                    showMessage("Account creation failed" + task.getException().getMessage());
                    signUpButton.setVisibility(View.VISIBLE);
                    loadingPrograss.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    //update user name and image
    private void updateUserInfo(final String name, Uri pickedImageAddress, final FirebaseUser currentUser) {

        //upload user image to firebase
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("user_images");
        final StorageReference imageFilePath = mStorage.child(pickedImageAddress.getLastPathSegment());

        imageFilePath.putFile(pickedImageAddress).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // image uploaded succesfully
                // now we can get our image url

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        // uri contain user image url


                        UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();


                        currentUser.updateProfile(profleUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            // user info updated successfully
                                            showMessage("Register Complete");
                                            updateUi();
                                        }

                                    }
                                });

                    }
                });


            }
        });

    }

    private void updateUi() {
        Intent i = new Intent(getApplicationContext(), Home.class);
        startActivity(i);
        //todo uncomment it only after testing
        finish();
    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESTIMAGECODE);

    }

    private void checkForUserPermission() {

        if (ContextCompat.checkSelfPermission(SignUpActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(getApplicationContext(), "please accept the required permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }
        } else {
            openGallery();
        }

    }

    private void loadScreenAnim() {
        topAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.login_screen_anim_up);
        topLayout.setAnimation(topAnim);

        downAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.login_anim_down);
        downLayout.setAnimation(downAnim);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTIMAGECODE && data != null) {
            pickedImageAddress = data.getData();
            registrationImage.setImageURI(pickedImageAddress);

        }
    }
}
