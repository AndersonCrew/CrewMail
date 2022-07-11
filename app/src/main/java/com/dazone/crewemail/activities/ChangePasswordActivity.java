package com.dazone.crewemail.activities;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.dazone.crewemail.R;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.databinding.ActivityChangePassBinding;
import com.dazone.crewemail.interfaces.BaseHTTPCallBack;
import com.dazone.crewemail.webservices.HttpRequest;

public class ChangePasswordActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityChangePassBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_change_pass);

        binding.btnBack.setOnClickListener(v -> onBackPressed());

        binding.fragmentChangePassBtnChangePass.setOnClickListener(v -> {
            String currentPass = binding.fragmentChangePassEtOldPass.getText().toString();
            String newPass = binding.fragmentChangePassEtNewPass.getText().toString();
            String confirmPass = binding.fragmentChangePassEtConfirmPass.getText().toString();
            checkPass(currentPass, newPass, confirmPass);
        });

    }

    private void checkPass(String currentPass, String newPass, String confirmPass) {
        if (TextUtils.isEmpty(currentPass)) {
            Toast.makeText(this, "You can't leave this empty current password", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(newPass)) {
            Toast.makeText(this, "You can't leave this empty new password", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(confirmPass)) {
            Toast.makeText(this, "These passwords don't match. Try again?", Toast.LENGTH_LONG).show();
        } else if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "These passwords don't match. Try again?", Toast.LENGTH_LONG).show();
        } else {
            HttpRequest.getInstance().ChangePass(currentPass, newPass, new BaseHTTPCallBack() {
                @Override
                public void onHTTPSuccess() {
                    onBackPressed();
                }

                @Override
                public void onHTTPFail(ErrorData errorDto) {
                    Toast.makeText(getApplicationContext(), errorDto.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}