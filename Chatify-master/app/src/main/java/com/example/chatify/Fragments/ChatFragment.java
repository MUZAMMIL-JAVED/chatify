package com.example.chatify.Fragments;

import static com.example.chatify.Activities.MainActivity.bottomNavigationView;
import android.content.Context;
import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chatify.HelperClasses.AccessToken;
import com.example.chatify.Adapters.ChatAdapter;
import com.example.chatify.Models.ChatModel;
import com.example.chatify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class ChatFragment extends Fragment {

    TextView tvReceiverName;
    ImageView backImage, sendButton;
    RecyclerView rvUserChat;
    ChatAdapter messageAdapter;
    EditText messageInputEditText;
    List<ChatModel> mChat;
    String msg;
    private String userId;
    private String userName;
    private String userEmail;

    private final String FCM_URL = "https://fcm.googleapis.com/v1/projects/chatify-4927c/messages:send";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToUserList();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat2, container, false);

        if (getArguments() != null) {
            userId = getArguments().getString("userId");
            userName = getArguments().getString("userName");
            userEmail = getArguments().getString("userEmail");
        }

        tvReceiverName = view.findViewById(R.id.tvReceiverName);
        backImage = view.findViewById(R.id.backImage);
        rvUserChat = view.findViewById(R.id.rvUserChat);
        messageInputEditText = view.findViewById(R.id.messageInputEditText);
        sendButton = view.findViewById(R.id.sendButton);

        rvUserChat.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        rvUserChat.setLayoutManager(linearLayoutManager);

        if (userName != null) {
            tvReceiverName.setText(userName);
        } else {
            tvReceiverName.setText("Name");
        }

        String senderUUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        fetchReceiverUUID(userEmail, receiverUserId -> {
            if (receiverUserId != null) {
                readMessages(getContext(), senderUUID, receiverUserId);
            }
        });

        backImage.setOnClickListener(v -> loadHomeFragment());

        setupSendButtonVisibility();

        sendButton.setOnClickListener(v -> {
            msg = messageInputEditText.getText().toString().trim();
            if (msg.isEmpty()) {
                Toast.makeText(getContext(), "Please type a message", Toast.LENGTH_SHORT).show();
            } else {
                fetchReceiverUUID(userEmail, receiverUserId -> {
                    if (receiverUserId != null) {
                        sendMessage(senderUUID, receiverUserId, msg);
                    }
                });
            }
        });

        return view;
    }

    private void setupSendButtonVisibility() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    sendButton.setVisibility(View.GONE);
                } else {
                    sendButton.setVisibility(View.VISIBLE);
                }
            }
        };

        messageInputEditText.addTextChangedListener(textWatcher);
        sendButton.setVisibility(View.GONE);
    }

    private void loadHomeFragment() {
        bottomNavigationView.setVisibility(View.VISIBLE);
        UserListFrag userListFrag = new UserListFrag();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, userListFrag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void readMessages(Context context, String senderUUID, String receiverUserId) {
        mChat = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chat_rooms")
                .child(senderUUID + "_" + receiverUserId);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                ChatModel chat = dataSnapshot.getValue(ChatModel.class);
                if (chat != null) {
                    mChat.add(chat);
                    if (messageAdapter == null) {
                        messageAdapter = new ChatAdapter(context, mChat);
                        rvUserChat.setAdapter(messageAdapter);
                    } else {
                        messageAdapter.notifyDataSetChanged();
                    }
                    rvUserChat.scrollToPosition(mChat.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void sendMessage(String senderUUID, String receiverUserId, String msg) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        String messageID = reference.push().getKey();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        HashMap<String, Object> messageData = new HashMap<>();
        messageData.put("sender", senderUUID);
        messageData.put("receiver", receiverUserId);
        messageData.put("message", msg);
        messageData.put("timestamp", currentTime);

        reference.child("chat_rooms")
                .child(senderUUID + "_" + receiverUserId)
                .child(messageID)
                .setValue(messageData);

        reference.child("chat_rooms")
                .child(receiverUserId + "_" + senderUUID)
                .child(messageID)
                .setValue(messageData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        messageInputEditText.setText("");
                        sendChatNotification(receiverUserId, msg);
                    }
                });
    }

    private void sendChatNotification(String receiverUserId, String message) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(receiverUserId);
        userRef.child("fcmToken").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String fcmToken = dataSnapshot.getValue(String.class);

                if (fcmToken != null) {
                    String senderUUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference("Users").child(senderUUID);
                    senderRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot senderSnapshot) {
                            String senderName = senderSnapshot.getValue(String.class);
                            if (senderName != null) {
                                new Thread(() -> {
                                    AccessToken accessTokenProvider = new AccessToken();
                                    String accessToken = accessTokenProvider.getAccessToken();

                                    if (accessToken != null) {
                                        sendNotification(accessToken, fcmToken, senderName, message);
                                    } else {
                                        requireActivity().runOnUiThread(() ->
                                                Toast.makeText(requireContext(), "Failed to get access token", Toast.LENGTH_SHORT).show()
                                        );
                                    }
                                }).start();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError senderError) {
                            Toast.makeText(requireContext(), "Error fetching sender name", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "FCM Token not found for receiver", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Error fetching FCM token", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void sendNotification(String accessToken, String fcmToken, String senderName, String message) {
        try {
            JSONObject notification = new JSONObject();
            notification.put("title", senderName);
            notification.put("body", message);

            JSONObject messageBody = new JSONObject();
            messageBody.put("notification", notification);
            messageBody.put("token", fcmToken);

            JSONObject requestBody = new JSONObject();
            requestBody.put("message", messageBody);

            RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, FCM_URL, requestBody,
                    response -> {},
                    error -> {}
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + accessToken);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void fetchReceiverUUID(String email, ReceiverIdCallback receiverIdCallback) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String receiverId = snapshot.getKey();
                        receiverIdCallback.onReceiverIdFetched(receiverId);
                        return;
                    }
                } else {
                    receiverIdCallback.onReceiverIdFetched(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                receiverIdCallback.onReceiverIdFetched(null);
            }
        });
    }

    interface ReceiverIdCallback {
        void onReceiverIdFetched(String receiverId);
    }

    private void navigateToUserList() {
        bottomNavigationView.setVisibility(View.VISIBLE);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, new UserListFrag());
        transaction.commit();
    }

}
