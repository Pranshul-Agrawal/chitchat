package com.example.chitchat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseUser currentUser;
    private DatabaseReference rootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mtoolbar = findViewById(R.id.appbar);
        setSupportActionBar(mtoolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("ChitChat");
        ViewPager viewPager = findViewById(R.id.view_pager);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();

    }

    public void onStart() {
        super.onStart();
        if (currentUser == null) {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        } else {
            String currentUserId = currentUser.getUid();
            rootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!(snapshot.child("username").exists())) {
                        Toast.makeText(MainActivity.this, "Help us to identify you ", Toast.LENGTH_SHORT).show();
                        Intent settingActivityIntent = new Intent(MainActivity.this, SettingsActivity.class);
                        settingActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(settingActivityIntent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Oops! something went wrong", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.find_friends:
                return true;
            case R.id.settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "Logged Out Successfully", Toast.LENGTH_SHORT).show();
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                return true;
            case R.id.create_groups:
                final AlertDialog.Builder alertToCreateGroup = new AlertDialog.Builder(MainActivity.this);
                alertToCreateGroup.setTitle("Enter Group Name :");
                final EditText groupName = new EditText(MainActivity.this);
                groupName.setHint("e.g F.R.I.E.N.D.S");
                alertToCreateGroup.setView(groupName);
                alertToCreateGroup.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String g_name = groupName.getText().toString();
                        if (TextUtils.isEmpty(g_name)) {
                            Toast.makeText(MainActivity.this, "Group Name can't be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            // creating new group
                            rootRef.child("Groups").child(g_name).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Group Created", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                        }

                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                alertToCreateGroup.show();

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}