package com.android.airbnb.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.airbnb.R;
import com.android.airbnb.WelcomeActivity;
import com.android.airbnb.data.ApiService;
import com.android.airbnb.data.LoginData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    Retrofit retrofit;
    ApiService apiService;

    private Button btnPreviousLogin, btnNextLoginEmail, btnUseTel;
    private TextView txtFindPassword, txtLogin, txtEmail, txtPassword;
    private EditText editEmail, editPassword;
    private CheckBox checkIndicate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setViews();
        setListeners();
    }

    private void setViews(){
        btnPreviousLogin = (Button) findViewById(R.id.btnPreviousLogin);
        btnNextLoginEmail = (Button) findViewById(R.id.btnNextLoginEmail);
        btnUseTel = (Button) findViewById(R.id.btnUseTel);
        txtFindPassword = (TextView) findViewById(R.id.txtFindPassword);
        txtLogin = (TextView) findViewById(R.id.txtLogin);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtPassword = (TextView) findViewById(R.id.txtPassword1);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword1);
        checkIndicate = (CheckBox) findViewById(R.id.checkIndicate);
    }

    private void setListeners(){
        btnPreviousLogin.setOnClickListener(this);
        btnNextLoginEmail.setOnClickListener(this);
        btnUseTel.setOnClickListener(this);
        txtFindPassword.setOnClickListener(this);
        indicatePassword();
    }


    // Password 표시하기
    private void indicatePassword(){
        checkIndicate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true) {
                    editPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//                    editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else if (isChecked == false){
//                    editPassword.setTransformationMethod(Text);
                    editPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                }
            }
        });
    }


    private void postLoginData(){
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        LoginData loginData = new LoginData();
        loginData.setEmail(editEmail.getText().toString());
        loginData.setPassword(editPassword.getText().toString());

        Call<LoginData> postLoginData = apiService.postLoginData(loginData);
        postLoginData.enqueue(new Callback<LoginData>() {
            @Override
            public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                Log.e("===============", "데이터 전송");
            }

            @Override
            public void onFailure(Call<LoginData> call, Throwable t) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.btnPreviousLogin :
                intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btnNextLoginEmail :
                postLoginData();
                Toast.makeText(LoginActivity.this, "메인 화면으로 이동", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnUseTel :
                Toast.makeText(LoginActivity.this, "전화번호 사용 버튼 클릭", Toast.LENGTH_SHORT).show();
                break;
            case R.id.txtFindPassword :
                intent = new Intent(LoginActivity.this, FindPasswordActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
