package com.miapp.duelchoice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.miapp.duelchoice.database.AppDatabase;
import com.miapp.duelchoice.database.Opcion;

import java.util.List;
import java.util.Locale;

public class RankingActivity extends AppCompatActivity {

    private TextView tvPrimeroNombre, tvPrimeroPuntos;
    private TextView tvSegundoNombre, tvSegundoPuntos;
    private TextView tvTerceroNombre, tvTerceroPuntos;
    private Button btnVolver;
    private AppDatabase db;
    private int categoriaId;
    private boolean desdeFinalizar;

    // Botones de idioma
    private TextView btnES, btnEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        cargarIdiomaGuardado();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnES = toolbar.findViewById(R.id.btnIdiomaES);
        btnEN = toolbar.findViewById(R.id.btnIdiomaEN);

        String idiomaActual = getSharedPreferences("settings", MODE_PRIVATE)
                .getString("idioma", "es");
        resaltarIdioma(idiomaActual);

        btnES.setOnClickListener(v -> cambiarIdioma("es"));
        btnEN.setOnClickListener(v -> cambiarIdioma("en"));

        // Resto del código
        categoriaId = getIntent().getIntExtra("categoria_id", -1);
        desdeFinalizar = getIntent().getBooleanExtra("desde_finalizar", false);

        db = AppDatabase.getInstance(this);

        tvPrimeroNombre = findViewById(R.id.tvPrimeroNombre);
        tvPrimeroPuntos = findViewById(R.id.tvPrimeroPuntos);
        tvSegundoNombre = findViewById(R.id.tvSegundoNombre);
        tvSegundoPuntos = findViewById(R.id.tvSegundoPuntos);
        tvTerceroNombre = findViewById(R.id.tvTerceroNombre);
        tvTerceroPuntos = findViewById(R.id.tvTerceroPuntos);
        btnVolver = findViewById(R.id.btnVolver);

        btnVolver.setOnClickListener(v -> {
            if (desdeFinalizar) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                finish();
            }
        });

        cargarRanking();
    }

    private void resaltarIdioma(String idioma) {
        if (idioma.equals("es")) {
            btnES.setBackgroundResource(R.drawable.boton_idioma_seleccionado);
            btnEN.setBackgroundResource(R.drawable.boton_idioma);
        } else {
            btnEN.setBackgroundResource(R.drawable.boton_idioma_seleccionado);
            btnES.setBackgroundResource(R.drawable.boton_idioma);
        }
    }

    private void cambiarIdioma(String codigo) {
        getSharedPreferences("settings", MODE_PRIVATE)
                .edit()
                .putString("idioma", codigo)
                .apply();

        Locale locale = new Locale(codigo);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        Toast.makeText(this, "Idioma cambiado", Toast.LENGTH_SHORT).show();
        recreate();
    }

    private void cargarIdiomaGuardado() {
        String idioma = getSharedPreferences("settings", MODE_PRIVATE)
                .getString("idioma", "es");

        Locale locale = new Locale(idioma);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    @SuppressLint("StaticFieldLeak")
    private void cargarRanking() {
        new AsyncTask<Void, Void, List<Opcion>>() {
            @Override
            protected List<Opcion> doInBackground(Void... params) {
                return db.opcionDao().getRankingByCategoria(categoriaId);
            }
            @Override
            protected void onPostExecute(List<Opcion> ranking) {
                if (ranking == null || ranking.isEmpty()) {
                    Toast.makeText(RankingActivity.this, R.string.no_hay_ranking, Toast.LENGTH_SHORT).show();
                    return;
                }

                for (int i = 0; i < ranking.size() && i < 3; i++) {
                    Opcion op = ranking.get(i);
                    switch (i) {
                        case 0:
                            tvPrimeroNombre.setText(op.getTexto());
                            tvPrimeroPuntos.setText(getString(R.string.elo) + " " + op.getPuntuacionElo());
                            break;
                        case 1:
                            tvSegundoNombre.setText(op.getTexto());
                            tvSegundoPuntos.setText(getString(R.string.elo) + " " + op.getPuntuacionElo());
                            break;
                        case 2:
                            tvTerceroNombre.setText(op.getTexto());
                            tvTerceroPuntos.setText(getString(R.string.elo) + " " + op.getPuntuacionElo());
                            break;
                    }
                }
            }
        }.execute();
    }
}