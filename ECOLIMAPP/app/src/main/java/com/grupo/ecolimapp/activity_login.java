package com.grupo.ecolimapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo.ecolimapp.DB.SupabaseClient;

import org.json.JSONArray;

import java.io.IOException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class activity_login extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(view -> loginUsuario());
    }

    private void loginUsuario() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Ingrese su correo");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Ingrese su contraseña");
            etPassword.requestFocus();
            return;
        }

        try {

            String emailEncoded = URLEncoder.encode(email, "UTF-8");

            String url = SupabaseClient.SUPABASE_URL +
                    "usuarios?correo=eq." + emailEncoded +
                    "&password=eq." + password;

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("apikey", SupabaseClient.SUPABASE_API_KEY)
                    .addHeader("Authorization", "Bearer " + SupabaseClient.SUPABASE_API_KEY)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {

                    runOnUiThread(() ->
                            Toast.makeText(activity_login.this,
                                    "Error de conexión",
                                    Toast.LENGTH_LONG).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    try {

                        String responseData = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseData);

                        runOnUiThread(() -> {

                            try {

                                if (jsonArray.length() > 0) {

                                    int idUsuario = jsonArray.getJSONObject(0).getInt("id_usuario");
                                    String nombre = jsonArray.getJSONObject(0).getString("nombre");
                                    String apellido = jsonArray.getJSONObject(0).getString("apellido");

                                    Toast.makeText(activity_login.this,
                                            "Login correcto",
                                            Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(activity_login.this, activity_inicio.class);
                                    intent.putExtra("id_usuario", idUsuario);
                                    intent.putExtra("nombre", nombre);
                                    intent.putExtra("apellido", apellido);
                                    startActivity(intent);
                                    finish();

                                } else {

                                    Toast.makeText(activity_login.this,
                                            "Correo o contraseña incorrectos",
                                            Toast.LENGTH_LONG).show();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}