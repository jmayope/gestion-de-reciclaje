package com.grupo.ecolimapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo.ecolimapp.DB.SupabaseClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class activity_registrar extends AppCompatActivity {

    private Spinner spUbicacion;
    private EditText etCantidad, etObservaciones;
    private Button btnGuardarRegistro;

    private TextView tvTituloRegistrar, tvSubtituloRegistrar;
    private TextView tvPlastico, tvPapel, tvOrganico, tvVidrio;

    private LinearLayout btnPlastico, btnPapel, btnOrganico, btnVidrio;

    private final OkHttpClient client = new OkHttpClient();

    private int idUsuario;
    private int idResiduoSeleccionado = 0;

    private boolean modoEdicion = false;
    private int idRegistroEditar = 0;
    private int idUbicacionEditar = 0;

    private final ArrayList<Ubicacion> listaUbicaciones = new ArrayList<>();
    private ArrayAdapter<Ubicacion> adapterUbicaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        spUbicacion = findViewById(R.id.spUbicacion);
        etCantidad = findViewById(R.id.etCantidad);
        etObservaciones = findViewById(R.id.etObservaciones);
        btnGuardarRegistro = findViewById(R.id.btnGuardarRegistro);

        tvTituloRegistrar = findViewById(R.id.tvTituloRegistrar);
        tvSubtituloRegistrar = findViewById(R.id.tvSubtituloRegistrar);

        btnPlastico = findViewById(R.id.btnPlastico);
        btnPapel = findViewById(R.id.btnPapel);
        btnOrganico = findViewById(R.id.btnOrganico);
        btnVidrio = findViewById(R.id.btnVidrio);

        tvPlastico = findViewById(R.id.tvPlastico);
        tvPapel = findViewById(R.id.tvPapel);
        tvOrganico = findViewById(R.id.tvOrganico);
        tvVidrio = findViewById(R.id.tvVidrio);

        idUsuario = getIntent().getIntExtra("id_usuario", 0);

        modoEdicion = getIntent().getBooleanExtra("modo_edicion", false);
        idRegistroEditar = getIntent().getIntExtra("id_registro", 0);
        idUbicacionEditar = getIntent().getIntExtra("id_ubicacion", 0);

        if (modoEdicion) {
            tvTituloRegistrar.setText("Modificar Registro");
            tvSubtituloRegistrar.setText("Actualice la información del residuo");
            btnGuardarRegistro.setText("Actualizar Registro");
        }

        adapterUbicaciones = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                listaUbicaciones
        );
        adapterUbicaciones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUbicacion.setAdapter(adapterUbicaciones);

        cargarUbicaciones();

        btnPlastico.setOnClickListener(v -> seleccionarResiduo(1));
        btnPapel.setOnClickListener(v -> seleccionarResiduo(2));
        btnVidrio.setOnClickListener(v -> seleccionarResiduo(3));
        btnOrganico.setOnClickListener(v -> seleccionarResiduo(4));

        btnGuardarRegistro.setOnClickListener(v -> {
            if (modoEdicion) {
                actualizarRegistro();
            } else {
                guardarRegistro();
            }
        });
    }

    private void cargarUbicaciones() {
        String url = SupabaseClient.SUPABASE_URL + "ubicaciones?select=id_ubicacion,nombre_lugar";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SupabaseClient.SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + SupabaseClient.SUPABASE_API_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(activity_registrar.this,
                                "Error cargando ubicaciones",
                                Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String respuesta = response.body() != null ? response.body().string() : "[]";
                    JSONArray jsonArray = new JSONArray(respuesta);

                    listaUbicaciones.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        int idUbicacion = obj.getInt("id_ubicacion");
                        String nombreLugar = obj.getString("nombre_lugar");

                        listaUbicaciones.add(new Ubicacion(idUbicacion, nombreLugar));
                    }

                    runOnUiThread(() -> {
                        adapterUbicaciones.notifyDataSetChanged();

                        if (modoEdicion) {
                            cargarDatosEdicion();
                        }
                    });

                } catch (Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(activity_registrar.this,
                                    "Error procesando ubicaciones",
                                    Toast.LENGTH_LONG).show());
                }
            }
        });
    }

    private void cargarDatosEdicion() {
        int idResiduo = getIntent().getIntExtra("id_residuo", 0);
        double cantidad = getIntent().getDoubleExtra("cantidad", 0);
        String observaciones = getIntent().getStringExtra("observaciones");

        idResiduoSeleccionado = idResiduo;
        seleccionarResiduo(idResiduo);

        if (cantidad > 0) {
            etCantidad.setText(String.valueOf(cantidad));
        }

        if (observaciones != null) {
            etObservaciones.setText(observaciones);
        }

        for (int i = 0; i < listaUbicaciones.size(); i++) {
            if (listaUbicaciones.get(i).getIdUbicacion() == idUbicacionEditar) {
                spUbicacion.setSelection(i);
                break;
            }
        }
    }

    private void seleccionarResiduo(int idResiduo) {
        idResiduoSeleccionado = idResiduo;
        limpiarSeleccion();

        if (idResiduo == 1) {
            btnPlastico.setBackgroundResource(R.drawable.bg_residuo_item_selected);
            tvPlastico.setTextColor(getColor(android.R.color.black));
        } else if (idResiduo == 2) {
            btnPapel.setBackgroundResource(R.drawable.bg_residuo_item_selected);
            tvPapel.setTextColor(getColor(android.R.color.black));
        } else if (idResiduo == 3) {
            btnVidrio.setBackgroundResource(R.drawable.bg_residuo_item_selected);
            tvVidrio.setTextColor(getColor(android.R.color.black));
        } else if (idResiduo == 4) {
            btnOrganico.setBackgroundResource(R.drawable.bg_residuo_item_selected);
            tvOrganico.setTextColor(getColor(android.R.color.black));
        }
    }

    private void limpiarSeleccion() {
        btnPlastico.setBackgroundResource(R.drawable.bg_residuo_item);
        btnPapel.setBackgroundResource(R.drawable.bg_residuo_item);
        btnVidrio.setBackgroundResource(R.drawable.bg_residuo_item);
        btnOrganico.setBackgroundResource(R.drawable.bg_residuo_item);

        tvPlastico.setTextColor(getColor(android.R.color.black));
        tvPapel.setTextColor(getColor(android.R.color.black));
        tvVidrio.setTextColor(getColor(android.R.color.black));
        tvOrganico.setTextColor(getColor(android.R.color.black));
    }

    private boolean validarCampos() {
        String cantidadTexto = etCantidad.getText().toString().trim();

        if (idUsuario == 0) {
            Toast.makeText(this, "No se recibió el id del usuario", Toast.LENGTH_LONG).show();
            return false;
        }

        if (listaUbicaciones.isEmpty()) {
            Toast.makeText(this, "No hay ubicaciones disponibles", Toast.LENGTH_LONG).show();
            return false;
        }

        if (idResiduoSeleccionado == 0) {
            Toast.makeText(this, "Seleccione un tipo de residuo", Toast.LENGTH_LONG).show();
            return false;
        }

        if (TextUtils.isEmpty(cantidadTexto)) {
            etCantidad.setError("Ingrese la cantidad");
            etCantidad.requestFocus();
            return false;
        }

        return true;
    }

    private void guardarRegistro() {
        if (!validarCampos()) return;

        String cantidadTexto = etCantidad.getText().toString().trim();
        String observaciones = etObservaciones.getText().toString().trim();

        try {
            double cantidad = Double.parseDouble(cantidadTexto);

            Ubicacion ubicacionSeleccionada = (Ubicacion) spUbicacion.getSelectedItem();
            int idUbicacion = ubicacionSeleccionada.getIdUbicacion();

            JSONObject json = new JSONObject();
            json.put("id_usuario", idUsuario);
            json.put("id_ubicacion", idUbicacion);
            json.put("id_residuo", idResiduoSeleccionado);
            json.put("cantidad", cantidad);
            json.put("unidad", "kg");
            json.put("observaciones", observaciones);

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody requestBody = RequestBody.create(json.toString(), mediaType);

            String url = SupabaseClient.SUPABASE_URL + "registros_recoleccion";

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("apikey", SupabaseClient.SUPABASE_API_KEY)
                    .addHeader("Authorization", "Bearer " + SupabaseClient.SUPABASE_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=representation")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(activity_registrar.this,
                                    "Error de conexión: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String respuesta = response.body() != null ? response.body().string() : "";

                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(activity_registrar.this,
                                    "Registro guardado correctamente",
                                    Toast.LENGTH_LONG).show();

                            etCantidad.setText("");
                            etObservaciones.setText("");
                            idResiduoSeleccionado = 0;
                            limpiarSeleccion();
                            spUbicacion.setSelection(0);
                        } else {
                            Toast.makeText(activity_registrar.this,
                                    "Error al guardar: " + respuesta,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese una cantidad válida", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Ocurrió un error al preparar el registro", Toast.LENGTH_LONG).show();
        }
    }

    private void actualizarRegistro() {
        if (!validarCampos()) return;

        String cantidadTexto = etCantidad.getText().toString().trim();
        String observaciones = etObservaciones.getText().toString().trim();

        try {
            double cantidad = Double.parseDouble(cantidadTexto);

            Ubicacion ubicacionSeleccionada = (Ubicacion) spUbicacion.getSelectedItem();
            int idUbicacion = ubicacionSeleccionada.getIdUbicacion();

            JSONObject json = new JSONObject();
            json.put("id_ubicacion", idUbicacion);
            json.put("id_residuo", idResiduoSeleccionado);
            json.put("cantidad", cantidad);
            json.put("unidad", "kg");
            json.put("observaciones", observaciones);

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody requestBody = RequestBody.create(json.toString(), mediaType);

            String url = SupabaseClient.SUPABASE_URL +
                    "registros_recoleccion?id_registro=eq." + idRegistroEditar;

            Request request = new Request.Builder()
                    .url(url)
                    .patch(requestBody)
                    .addHeader("apikey", SupabaseClient.SUPABASE_API_KEY)
                    .addHeader("Authorization", "Bearer " + SupabaseClient.SUPABASE_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=representation")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(activity_registrar.this,
                                    "Error de conexión: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String respuesta = response.body() != null ? response.body().string() : "";

                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(activity_registrar.this,
                                    "Registro actualizado correctamente",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(activity_registrar.this,
                                    "Error al actualizar: " + respuesta,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese una cantidad válida", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Ocurrió un error al preparar la actualización", Toast.LENGTH_LONG).show();
        }
    }
}
