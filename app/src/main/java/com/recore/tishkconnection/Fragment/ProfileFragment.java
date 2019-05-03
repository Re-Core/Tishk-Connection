package com.recore.tishkconnection.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.recore.tishkconnection.Model.User;
import com.recore.tishkconnection.Prelevents.Prelevents;
import com.recore.tishkconnection.R;

import org.w3c.dom.Text;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userNodeDatabaseRefrence;
    private StorageReference storageProfilePictureRef;

    private TextView txtUserName, txtUserPhone, txtDepartment, txtUserMail;
    private CircleImageView imgUser;

    private Button btnEditProfile;

    private Dialog popupEditProfile;
    private CircleImageView imgPopUpCurrentUser;
    private TextView txtPopUpUserName;
    private EditText edtPopUpUserName, edtPopUpUserMail, edtPopUpUserPhone, edtPopUpUserDepartment;

    private boolean isDark1 = false;
    private String currentUserId;

    private User currentUser;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        isDark1 = getThemeState();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile_picture");
        firebaseDatabase = FirebaseDatabase.getInstance();
        userNodeDatabaseRefrence = firebaseDatabase.getReference("Users");

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();


        txtUserName = (TextView) v.findViewById(R.id.txtUserName);
        txtUserMail = (TextView) v.findViewById(R.id.txtUserMail);
        txtUserPhone = (TextView) v.findViewById(R.id.txtUserPhoneNumber);
        txtDepartment = (TextView) v.findViewById(R.id.txtUserDepartment);
        imgUser = (CircleImageView) v.findViewById(R.id.imgUser);


        Glide.with(this)
                .load(mCurrentUser.getPhotoUrl())
                .into(imgUser);

        btnEditProfile = (Button) v.findViewById(R.id.btnEditProfile);

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniPopUp();
            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;


    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void iniPopUp() {

        userNodeDatabaseRefrence.child(mCurrentUser.getUid());


        popupEditProfile = new Dialog(getContext());
        popupEditProfile.setContentView(R.layout.popup_edit_profile);
        popupEditProfile.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupEditProfile.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popupEditProfile.getWindow().getAttributes().gravity = Gravity.TOP;
        LinearLayout popUpRoot = (LinearLayout) popupEditProfile.findViewById(R.id.popUpRoot);

        imgPopUpCurrentUser = (CircleImageView) popupEditProfile.findViewById(R.id.imgPopUpUserImg);
        txtPopUpUserName = (TextView) popupEditProfile.findViewById(R.id.txtPopUpUserName);
        btnEditProfile = (Button) popupEditProfile.findViewById(R.id.btnConfirmChanges);

        TextView accText = (TextView) popupEditProfile.findViewById(R.id.accText);
        edtPopUpUserName = (EditText) popupEditProfile.findViewById(R.id.edtUserName);
        edtPopUpUserMail = (EditText) popupEditProfile.findViewById(R.id.edtUserMail);
        edtPopUpUserDepartment = (EditText) popupEditProfile.findViewById(R.id.edtUserDepartment);
        edtPopUpUserPhone = (EditText) popupEditProfile.findViewById(R.id.edtUserPhone);

        if (isDark1) {
            edtPopUpUserName.setBackground(getResources().getDrawable(R.drawable.edittext_button_rounded_style_dark));
            edtPopUpUserMail.setBackground(getResources().getDrawable(R.drawable.edittext_button_rounded_style_dark));
            edtPopUpUserDepartment.setBackground(getResources().getDrawable(R.drawable.edittext_button_rounded_style_dark));
            edtPopUpUserPhone.setBackground(getResources().getDrawable(R.drawable.edittext_button_rounded_style_dark));
            popUpRoot.setBackgroundColor(getResources().getColor(R.color.dark));
            txtPopUpUserName.setTextColor(getResources().getColor(R.color.white));
            accText.setTextColor(getResources().getColor(R.color.white));

            edtPopUpUserName.setHintTextColor(getResources().getColor(R.color.content_text_color));
            edtPopUpUserPhone.setHintTextColor(getResources().getColor(R.color.content_text_color));
            edtPopUpUserDepartment.setHintTextColor(getResources().getColor(R.color.content_text_color));
            edtPopUpUserMail.setHintTextColor(getResources().getColor(R.color.content_text_color));

            edtPopUpUserName.setTextColor(getResources().getColor(R.color.white));
            edtPopUpUserMail.setTextColor(getResources().getColor(R.color.white));
            edtPopUpUserDepartment.setTextColor(getResources().getColor(R.color.white));
            edtPopUpUserPhone.setTextColor(getResources().getColor(R.color.white));

        } else {
            edtPopUpUserName.setBackground(getResources().getDrawable(R.drawable.edittext_button_rounded_style));
            edtPopUpUserMail.setBackground(getResources().getDrawable(R.drawable.edittext_button_rounded_style));
            edtPopUpUserDepartment.setBackground(getResources().getDrawable(R.drawable.edittext_button_rounded_style));
            edtPopUpUserPhone.setBackground(getResources().getDrawable(R.drawable.edittext_button_rounded_style));
            txtPopUpUserName.setTextColor(getResources().getColor(R.color.text_title_color));

            edtPopUpUserName.setHintTextColor(getResources().getColor(R.color.content_text_color));
            edtPopUpUserPhone.setHintTextColor(getResources().getColor(R.color.content_text_color));
            edtPopUpUserDepartment.setHintTextColor(getResources().getColor(R.color.content_text_color));
            edtPopUpUserMail.setHintTextColor(getResources().getColor(R.color.content_text_color));

        }

        txtPopUpUserName.setText(currentUser.getUsername());
        edtPopUpUserName.setText(currentUser.getUsername());
        edtPopUpUserMail.setText(currentUser.getUserMail());
        edtPopUpUserPhone.setText(currentUser.getUserPhoneNumber());
        edtPopUpUserDepartment.setText(currentUser.getUserDepartment());

        Glide.with(this)
                .load(mCurrentUser.getPhotoUrl())
                .into(imgPopUpCurrentUser);

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateOnlyUserInfo();


                txtUserName.setText(currentUser.getUsername());
                txtUserMail.setText(currentUser.getUserMail());
                txtUserPhone.setText(currentUser.getUserPhoneNumber());
                txtDepartment.setText(currentUser.getUserDepartment());


                popupEditProfile.dismiss();
            }

        });


        popupEditProfile.show();

    }

    private void updateOnlyUserInfo() {


        HashMap<String, Object> userMap = new HashMap<>();

        if (!edtPopUpUserName.getText().toString().isEmpty() && !edtPopUpUserMail.getText().toString().isEmpty() &&
                !edtPopUpUserPhone.getText().toString().isEmpty() && !edtPopUpUserDepartment.getText().toString().isEmpty()) {

            userMap.put("username", edtPopUpUserName.getText().toString());
            userMap.put("userMail", edtPopUpUserMail.getText().toString());
            userMap.put("userPhoneNumber", edtPopUpUserPhone.getText().toString());
            userMap.put("userDepartment", edtPopUpUserDepartment.getText().toString());

            userNodeDatabaseRefrence.child(mCurrentUser.getUid()).updateChildren(userMap);
            showMessage("Profile info updated successfully");

        } else {
            showMessage("Confirm All field");
        }

    }

    private void showMessage(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }

    private boolean getThemeState() {
        SharedPreferences getTheme = getContext().getSharedPreferences("myPref", MODE_PRIVATE);
        boolean isDark = getTheme.getBoolean("isDark", false);
        return isDark;
    }

    @Override
    public void onStart() {
        super.onStart();
        currentUserId = mCurrentUser.getUid();

        userNodeDatabaseRefrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(currentUserId)) {
                    currentUser = dataSnapshot.child(currentUserId).getValue(User.class);
                    Prelevents.currentOnlineUser = currentUser;
                }
                txtUserName.setText(currentUser.getUsername());
                txtUserMail.setText(currentUser.getUserMail());
                txtUserPhone.setText(currentUser.getUserPhoneNumber());
                txtDepartment.setText(currentUser.getUserDepartment());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
