package com.grupo.ecolimapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo.ecolimapp.DB.SupabaseClient;

import java.io.IOException;
import java.text.DecimalFormat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class activity_detalle_registro extends AppCompatActivity {

    private TextView tvIdRegistroDetalle, tvResiduoDetalle, tvUbicacionDetalle,
            tvCantidadDetalle, tvFechaDetalle, tvObservacionesDetalle;

    private Button btnVolverHistorial, btnModificarRegistro, btnEliminarRegistro;

    private int idRegistro, idUsuario, idUbicacion, idResiduo;
    private double cantidad;
    private String unidad, fecha, observaciones;

    private final OkHttpClient client = new OkHttpClient();
    private final DecimalFormat df = new DecimalFormat("0.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_registro);

        tvIdRegistroDetalle = findViewById(R.id.tvIdRegistroDetalle);
        tvResiduoDetalle = findViewById(R.id.tvResiduoDetalle);
        tvUbicacionDetalle = findViewById(R.id.tvUbicacionDetalle);
        tvCantidadDetalle = findViewById(R.id.tvCantidadDetalle);
        tvFechaDetalle = findViewById(R.id.tvFechaDetalle);
        tvObservacionesDetalle = findViewById(R.id.tvObservacionesDetalle);

        btnVolverHistorial = findViewById(R.id.btnVolverHistorial);
        btnModificarRegistro = findViewById(R.id.btnModificarRegistro);
        btnEliminarRegistro = findViewById(R.id.btnEliminarRegistro);

        idRegistro = getIntent().getIntExtra("id_registro", 0);
        idUsuario = getIntent().getIntExtra("id_usuario", 0);
        idUbicacion = getIntent().getIntExtra("id_ubicacion", 0);
        idResiduo = getIntent().getIntExtra("id_residuo", 0);
        cantidad = getIntent().getDoubleExtra("cantidad", 0);
        unidad = getIntent().getStringExtra("unidad");
        fecha = getIntent().getStringExtra("fecha");
        observaciones = getIntent().getStringExtra("observaciones");

        if (unidad == null) unidad = "kg";
        if (fecha == null) fecha = "Sin fecha";
        if (observaciones == null || observaciones.trim().isEmpty()) observaciones = "Sin observaciones";

        cargarDatos();

        btnVolverHistorial.setOnClickListener(v -> finish());

        btnModificarRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(activity_detalle_registro.this, activity_registrar.class);
            intent.putExtra("modo_edicion", true);
            intent.putExtra("id_registro", idRegistro);
            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("id_ubicacion", idUbicacion);
            intent.putExtra("id_residuo", idResiduo);
            intent.putExtra("cantidad", cantidad);
            intent.putExtra("unidad", unidad);
            intent.putExtra("fecha", fecha);
            intent.putExtra("observaciones", observaciones);
            startActivity(intent);
        });

        btnEliminarRegistro.setOnClickListener(v -> eliminarRegistro());
    }

    private void cargarDatos() {
        tvIdRegistroDetalle.setText(String.valueOf(idRegistro));
        tvResiduoDetalle.setText(String.valueOf(idResiduo));
        tvUbicacionDetalle.setText(String.valueOf(idUbicacion));
        tvCantidadDetalle.setText(df.format(cantidad) + " " + unidad);
        tvFechaDetalle.setText(fecha);
        tvObservacionesDetalle.setText(observaciones);
    }

    private void eliminarRegistro() {
        String url = SupabaseClient.SUPABASE_URL +
                "registros_recoleccion?id_registro=eq." + idRegistro;

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("apikey", SupabaseClient.SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + SupabaseClient.SUPABASE_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(activity_detalle_registro.this, "No se pudo eliminar el registro.", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(activity_detalle_registro.this, "Registro eliminado correctamente.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(activity_detalle_registro.this, activity_historial.class);
                        intent.putExtra("id_usuario", idUsuario);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(activity_detalle_registro.this, "Error al eliminar el registro.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}