package com.grupo.ecolimapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.grupo.ecolimapp.DB.SupabaseClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class activity_resumen extends AppCompatActivity {

    private PieChart pieChartResiduos;
    private BarChart barChartDias;
    private TextView tvEstadoTorta, tvEstadoBarras, tvTituloResumen, tvSubtituloResumen;
    private Button btnVolverResumen;

    private int idUsuario;
    private String nombre, apellido;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen);

        pieChartResiduos = findViewById(R.id.pieChartResiduos);
        barChartDias = findViewById(R.id.barChartDias);
        tvEstadoTorta = findViewById(R.id.tvEstadoTorta);
        tvEstadoBarras = findViewById(R.id.tvEstadoBarras);
        tvTituloResumen = findViewById(R.id.tvTituloResumen);
        tvSubtituloResumen = findViewById(R.id.tvSubtituloResumen);
        btnVolverResumen = findViewById(R.id.btnVolverResumen);

        idUsuario = getIntent().getIntExtra("id_usuario", 0);
        nombre = getIntent().getStringExtra("nombre");
        apellido = getIntent().getStringExtra("apellido");

        if (nombre == null) nombre = "";
        if (apellido == null) apellido = "";

        tvTituloResumen.setText("Resumen de " + nombre);
        tvSubtituloResumen.setText("Estadísticas de " + nombre + " " + apellido);

        btnVolverResumen.setOnClickListener(v -> finish());

        configurarPieChart();
        configurarBarChart();
        cargarDatosResumen();
    }

    private void configurarPieChart() {
        pieChartResiduos.setUsePercentValues(true);
        pieChartResiduos.setDrawHoleEnabled(true);
        pieChartResiduos.setHoleRadius(45f);
        pieChartResiduos.setTransparentCircleRadius(50f);
        pieChartResiduos.setEntryLabelTextSize(12f);
        pieChartResiduos.setCenterText("Hoy");
        pieChartResiduos.setCenterTextSize(16f);

        Description description = new Description();
        description.setText("");
        pieChartResiduos.setDescription(description);

        Legend legend = pieChartResiduos.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
    }

    private void configurarBarChart() {
        Description description = new Description();
        description.setText("");
        barChartDias.setDescription(description);

        barChartDias.setFitBars(true);
        barChartDias.setDrawGridBackground(false);
        barChartDias.getAxisRight().setEnabled(false);
        barChartDias.getLegend().setEnabled(false);
        barChartDias.getAxisLeft().setAxisMinimum(0f);

        XAxis xAxis = barChartDias.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
    }

    private void cargarDatosResumen() {
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
                runOnUiThread(() -> {
                    tvEstadoTorta.setText("No se pudo cargar el gráfico.");
                    tvEstadoBarras.setText("No se pudo cargar el gráfico.");
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful() || response.body() == null) {
                        runOnUiThread(() -> {
                            tvEstadoTorta.setText("No se pudo cargar el gráfico.");
                            tvEstadoBarras.setText("No se pudo cargar el gráfico.");
                        });
                        return;
                    }

                    String data = response.body().string();
                    JSONArray jsonArray = new JSONArray(data);

                    procesarGraficoTorta(jsonArray);
                    procesarGraficoBarras(jsonArray);

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        tvEstadoTorta.setText("Error al procesar datos.");
                        tvEstadoBarras.setText("Error al procesar datos.");
                    });
                }
            }
        });
    }

    private void procesarGraficoTorta(JSONArray jsonArray) {
        try {
            String fechaHoy = obtenerFechaHoy();

            Map<Integer, Float> mapaResiduos = new LinkedHashMap<>();
            mapaResiduos.put(1, 0f);
            mapaResiduos.put(2, 0f);
            mapaResiduos.put(3, 0f);
            mapaResiduos.put(4, 0f);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                String fecha = obj.optString("fecha", "");
                String soloFecha = extraerSoloFecha(fecha);

                if (fechaHoy.equals(soloFecha)) {
                    int idResiduo = obj.optInt("id_residuo", 0);
                    float cantidad = (float) obj.optDouble("cantidad", 0);

                    if (mapaResiduos.containsKey(idResiduo)) {
                        mapaResiduos.put(idResiduo, mapaResiduos.get(idResiduo) + cantidad);
                    }
                }
            }

            ArrayList<PieEntry> entries = new ArrayList<>();

            for (Map.Entry<Integer, Float> entry : mapaResiduos.entrySet()) {
                if (entry.getValue() > 0) {
                    entries.add(new PieEntry(entry.getValue(), obtenerNombreResiduo(entry.getKey())));
                }
            }

            runOnUiThread(() -> {
                if (entries.isEmpty()) {
                    tvEstadoTorta.setText("No hay residuos registrados hoy.");
                    pieChartResiduos.clear();
                    return;
                }

                tvEstadoTorta.setText("Distribución porcentual de residuos recolectados hoy");

                PieDataSet dataSet = new PieDataSet(entries, "");
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                dataSet.setSliceSpace(3f);
                dataSet.setValueTextSize(12f);

                PieData pieData = new PieData(dataSet);
                pieData.setValueFormatter(new PercentFormatter(pieChartResiduos));

                pieChartResiduos.setData(pieData);
                pieChartResiduos.invalidate();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void procesarGraficoBarras(JSONArray jsonArray) {
        try {
            LinkedHashSet<String> diasUnicos = new LinkedHashSet<>();
            LinkedHashMap<String, Float> totalesPorDia = new LinkedHashMap<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String fecha = obj.optString("fecha", "");
                String soloFecha = extraerSoloFecha(fecha);

                if (!soloFecha.isEmpty()) {
                    diasUnicos.add(soloFecha);
                    if (diasUnicos.size() == 5) {
                        break;
                    }
                }
            }

            for (String dia : diasUnicos) {
                totalesPorDia.put(dia, 0f);
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String fecha = obj.optString("fecha", "");
                String soloFecha = extraerSoloFecha(fecha);
                float cantidad = (float) obj.optDouble("cantidad", 0);

                if (totalesPorDia.containsKey(soloFecha)) {
                    totalesPorDia.put(soloFecha, totalesPorDia.get(soloFecha) + cantidad);
                }
            }

            List<String> etiquetas = new ArrayList<>(totalesPorDia.keySet());
            Collections.reverse(etiquetas);

            ArrayList<BarEntry> entradas = new ArrayList<>();
            for (int i = 0; i < etiquetas.size(); i++) {
                String dia = etiquetas.get(i);
                entradas.add(new BarEntry(i, totalesPorDia.get(dia)));
            }

            runOnUiThread(() -> {
                if (entradas.isEmpty()) {
                    tvEstadoBarras.setText("No hay suficientes registros para mostrar.");
                    barChartDias.clear();
                    return;
                }

                tvEstadoBarras.setText("Kg recolectados por día en tus últimos 5 días con actividad");

                BarDataSet dataSet = new BarDataSet(entradas, "Kg por día");
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                dataSet.setValueTextSize(11f);

                BarData barData = new BarData(dataSet);
                barData.setBarWidth(0.6f);

                barChartDias.setData(barData);
                barChartDias.getXAxis().setValueFormatter(new IndexAxisValueFormatter(formatearEtiquetasDias(etiquetas)));
                barChartDias.invalidate();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> formatearEtiquetasDias(List<String> fechas) {
        List<String> resultado = new ArrayList<>();
        for (String fecha : fechas) {
            if (fecha.length() >= 10) {
                resultado.add(fecha.substring(8, 10) + "/" + fecha.substring(5, 7));
            } else {
                resultado.add(fecha);
            }
        }
        return resultado;
    }

    private String obtenerFechaHoy() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String extraerSoloFecha(String fechaRaw) {
        try {
            if (fechaRaw != null && fechaRaw.length() >= 10) {
                return fechaRaw.substring(0, 10);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String obtenerNombreResiduo(int idResiduo) {
        switch (idResiduo) {
            case 1:
                return "Plástico";
            case 2:
                return "Papel";
            case 3:
                return "Vidrio";
            case 4:
                return "Orgánico";
            default:
                return "Otro";
        }
    }
}