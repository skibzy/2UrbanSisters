package com.twourbansisters.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.twourbansisters.app.includes.Utils;

public class LoginActivity extends AppCompatActivity {

    Utils utils;

    //  MAIN BUTTONS
    MaterialButton login, reset;

    //  SUPPORTING BUTTONS
    MaterialButton login_create, login_reset, reset_create, reset_login;

    private View loginView, resetView;
    MaterialAlertDialogBuilder progress;
    AlertDialog.Builder alertDialog;

    FirebaseAuth firebaseAuth;

    private TextInputLayout email_layout, password_layout, forgot_layout;
    private TextInputEditText email_text, password_text, forgot_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        utils = new Utils(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (!utils.haveNetwork()) {
            // custom dialog
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_conn);
            dialog.setCancelable(false);

            // set the custom dialog components - text, image and button
            TextView textTitle = (TextView) dialog.findViewById(R.id.textTitle);
            textTitle.setText("Internet Connection Required");
            TextView text = (TextView) dialog.findViewById(R.id.text);
            text.setText("You need an active internet connection to proceed.");
            ImageView image = (ImageView) dialog.findViewById(R.id.image);
            image.setImageResource(R.drawable.ic_baseline_wifi_off_24);

            MaterialButton dialogButtonTry = (MaterialButton) dialog.findViewById(R.id.dialogButtonTry);
            MaterialButton dialogButtonDismiss = (MaterialButton) dialog.findViewById(R.id.dialogButtonDismiss);
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

        if (firebaseAuth.getCurrentUser() != null) {
            // User is logged in
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        login = (MaterialButton) findViewById(R.id.log_in);
        reset = (MaterialButton) findViewById(R.id.reset_pw);

        login_create = (MaterialButton) findViewById(R.id.login_create);
        login_reset = (MaterialButton) findViewById(R.id.login_reset);
        reset_create = (MaterialButton) findViewById(R.id.reset_create);
        reset_login = (MaterialButton) findViewById(R.id.reset_login);

        loginView = findViewById(R.id.login_layout);
        resetView = findViewById(R.id.reset_pw_layout);

        email_text = (TextInputEditText) findViewById(R.id.email_name);
        password_text = (TextInputEditText) findViewById(R.id.password_name);
        forgot_text = (TextInputEditText) findViewById(R.id.forgot_name);

        email_layout = (TextInputLayout) findViewById(R.id.email_label);
        password_layout = (TextInputLayout) findViewById(R.id.password_label);
        forgot_layout = (TextInputLayout) findViewById(R.id.forgot_label);

        progress = new MaterialAlertDialogBuilder(this);
        alertDialog = new AlertDialog.Builder(this);

        if (utils.haveNetwork()) {}

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

                if (s.length() < 5)
                    password_layout.setError(getResources().getString(R.string.valid_short_password));
                else
                    password_layout.setError(null);

            }
        });
        forgot_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if (!utils.validateEmail(String.valueOf(s)))
                    forgot_layout.setError(getResources().getString(R.string.valid_email_error));
                else
                    forgot_layout.setError(null);

            }
        });

        login_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegister();
            }
        });

        login_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utils.viewToggle(loginView, resetView);
            }
        });

        reset_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegister();
            }
        });

        reset_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utils.viewToggle(loginView, resetView);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = forgot_text.getText().toString().trim();
                if (TextUtils.isEmpty(email) || !utils.validateEmail(email)) {
                    forgot_layout.setError(getResources().getString(R.string.valid_email_error));
                } else {
                    firebaseAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Failed to send reset email!" + task.getException(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void userLogin() {
        String em = email_text.getText().toString();
        String pw = password_text.getText().toString();
        if (TextUtils.isEmpty(em) || !utils.validateEmail(em)) {
            email_layout.setError(getResources().getString(R.string.valid_email_error));
            email_text.requestFocus();
        } else if (password_text.getText().length() < 1) {
            password_layout.setError(getResources().getString(R.string.valid_required));
            password_text.requestFocus();
        } else if (password_text.getText().length() < 6) {
            password_layout.setError(getResources().getString(R.string.valid_short_password));
            password_text.requestFocus();
        } else {
            firebaseAuth.signInWithEmailAndPassword(em, pw)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login failed. " + task.getException(), Toast.LENGTH_LONG).show();
                    } else {
                        LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        LoginActivity.this.finish();
                    }
                }
            });
        }
    }

    private void openRegister() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        finish();
    }
}