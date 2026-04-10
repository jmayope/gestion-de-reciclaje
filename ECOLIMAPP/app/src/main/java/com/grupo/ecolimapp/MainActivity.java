package com.grupo.ecolimapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo.ecolimapp.DB.SupabaseClient;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // probar conexión
        probarConexion();

        // SIEMPRE abrir login después de 3 segundos
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(MainActivity.this, activity_login.class);
                startActivity(intent);
                finish();

            }
        }, 3000);
    }

    private void probarConexion() {

        String url = SupabaseClient.SUPABASE_URL + "usuarios?limit=1";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SupabaseClient.SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + SupabaseClient.SUPABASE_API_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this,
                                "No se pudo conectar a Supabase",
                                Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) {

                runOnUiThread(() -> {

                    if (response.isSuccessful()) {

                        Toast.makeText(MainActivity.this,
                                "Conectado a la base de datos",
                                Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(MainActivity.this,
                                "Error al conectar con Supabase",
                                Toast.LENGTH_SHORT).show();
                    }

                });
            }
        });
    }
}