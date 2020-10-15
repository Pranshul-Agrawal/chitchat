
package com.example.chitchat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private EditText userName, userStatus;
    private String userId;
    private DatabaseReference rootRef;
    private final int PICK_IMAGE = 1;
    private StorageReference userProfileImageRef;
    private CircleImageView profileimg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile");
        userName = findViewById(R.id.user_name);
        userStatus = findViewById(R.id.user_status);
        rootRef = FirebaseDatabase.getInstance().getReference();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        retrieveUserInfo();
        userName.setVisibility(View.INVISIBLE);
        profileimg = findViewById(R.id.profile_image);
    }

    /*Onclick Button Method*/
    public void updateProfile(View view) {


        String username = userName.getText().toString();
        String userstatus = userStatus.getText().toString();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(userstatus)) {
            Toast.makeText(SettingsActivity.this, "Username or Status is invalid", Toast.LENGTH_SHORT).show();
        } else {

            HashMap<String, String> profileDetail = new HashMap<>();
            profileDetail.put("userId", userId);
            profileDetail.put("username", username);
            profileDetail.put("status", userstatus);
            rootRef.child("Users").child(userId).setValue(profileDetail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SettingsActivity.this, "Username and status set Successfully", Toast.LENGTH_SHORT).show();
                        Intent MainActivityIntent = new Intent(SettingsActivity.this, MainActivity.class);
                        startActivity(MainActivityIntent);

                    } else {
                        Toast.makeText(SettingsActivity.this, "Oops! Something went wrong.", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
    }

    /* user defined Helper Method */
    private void retrieveUserInfo() {
        rootRef.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("username")) && (snapshot.hasChild("profile"))) {
                    String retrieveUserName = snapshot.child("username").getValue().toString();
                    String retrieveUserStatus = snapshot.child("status").getValue().toString();
                    String retrieveUserImage = snapshot.child("profile").getValue().toString();
                    userName.setText(retrieveUserName);
                    userStatus.setText(retrieveUserStatus);
                    Picasso.get().load(retrieveUserImage).into(profileimg);
                    // manageChatRequest();

                } else if ((snapshot.exists()) && (snapshot.hasChild("username"))) {
                    String retrieveUserName = snapshot.child("username").getValue().toString();
                    String retrieveUserStatus = snapshot.child("status").getValue().toString();
                    userName.setText(retrieveUserName);
                    userStatus.setText(retrieveUserStatus);
                    // manageChatRequest();

                } else {
                    userName.setVisibility(View.VISIBLE);
                    Toast.makeText(SettingsActivity.this, "Set or Update Profile", Toast.LENGTH_SHORT).show();
                    //  manageChatRequest();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateProfilePic(View view) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            CropImage.activity(selectedImage).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri resultImg = result.getUri();

                final StorageReference filepath = userProfileImageRef.child(userId + ".jpg");
                filepath.putFile(resultImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                rootRef.child("Users").child(userId).child("profile").setValue(url);

                            }
                        });

                    }
                });


            }
        }

    }
}