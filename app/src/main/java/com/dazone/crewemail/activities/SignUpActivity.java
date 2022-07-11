package com.dazone.crewemail.activities;

import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.dazone.crewemail.R;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.interfaces.BaseHTTPCallBackWithString;
import com.dazone.crewemail.webservices.HttpRequest;

/**
 * Created by david on 12/3/15.
 */

public class SignUpActivity extends BaseActivity {
    private EditText mEtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initToolBar();

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.app_base_color));
        }

        mEtEmail = findViewById(R.id.sign_up_edt_email);
        RelativeLayout mBtnSignUp = findViewById(R.id.login_btn_register);

        if (mEtEmail != null) {
            mEtEmail.setSelection(0);
        }

        if (mBtnSignUp != null) {
            mBtnSignUp.setOnClickListener(v -> {
                String email = mEtEmail.getText().toString().trim();
                signUp(email);
            });
        }
    }

    private void signUp(String email) {
        HttpRequest.getInstance().signUp(new BaseHTTPCallBackWithString() {
            @Override
            public void onHTTPSuccess(String message) {
                Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onHTTPFail(ErrorData errorData) {
                Toast.makeText(SignUpActivity.this, errorData.getMessage(), Toast.LENGTH_LONG).show();
                mEtEmail.requestFocus();
            }
        }, email);
    }

    public void initToolBar() {
        Toolbar toolbar = findViewById(R.id.tbSignUpToolbar);

        if (toolbar == null) {
            return;
        }

        toolbar.setTitle(R.string.title_sign_up_screen);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        toolbar.setNavigationOnClickListener(
                v -> finish()
        );
    }
}