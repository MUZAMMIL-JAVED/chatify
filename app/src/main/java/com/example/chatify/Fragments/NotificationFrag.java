package com.example.chatify.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chatify.HelperClasses.AccessToken;
import com.example.chatify.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class NotificationFrag extends Fragment {

    private static final String FCM_URL = "https://fcm.googleapis.com/v1/projects/chatify-4927c/messages:send";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        MaterialButton btnSend = view.findViewById(R.id.btnSend);
        MaterialButton btnLanguage = view.findViewById(R.id.btnTranslate);

        loadLocale();

        btnLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguage();
            }
        });

        btnSend.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
            userRef.child("fcmToken").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String fcmToken = dataSnapshot.getValue(String.class);

                    if (fcmToken != null) {
                        new Thread(() -> {
                            AccessToken accessTokenProvider = new AccessToken();
                            String accessToken = accessTokenProvider.getAccessToken();
                            Log.d("Access Token", accessToken);

                            if (accessToken != null) {
                                sendNotification(accessToken, fcmToken);
                            } else {
                                requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(requireContext(), "Failed to get access token", Toast.LENGTH_SHORT).show();
                                });
                            }
                        }).start();
                    } else {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "FCM Token not found", Toast.LENGTH_SHORT).show();
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("FCM Error", "Failed to fetch FCM token: " + databaseError.getMessage());
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Failed to fetch FCM token", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });

        return view;
    }


    private void sendNotification(String accessToken, String fcmToken) {
        JSONObject message = new JSONObject();
        JSONObject notification = new JSONObject();
        try {
            notification.put("title", "Hello");
            notification.put("body", "This is a test notification");
            message.put("notification", notification);
            message.put("token", fcmToken);

            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put("message", message);
            } catch (Exception e) {
                Log.e("Error", "Failed to create request body", e);
                return;
            }

            RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, FCM_URL, requestBody,
                    response -> {
                        Log.d("FCM Response", "Notification Sent Successfully! Response: " + response.toString());
                    },
                    error -> {
                        Log.e("FCM Error", "Failed to send notification: " + error.getMessage(), error);
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + accessToken);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            requestQueue.add(jsonObjectRequest);

        }
        catch (Exception e) {
            Log.e("Error", "Unexpected error", e);
        }
    }

    private void changeLanguage() {
        final String [] languages = {"English", "Urdu", "Arabic"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose Language");
        builder.setSingleChoiceItems(languages, -1, (dialog, which) -> {
            if (which == 0) {
                setLocale("en");
                requireActivity().recreate();
            }
            else if (which == 1) {
                setLocale("ur");
                requireActivity().recreate();
            }
            else if (which == 2) {
                setLocale("ar");
                requireActivity().recreate();
            }
        });
        builder.create();
        builder.show();
    }

    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);
        requireContext().getResources().updateConfiguration(config, requireContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
        editor.putString("language", language);
        editor.apply();
    }

    private void loadLocale() {
        SharedPreferences preferences = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String language = preferences.getString("language", "");
        setLocale(language);
    }


}