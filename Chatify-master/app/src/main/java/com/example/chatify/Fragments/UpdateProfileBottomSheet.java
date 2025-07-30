package com.example.chatify.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.chatify.databinding.FragmentUpdateProfileBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;


public class UpdateProfileBottomSheet extends BottomSheetDialogFragment {

    private FragmentUpdateProfileBottomSheetBinding binding;
    private DatabaseReference databaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUpdateProfileBottomSheetBinding.inflate(inflater, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        prefillUserProfileData(userId);

        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserProfile();
            }
        });
        return binding.getRoot();

    }

    private void prefillUserProfileData(String userId) {
        databaseReference.child(userId).get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String cnic = dataSnapshot.child("cnic").getValue(String.class);
                        String number = dataSnapshot.child("number").getValue(String.class);

                        if (name != null && cnic != null && number != null){
                            binding.editTextName.setText(name);
                            binding.editTextCNIC.setText(cnic);
                            binding.editTextNumber.setText(number);
                        }
                    } else {
                        Toast.makeText(requireContext(), "User data not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to fetch data: "
                        + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateUserProfile() {
        String name = binding.editTextName.getText().toString().trim();
        String cnic = binding.editTextCNIC.getText().toString().trim();
        String number = binding.editTextNumber.getText().toString().trim();

        if (!name.isEmpty() && !cnic.isEmpty() && !number.isEmpty()) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Map<String, Object> updates = new HashMap<>();
            updates.put("name", name);
            updates.put("cnic", cnic);
            updates.put("number", number);


            databaseReference.child(userId).updateChildren(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

}