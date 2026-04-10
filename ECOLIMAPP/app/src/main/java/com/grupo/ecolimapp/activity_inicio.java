package com.grupo.ecolimapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo.ecolimapp.DB.SupabaseClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class activity_inicio extends AppCompatActivity {

    TextView tvNombre, tvRecolecciones, tvKg, tvUltima;
    LinearLayout btnRegistrar, btnHistorial, btnSincronizar;
    Button btnSalir;

    int idUsuario;
    String nombre;
    String apellido;

    OkHttpClient client = new OkHttpClient();
    DecimalFormat df = new DecimalFormat("0.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        tvNombre = findViewById(R.id.tvNombre);
        tvRecolecciones = findViewById(R.id.tvRecolecciones);
        tvKg = findViewById(R.id.tvKg);
        tvUltima = findViewById(R.id.tvUltima);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnHistorial = findViewById(R.id.btnHistorial);
        btnSincronizar = findViewById(R.id.btnSincronizar);
        btnSalir = findViewById(R.id.btnSalir);

        idUsuario = getIntent().getIntExtra("id_usuario", 0);
        nombre = getIntent().getStringExtra("nombre");
        apellido = getIntent().getStringExtra("apellido");

        if (nombre == null) nombre = "";
        if (apellido == null) apellido = "";

        tvNombre.setText("Bienvenido\n" + nombre + " " + apellido);

        btnRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(activity_inicio.this, activity_registrar.class);
            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("nombre", nombre);
            intent.putExtra("apellido", apellido);
            startActivity(intent);
        });

        btnHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(activity_inicio.this, activity_historial.class);
            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("nombre", nombre);
            intent.putExtra("apellido", apellido);
            startActivity(intent);
        });

        btnSincronizar.setOnClickListener(v -> {
            Intent intent = new Intent(activity_inicio.this, activity_resumen.class);
            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("nombre", nombre);
            intent.putExtra("apellido", apellido);
            startActivity(intent);
        });

        btnSalir.setOnClickListener(v -> {
            Intent intent = new Intent(activity_inicio.this, activity_login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarEstadisticas();
    }

    private void cargarEstadisticas() {
        String url = SupabaseClient.SUPABASE_URL +
                "registros_recoleccion?id_usuario=eq." + idUsuario +
                "&order=fecha.asc";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SupabaseClient.SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + SupabaseClient.SUPABASE_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    tvRecolecciones.setText("0");
                    tvKg.setText("0 kg");
                    tvUltima.setText("Sin registros");
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful() || response.body() == null) {
                        runOnUiThread(() -> {
                            tvRecolecciones.setText("0");
                            tvKg.setText("0 kg");
                            tvUltima.setText("Sin registros");
                        });
                        return;
                    }

                    String data = response.body().string();
                    JSONArray jsonArray = new JSONArray(data);

                    int recoleccionesHoy = 0;
                    double totalKgHoy = 0;
                    String ultimaFechaRaw = null;

                    String fechaHoyLocal = obtenerFechaHoyLocal();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        String fechaRaw = obj.optString("fecha", "");
                        double cantidad = obj.optDouble("cantidad", 0);

                        String fechaLocalRegistro = convertirFechaUTCaLocalSoloFecha(fechaRaw);

                        if (fechaHoyLocal.equals(fechaLocalRegistro)) {
                            recoleccionesHoy++;
                            totalKgHoy += cantidad;
                        }

                        if (!fechaRaw.isEmpty()) {
                            ultimaFechaRaw = fechaRaw;
                        }
                    }

                    String ultimaFormateada = "Sin registros";
                    if (ultimaFechaRaw != null) {
                        ultimaFormateada = formatearFechaHoraLocal(ultimaFechaRaw);
                    }

                    int finalRecoleccionesHoy = recoleccionesHoy;
                    double finalTotalKgHoy = totalKgHoy;
                    String finalUltimaFormateada = ultimaFormateada;

                    runOnUiThread(() -> {
                        tvRecolecciones.setText(String.valueOf(finalRecoleccionesHoy));
                        tvKg.setText(df.format(finalTotalKgHoy) + " kg");
                        tvUltima.setText(finalUltimaFormateada);
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        tvRecolecciones.setText("0");
                        tvKg.setText("0 kg");
                        tvUltima.setText("Sin registros");
                    });
                }
            }
        });
    }

    private String obtenerFechaHoyLocal() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date());
    }

    private String convertirFechaUTCaLocalSoloFecha(String fechaRaw) {
        try {
            Date fecha = parseFechaSupabase(fechaRaw);
            if (fecha == null) return "";

            SimpleDateFormat formatoLocal = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            formatoLocal.setTimeZone(TimeZone.getDefault());
            return formatoLocal.format(fecha);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String formatearFechaHoraLocal(String fechaRaw) {
        try {
            Date fecha = parseFechaSupabase(fechaRaw);
            if (fecha == null) return "Sin registros";

            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy\nhh:mm a", Locale.getDefault());
            formatoSalida.setTimeZone(TimeZone.getDefault());
            return formatoSalida.format(fecha);

        } catch (Exception e) {
            e.printStackTrace();
            return "Sin registros";
        }
    }

    private Date parseFechaSupabase(String fechaRaw) {
        try {
            if (fechaRaw == null || fechaRaw.isEmpty()) return null;

            SimpleDateFormat formatoEntrada;

            if (fechaRaw.endsWith("Z")) {
                if (fechaRaw.contains(".")) {
                    formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                } else {
                    formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                }
                formatoEntrada.setTimeZone(TimeZone.getTimeZone("UTC"));
                return formatoEntrada.parse(fechaRaw);
            }

            String fechaLimpia = fechaRaw;
            if (fechaLimpia.contains(".")) {
                fechaLimpia = fechaLimpia.substring(0, fechaLimpia.indexOf("."));
            }
            fechaLimpia = fechaLimpia.replace("T", " ");

            formatoEntrada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            formatoEntrada.setTimeZone(TimeZone.getTimeZone("UTC"));
            return formatoEntrada.parse(fechaLimpia);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}