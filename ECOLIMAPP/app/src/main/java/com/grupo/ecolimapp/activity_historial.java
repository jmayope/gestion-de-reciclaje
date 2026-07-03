package com.grupo.ecolimapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grupo.ecolimapp.DB.SupabaseClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class activity_historial extends AppCompatActivity {

    private RecyclerView recyclerHistorial;
    private TextView tvEstadoHistorial;
    private Button btnVolverInicio;

    private int idUsuario;
    private final OkHttpClient client = new OkHttpClient();

    private final List<RegistroItem> listaRegistros = new ArrayList<>();
    private HistorialAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        recyclerHistorial = findViewById(R.id.recyclerHistorial);
        tvEstadoHistorial = findViewById(R.id.tvEstadoHistorial);
        btnVolverInicio = findViewById(R.id.btnVolverInicio);

        idUsuario = getIntent().getIntExtra("id_usuario", 0);

        recyclerHistorial.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HistorialAdapter(listaRegistros, registro -> {
            Intent intent = new Intent(activity_historial.this, activity_detalle_registro.class);
            intent.putExtra("id_registro", registro.getIdRegistro());
            intent.putExtra("id_usuario", registro.getIdUsuario());
            intent.putExtra("id_ubicacion", registro.getIdUbicacion());
            intent.putExtra("id_residuo", registro.getIdResiduo());
            intent.putExtra("cantidad", registro.getCantidad());
            intent.putExtra("unidad", registro.getUnidad());
            intent.putExtra("fecha", registro.getFecha());
            intent.putExtra("observaciones", registro.getObservaciones());
            startActivity(intent);
        });

        recyclerHistorial.setAdapter(adapter);

        btnVolverInicio.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarRegistrosDeHoy();
    }

    private void cargarRegistrosDeHoy() {
        tvEstadoHistorial.setText("Cargando registros...");
        listaRegistros.clear();
        adapter.notifyDataSetChanged();

        String url = SupabaseClient.SUPABASE_URL +
                "registros_recoleccion?id_usuario=eq." + idUsuario +
                "&order=fecha.desc";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SupabaseClient.SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + SupabaseClient.SUPABASE_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> tvEstadoHistorial.setText("No se pudo cargar el historial."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful() || response.body() == null) {
                        runOnUiThread(() -> tvEstadoHistorial.setText("No se pudo cargar el historial."));
                        return;
                    }

                    String data = response.body().string();
                    JSONArray jsonArray = new JSONArray(data);

                    String fechaHoyLocal = obtenerFechaHoyLocal();
                    List<RegistroItem> nuevaLista = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        String fecha = obj.optString("fecha", "");
                        String fechaLocalRegistro = convertirFechaUTCaLocalSoloFecha(fecha);

                        if (fechaHoyLocal.equals(fechaLocalRegistro)) {
                            RegistroItem item = new RegistroItem();
                            item.setIdRegistro(obj.optInt("id_registro", 0));
                            item.setIdUsuario(obj.optInt("id_usuario", 0));
                            item.setIdUbicacion(obj.optInt("id_ubicacion", 0));
                            item.setIdResiduo(obj.optInt("id_residuo", 0));
                            item.setCantidad(obj.optDouble("cantidad", 0));
                            item.setUnidad(obj.optString("unidad", "kg"));
                            item.setFecha(formatearFechaHoraLocal(fecha));
                            item.setObservaciones(obj.optString("observaciones", ""));
                            nuevaLista.add(item);
                        }
                    }

                    runOnUiThread(() -> {
                        listaRegistros.clear();
                        listaRegistros.addAll(nuevaLista);
                        adapter.notifyDataSetChanged();

                        if (listaRegistros.isEmpty()) {
                            tvEstadoHistorial.setText("No tienes registros hoy.");
                        } else {
                            tvEstadoHistorial.setText("Registros encontrados: " + listaRegistros.size());
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> tvEstadoHistorial.setText("Error al procesar el historial."));
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
            if (fecha == null) return "Sin fecha";

            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
            formatoSalida.setTimeZone(TimeZone.getDefault());
            return formatoSalida.format(fecha);

        } catch (Exception e) {
            e.printStackTrace();
            return "Sin fecha";
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