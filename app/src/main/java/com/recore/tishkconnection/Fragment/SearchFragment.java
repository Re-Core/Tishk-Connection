package com.recore.tishkconnection.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.recore.tishkconnection.Activity.Home;
import com.recore.tishkconnection.Activity.OtherProfileActivity;
import com.recore.tishkconnection.Adapter.CommentAdapter;
import com.recore.tishkconnection.Model.Post;
import com.recore.tishkconnection.Model.User;
import com.recore.tishkconnection.R;
import com.recore.tishkconnection.ViewHolder.UsersViewHolder;

import java.util.List;


public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView searchList;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<User> mUserList;
    private LinearLayoutManager mLayoutManager;
    private String input;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        searchList = (RecyclerView) v.findViewById(R.id.searchRv);
        mLayoutManager = new LinearLayoutManager(getContext());
        searchList.setLayoutManager(mLayoutManager);
        searchList.setHasFixedSize(true);

        Home.searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(getContext(), "TESSST", Toast.LENGTH_SHORT).show();
                input = query;
                onStart();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && !newText.isEmpty()) {

                } else {

                }
                return true;
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

    @Override
    public void onStart() {
        super.onStart();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(reference.orderByChild("username").startAt(input).endAt(input), User.class).build();

        FirebaseRecyclerAdapter<User, UsersViewHolder> adapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull final User model) {

//                holder.productTitletxt.setText(model.getPname());
//                holder.productDescriptiontxt.setText(model.getDescription());
//                holder.productPricetxt.setText(model.getPrice()+" IQD");
//                Picasso.get().load(model.getImage()).into(holder.productImg);

                holder.txtUserNameComment.setText(model.getUsername());
                holder.txtDescriptionComment.setText(model.getUserDepartment());
                holder.txtDateComment.setText("");

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getContext(), OtherProfileActivity.class);
                        i.putExtra("userId", model.getUserId());
                        Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
                        startActivity(i);
                    }
                });

            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items_comment, viewGroup, false);

                return new UsersViewHolder(v);
            }
        };

        searchList.setAdapter(adapter);
        adapter.startListening();

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}

