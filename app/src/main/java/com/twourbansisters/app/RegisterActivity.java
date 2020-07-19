package com.twourbansisters.app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.twourbansisters.app.includes.Utils;

public class RegisterActivity extends AppCompatActivity {
    Utils utils;
    MaterialButton login, create;
    private TextInputEditText email_text, password_text, conf_password_text;
    private TextInputLayout email_layout, password_layout, conf_password_layout;

    FirebaseAuth firebaseAuth;
    private static final String TAG = "RegisterActivity";
    
    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        utils = new Utils(this);
        firebaseAuth = FirebaseAuth.getInstance(); //need firebase authentication instance

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (!utils.haveNetwork()) {
            // custom dialog
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_conn);
            dialog.setTitle("Title...");
            dialog.setCancelable(false);

            // set the custom dialog components - text, image and button
            TextView textTitle = (TextView) dialog.findViewById(R.id.textTitle);
            TextView text = (TextView) dialog.findViewById(R.id.text);
            ImageView image = (ImageView) dialog.findViewById(R.id.image);

            MaterialButton dialogButtonTry = (MaterialButton) dialog.findViewById(R.id.dialogButtonTry);
            MaterialButton dialogButtonDismiss = (MaterialButton) dialog.findViewById(R.id.dialogButtonDismiss);

            textTitle.setText(getString(R.string.dialog_connection_title));
            text.setText(getString(R.string.dialog_connection_message));
            image.setImageResource(R.drawable.ic_baseline_wifi_off_24);

            // if button is clicked, close the custom dialog
            dialogButtonDismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            dialogButtonTry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    startActivity(getIntent());
                }
            });
            dialog.show();
        }

        login = (MaterialButton) findViewById(R.id.reg_login);
        create = (MaterialButton) findViewById(R.id.reg_create);

        email_text = (TextInputEditText) findViewById(R.id.email_name);
        password_text = (TextInputEditText) findViewById(R.id.password_name);
        conf_password_text = (TextInputEditText) findViewById(R.id.conf_password_name);

        email_layout = (TextInputLayout) findViewById(R.id.email_label);
        password_layout = (TextInputLayout) findViewById(R.id.password_label);
        conf_password_layout = (TextInputLayout) findViewById(R.id.conf_password_label);

        email_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if (!utils.validateEmail(String.valueOf(s)))
                    email_layout.setError(getResources().getString(R.string.valid_email_error));
                else
                    email_layout.setError(null);

            }
        });

        password_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if ((String.valueOf(s)).length() < 6)
                    password_layout.setError(getResources().getString(R.string.valid_short_password));
                else
                    password_layout.setError(null);
            }
        });

        conf_password_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {

                if (!String.valueOf(s).matches(password_text.getText().toString().trim()))
                    conf_password_layout.setError(getResources().getString(R.string.valid_password_match));
                else
                    conf_password_layout.setError(null);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_text.getText().toString().trim();
                String password = password_text.getText().toString().trim();
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "New user registration: " + task.isSuccessful());

                            if (!task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Registration failed. " + task.getException(), Toast.LENGTH_LONG).show();
                            } else {
                                RegisterActivity.this.startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                RegisterActivity.this.finish();
                            }
                        }
                    });
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout parent = (RelativeLayout) findViewById(R.id.loginParent);
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}