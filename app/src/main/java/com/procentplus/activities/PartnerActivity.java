package com.procentplus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.procentplus.R;
import com.procentplus.retrofit.RetrofitClient;
import com.procentplus.retrofit.interfaces.ILogout;
import com.procentplus.retrofit.models.CategoriesResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.procentplus.activities.MainActivity.prefConfig;

public class PartnerActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.partner_cab_scan_button)
    Button scan;

    @BindView(R.id.partner_cab_sum)
    EditText sumText;

    @BindView(R.id.partner_cab_send)
    Button send;

    @BindView(R.id.partner_cab_logout)
    ImageView logout_btn;

    private ILogout iLogout;
    private Retrofit retrofit;
    private String sum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner);
        ButterKnife.bind(this);

        retrofit = RetrofitClient.getInstance();

        scan.setOnClickListener(this);
        send.setOnClickListener(this);
        logout_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.partner_cab_scan_button:
                Log.d("Partner Activity", "readIsPartner - if - " + prefConfig.readIsPartner());
                Intent auth_intent = new Intent(PartnerActivity.this, QrCodeScannerActivity.class);
                startActivity(auth_intent);
                finish();
                break;
            case R.id.partner_cab_send:
                sum = sumText.getText().toString().trim();
                if (!sum.isEmpty()) {

                } else {
                    prefConfig.displayToast("Введите сумму!");
                }
                break;
            case R.id.partner_cab_logout:
                logout();
                break;
        }
    }

    private void logout() {
        iLogout = retrofit.create(ILogout.class);
        Call<CategoriesResponse> call = iLogout.logOut(prefConfig.readToken());

        call.enqueue(new Callback<CategoriesResponse>() {
            @Override
            public void onResponse(Call<CategoriesResponse> call, Response<CategoriesResponse> response) {
                int statusCode = response.code();
                Log.d("LOGGER Logout", "statusCode: " + statusCode);
                if (statusCode == 200 || statusCode == 204) {
                    prefConfig.writeLoginStatus(false);
                    Intent intent = new Intent(PartnerActivity.this, AuthActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<CategoriesResponse> call, Throwable t) {
                prefConfig.displayToast("Произошла ошибка при попытке выхода из профиля\n" + t.getStackTrace());
            }
        });

    }




}
