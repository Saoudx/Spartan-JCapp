package com.example.zica.spartanjcapp;


import android.content.Intent;

import android.graphics.Bitmap;

import android.net.Uri;

import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;




import java.io.IOException;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


/**
 * This class handles all the functionalities of the registration page
 *
 * @author Saoud
 */
public class RegistrationActivity extends AppCompatActivity {

    private EditText userName, userPassword, userEmail, userAge, userPhone;
    private Button regButton;
    private TextView userLogin;
    private FirebaseAuth firebaseAuth;
    private ImageView userProfilePic;
    String email, name, age, password, phoneNumber;
    private FirebaseStorage firebaseStorage;
    private static int PICK_IMAGE = 123;
    Uri profileImagePath;
    private StorageReference storageReference;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null){
            profileImagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), profileImagePath);
                userProfilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * On creation of the activity it handles all the events on the registration page.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setupUIViews(); // Sets up the UI views.

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        storageReference = firebaseStorage.getReference();
        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");//application/* ex on sending data to type
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE);
            }
        });




        // Run this when the registration button is clicked
        regButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(validate()){
                    //update data to the data base
                    String user_email = userEmail.getText().toString().trim();
                    String user_password = userPassword.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //sendEmailVerification();
                                sendUserData();
                                firebaseAuth.signOut();
                                Toast.makeText(RegistrationActivity.this, "Successfully Registered, Upload complete!", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                            }else{
                                Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            }
        });
    }

    /**
     * Sets up the ui views for the instance variables
     */
    private void setupUIViews(){
        userName = (EditText)findViewById(R.id.etUserName);
        userPassword = (EditText)findViewById(R.id.etUsePassword);
        userEmail = (EditText)findViewById(R.id.etUserEmail);
        userPhone = (EditText)findViewById(R.id.etUserPhone);
        regButton = (Button)findViewById(R.id.btnRegister);
        userLogin = (TextView) findViewById(R.id.tvUserLogin);
        userAge = (EditText)findViewById(R.id.etAge);
        userProfilePic = (ImageView) findViewById(R.id.ivProfile);


    }

    /**
     * Function which validates that the user entered appropriate values for the name, password, and email.
     * (currently only tests if they have been inputted)
     *
     * @return a boolean value if it has passed or not.
     */
    private Boolean validate(){
        Boolean result = false;
         name = userName.getText().toString();
         password = userPassword.getText().toString();
         email = userEmail.getText().toString();
         age = userAge.getText().toString();
         phoneNumber = userPhone.getText().toString();

        if(name.isEmpty() || password.isEmpty() || email.isEmpty() || age.isEmpty() || profileImagePath == null || phoneNumber.isEmpty()){
            Toast.makeText(this, "Pleas enter all the details ", Toast.LENGTH_SHORT).show();

        }else{
            result = true;
        }
        return result;
    }

    /**
     * Sends an email verification to the user on successful registration of the application.
     */
    private void sendEmailVerification(){
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        sendUserData();
                        Toast.makeText(RegistrationActivity.this, "Successfully Registered, a Verification mail sent!", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                    }else{
                        Toast.makeText(RegistrationActivity.this, "FB is Down lol Verification mail hasn't been sent!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserData(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference(firebaseAuth.getUid());
        StorageReference profileImageReference = storageReference.child(firebaseAuth.getUid()).child("Images").child("Profile Pic");//User ID /Images/profile pic

        StorageReference imageReference = storageReference.child(firebaseAuth.getUid()).child("Images").child("QR Code Image");//User ID /Images/QR Code image



        UploadTask uploadTask = profileImageReference.putFile(profileImagePath);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistrationActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(RegistrationActivity.this, "Upload complete!", Toast.LENGTH_SHORT).show();

            }
        });
        UserProfile userProfile = new UserProfile(age ,email, name, phoneNumber);
        myRef.setValue(userProfile);
    }


}
