package com.recore.tishkconnection.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.recore.tishkconnection.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignInActivity extends AppCompatActivity {

    private CircleImageView createAccount;
    private EditText mail,password;
    private ProgressBar signInProgress;
    private Button signIn;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sign_in);

        mail =(EditText)findViewById(R.id.mail_edit_text_sign_in);
        password = (EditText)findViewById(R.id.password_edit_text_sign_in);
        createAccount =(CircleImageView)findViewById(R.id.create_account);
        signInProgress =(ProgressBar)findViewById(R.id.login_prograss);
        signIn =(Button)findViewById(R.id.sign_in_button);
        mAuth =FirebaseAuth.getInstance();

        signInProgress.setVisibility(View.INVISIBLE);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInProgress.setVisibility(View.VISIBLE);
                signIn.setVisibility(View.INVISIBLE);

                final String mEmail = mail.getText().toString();
                final String mPassword =password.getText().toString();

                if (mEmail.isEmpty()||mPassword.isEmpty()){
                    showMessage("please verify all field");
                    signIn.setVisibility(View.VISIBLE);
                    signInProgress.setVisibility(View.INVISIBLE);

                }else{

                    signInWithMailAndPassword(mEmail,mPassword);
                    signIn.setVisibility(View.INVISIBLE);
                    signInProgress.setVisibility(View.VISIBLE);

                }

            }
        });


        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(i);
            }
        });

    }

    private void signInWithMailAndPassword(String mEmail, String mPassword) {


        mAuth.signInWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    signInProgress.setVisibility(View.INVISIBLE);
                    signIn.setVisibility(View.VISIBLE);
                    updateUi();

                }else{
                    showMessage("login failed"+task.getException().getMessage());
                    signInProgress.setVisibility(View.INVISIBLE);
                    signIn.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void updateUi() {
        Intent i = new Intent(this,Home.class);
        startActivity(i);
        finish();
    }

    private void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
