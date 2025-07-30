package com.example.chatify.Activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.chatify.Fragments.NotificationFrag;
import com.example.chatify.Fragments.ProfileFrag;
import com.example.chatify.R;
import com.example.chatify.Fragments.UserListFrag;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    public static BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.notification) {
                    loadFrag(new NotificationFrag());
                }

                else if (id == R.id.chat) {
                    loadFrag(new UserListFrag());
                }

                else if (id == R.id.profile) {
                    loadFrag(new ProfileFrag());
                }

                return true;
            }
        });

        if (savedInstanceState == null) {
            loadFrag(new NotificationFrag());
            bottomNavigationView.setSelectedItemId(R.id.notification);
        }
    }

    public void loadFrag(Fragment fragment) {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

}
