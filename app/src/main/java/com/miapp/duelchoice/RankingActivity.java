package com.miapp.duelchoice;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.miapp.duelchoice.database.AppDatabase;
import com.miapp.duelchoice.database.Opcion;

import java.util.List;

public class RankingActivity extends AppCompatActivity {

    private TextView tvPrimeroNombre, tvPrimeroPuntos;
    private TextView tvSegundoNombre, tvSegundoPuntos;
    private TextView tvTerceroNombre, tvTerceroPuntos;
    private Button btnVolver;
    private AppDatabase db;
    private int categoriaId;
    private boolean desdeFinalizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

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
                // Si venía de finalizar, volvemos a MainActivity
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                // Si venía del botón Ranking, volvemos al juego
                finish();
            }
        });

        cargarRanking();
    }

    private void cargarRanking() {
        new AsyncTask<Void, Void, List<Opcion>>() {
            @Override
            protected List<Opcion> doInBackground(Void... params) {
                return db.opcionDao().getRankingByCategoria(categoriaId);
            }

            @Override
            protected void onPostExecute(List<Opcion> ranking) {
                if (ranking == null || ranking.isEmpty()) {
                    Toast.makeText(RankingActivity.this,
                            "No hay datos de ranking", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Mostrar podio
                for (int i = 0; i < ranking.size() && i < 3; i++) {
                    Opcion op = ranking.get(i);
                    switch (i) {
                        case 0: // 1º
                            tvPrimeroNombre.setText(op.getTexto());
                            tvPrimeroPuntos.setText("ELO: " + op.getPuntuacionElo());
                            break;
                        case 1: // 2º
                            tvSegundoNombre.setText(op.getTexto());
                            tvSegundoPuntos.setText("ELO: " + op.getPuntuacionElo());
                            break;
                        case 2: // 3º
                            tvTerceroNombre.setText(op.getTexto());
                            tvTerceroPuntos.setText("ELO: " + op.getPuntuacionElo());
                            break;
                    }
                }
            }
        }.execute();
    }
}