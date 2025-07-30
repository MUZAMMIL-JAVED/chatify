package com.example.chatify.Fragments;

import static com.example.chatify.Activities.MainActivity.bottomNavigationView;
import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatify.Adapters.ChatListAdapter;
import com.example.chatify.R;
import com.example.chatify.Models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;


public class UserListFrag extends Fragment {

    RecyclerView recyclerView;
    ArrayList<UserModel> list;
    DatabaseReference databaseReference;
    ChatListAdapter chatListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.rvItems);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        list = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        chatListAdapter = new ChatListAdapter(getContext(), list, this::openChatFragment);
        recyclerView.setAdapter(chatListAdapter);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserModel user = dataSnapshot.getValue(UserModel.class);
                    if (user != null && !user.getId().equals(currentUserId)) {
                        // Add only users whose ID does not match the current user's ID
                        list.add(user);
                    } else if (user == null) {
                        Log.e("Firebase", "UserModel is null");
                    }
                }
                chatListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Database error: " + error.getMessage());
            }
        });

        return view;
    }

    private void openChatFragment(UserModel user) {
        bottomNavigationView.setVisibility(View.GONE);

        ChatFragment chatFragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString("userId", user.getId());
        args.putString("userName", user.getName());
        args.putString("userEmail", user.getEmail());
        chatFragment.setArguments(args);

        FragmentManager manager = ((FragmentActivity) getContext()).getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, chatFragment);
        transaction.commit();
    }

}