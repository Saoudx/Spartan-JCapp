package com.example.zica.spartanjcapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * This class is used to start the main functionality of the
 * app which is the login.
 *
 * @author Saoud
 */
public class MainActivity extends AppCompatActivity {

    private EditText Name;
    private EditText Password;
    private TextView Info;
    private Button Login;
    private int counter = 5;
    private  TextView userRegistration;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private TextView forgotPassword;

    // Starts at the very beginning of app creation.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Name = (EditText)findViewById(R.id.etName);
        Password = (EditText) findViewById(R.id.etPassword);
        Info = (TextView) findViewById(R.id.tvInfo);
        Login = (Button) findViewById(R.id.btnLogin);
        userRegistration = (TextView) findViewById((R.id.tvRegister));
        forgotPassword = (TextView)findViewById(R.id.tvForgotPassword);

        Info.setText("Number of aattemts remaining: 5");

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        // if user is already logged in, send them to the second activity
        if(user != null){
            finish();
            startActivity(new Intent(MainActivity.this, SecondActivity.class));
        }

        //
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate(Name.getText().toString(), Password.getText().toString());
            }
        });

        // activates the user registration activity to register a new user
        userRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            }
        });

        // activates the forgot password activity to change your password
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PasswordActivity.class));
            }
        });
    }

    /**
     * Validates that the user has logged and provides a progress dialog while waiting.
     *
     * @param userName the username of the user.
     * @param userPassword the password of the user.
     */
    private void validate(String userName, String userPassword){

        progressDialog.setMessage(":) w8 for it");
        progressDialog.show();
       firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task){
               if(task.isSuccessful()){
                   progressDialog.dismiss();
                   //Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                   checkEmailVerification();

               }else{
                   Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                   counter--;
                   Info.setText("Number of attempts remaining: " + counter);
                   progressDialog.dismiss();
                   if(counter ==0){
                       Login.setEnabled(false);
                   }
               }
           }
       });
    }

    private void checkEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        Boolean emailflag = firebaseUser.isEmailVerified();

        if(emailflag){
            startActivity(new Intent(MainActivity.this, SecondActivity.class));
        }else{
            Toast.makeText(this, "Verify your email", Toast.LENGTH_SHORT).show();

            firebaseAuth.signOut();
        }
    }
}
