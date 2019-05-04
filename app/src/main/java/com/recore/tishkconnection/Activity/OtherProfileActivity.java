package com.recore.tishkconnection.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.recore.tishkconnection.Model.User;
import com.recore.tishkconnection.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class OtherProfileActivity extends AppCompatActivity {

    private String userID;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userNode;

    private TextView oUserName, oUserPhone, oUserDepartment, oUserMail;
    private CircleImageView oUserImg;
    private User oUser;

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
        setContentView(R.layout.activity_other_profile);

        firebaseDatabase = FirebaseDatabase.getInstance();
        userNode = firebaseDatabase.getReference("Users");

        userID = getIntent().getStringExtra("userId");
        userNode.child(userID);

        oUserName = (TextView) findViewById(R.id.OtxtUserName);
        oUserMail = (TextView) findViewById(R.id.OtxtUserMail);
        oUserDepartment = (TextView) findViewById(R.id.OtxtUserDepartment);
        oUserPhone = (TextView) findViewById(R.id.OtxtUserPhoneNumber);
        oUserImg = (CircleImageView) findViewById(R.id.OimgUser);


    }

    @Override
    protected void onStart() {
        super.onStart();

        userNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userID)) {
                    oUser = dataSnapshot.child(userID).getValue(User.class);
                }
                oUserName.setText(oUser.getUsername());
                oUserMail.setText(oUser.getUserMail());
                oUserPhone.setText(oUser.getUserPhoneNumber());
                oUserDepartment.setText(oUser.getUserDepartment());
                Picasso.get().load(oUser.getUserImage()).into(oUserImg);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
