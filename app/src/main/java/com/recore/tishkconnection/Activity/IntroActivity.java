package com.recore.tishkconnection.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.recore.tishkconnection.Adapter.IntroviewPagerAdapter;
import com.recore.tishkconnection.R;
import com.recore.tishkconnection.Model.IntroScreen;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class IntroActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private IntroviewPagerAdapter mPagerAdapter;
    private TabLayout introTabIndicator;
    private Button nextIntroButton;
    private Animation getStartedAnimation;

    private Button getStartedButton;
    private int position;

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

        if (restorePrefData()) {

            Intent i = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(i);
            finish();

        }

        setContentView(R.layout.activity_intro);


        final List<IntroScreen> list = new ArrayList<>();
        list.add(new IntroScreen("Connect", "Connect with your university see the latest news on the go", R.drawable.img1));
        list.add(new IntroScreen("Find colleague", "Colleague to work with, do your research with students from other fields", R.drawable.img2));
        list.add(new IntroScreen("Share your moment", "Share your day with the whole university, share your projects, your visions, or just your moments", R.drawable.img3));

        introTabIndicator = findViewById(R.id.intro_tab_indicator);
        nextIntroButton = findViewById(R.id.next_intro_button);
        getStartedButton = findViewById(R.id.get_started_button);
        getStartedAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);

        mAuth = FirebaseAuth.getInstance();

        mViewPager = findViewById(R.id.intro_pager);
        mPagerAdapter = new IntroviewPagerAdapter(this, list);
        mViewPager.setAdapter(mPagerAdapter);

        introTabIndicator.setupWithViewPager(mViewPager);
        nextIntroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                position = mViewPager.getCurrentItem();
                if (position < list.size()) {
                    position++;
                    mViewPager.setCurrentItem(position);
                }
                if (position == list.size()) {
                    loadLastScreen();
                }

            }
        });

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(i);
                savePref();
                finish();
            }
        });

        introTabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (position == list.size() - 1) {
                    loadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private boolean restorePrefData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefData", MODE_PRIVATE);
        Boolean isIntroActivityOpendBefore = pref.getBoolean("isIntroOpenid", false);
        return isIntroActivityOpendBefore;

    }

    private void savePref() {

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("myPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isIntroOpenid", true);
        editor.commit();


    }

    private void loadLastScreen() {

        nextIntroButton.setVisibility(View.INVISIBLE);
        introTabIndicator.setVisibility(View.INVISIBLE);
        getStartedButton.setVisibility(View.VISIBLE);
        getStartedButton.setAnimation(getStartedAnimation);


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Intent i = new Intent(getApplicationContext(), Home.class);
            startActivity(i);
            finish();
        }
    }
}
