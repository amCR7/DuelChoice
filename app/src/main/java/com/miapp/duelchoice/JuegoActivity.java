package com.miapp.duelchoice;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.miapp.duelchoice.database.AppDatabase;
import com.miapp.duelchoice.database.Opcion;

import java.util.List;

public class JuegoActivity extends AppCompatActivity implements
        OpcionFragment.OnOpcionClickListener {

    private AppDatabase db;
    private int categoriaId;
    private String categoriaNombre;
    private Opcion opcionA, opcionB;
    private boolean cargandoPregunta = false;

    private PreguntaFragment preguntaFragment;
    private OpcionFragment fragmentA, fragmentB;

    private Button btnFinalizar, btnRanking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        categoriaId = getIntent().getIntExtra("categoria_id", -1);
        categoriaNombre = getIntent().getStringExtra("categoria_nombre");

        if (categoriaId == -1) {
            Toast.makeText(this, "Error: Categoría no válida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = AppDatabase.getInstance(this);

        configurarFragments();
        configurarBotones();
    }

    private void configurarFragments() {
        try {
            // Obtener referencias a los fragments existentes
            fragmentA = (OpcionFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentOpcionA);
            fragmentB = (OpcionFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentOpcionB);
            preguntaFragment = (PreguntaFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentPregunta);

            if (fragmentA == null || fragmentB == null || preguntaFragment == null) {
                Toast.makeText(this, "Error al cargar los fragments", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Configurar argumentos
            Bundle argsA = fragmentA.getArguments();
            if (argsA == null) argsA = new Bundle();
            argsA.putInt("posicion", 1);
            fragmentA.setArguments(argsA);

            Bundle argsB = fragmentB.getArguments();
            if (argsB == null) argsB = new Bundle();
            argsB.putInt("posicion", 2);
            fragmentB.setArguments(argsB);

            // Deshabilitar clicks mientras carga
            fragmentA.setClickable(false);
            fragmentB.setClickable(false);

            cargarNuevaPregunta();

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void cargarNuevaPregunta() {
        if (cargandoPregunta) return;
        cargandoPregunta = true;

        if (preguntaFragment != null) {
            preguntaFragment.actualizarPregunta("Cargando...");
        }

        new CargarPreguntaTask().execute(categoriaId);
    }

    private class CargarPreguntaTask extends AsyncTask<Integer, Void, List<Opcion>> {

        @Override
        protected List<Opcion> doInBackground(Integer... params) {
            int catId = params[0];
            try {
                return db.opcionDao().getDosOpcionesAleatorias(catId);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Opcion> dosOpciones) {
            cargandoPregunta = false;

            if (dosOpciones == null) {
                Toast.makeText(JuegoActivity.this,
                        "Error al cargar opciones", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            if (dosOpciones.size() < 2) {
                Toast.makeText(JuegoActivity.this,
                        "No hay suficientes opciones (necesitas al menos 2)",
                        Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            opcionA = dosOpciones.get(0);
            opcionB = dosOpciones.get(1);

            if (opcionA == null || opcionB == null) {
                Toast.makeText(JuegoActivity.this,
                        "Las opciones tienen datos inválidos", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Actualizar fragments
            if (fragmentA != null && fragmentB != null && preguntaFragment != null) {
                fragmentA.setOpcion(opcionA, JuegoActivity.this);
                fragmentB.setOpcion(opcionB, JuegoActivity.this);
                preguntaFragment.actualizarPregunta("¿QUÉ PREFIERES?");

                // Habilitar clicks
                fragmentA.setClickable(true);
                fragmentB.setClickable(true);
            }
        }
    }

    @Override
    public void onOpcionClick(Opcion opcionSeleccionada) {
        if (cargandoPregunta) return;

        if (opcionSeleccionada == null || opcionA == null || opcionB == null) {
            return;
        }

        if (opcionSeleccionada.getId() == opcionA.getId()) {
            registrarVoto(opcionA, opcionB);
        } else {
            registrarVoto(opcionB, opcionA);
        }

        // Deshabilitar clicks mientras carga nueva pregunta
        if (fragmentA != null && fragmentB != null) {
            fragmentA.setClickable(false);
            fragmentB.setClickable(false);
        }

        cargarNuevaPregunta();
    }

    private void registrarVoto(Opcion ganadora, Opcion perdedora) {
        new RegistrarVotoTask().execute(new Opcion[]{ganadora, perdedora});
    }

    private class RegistrarVotoTask extends AsyncTask<Opcion[], Void, Void> {

        @Override
        protected Void doInBackground(Opcion[]... params) {
            try {
                Opcion ganadora = params[0][0];
                Opcion perdedora = params[0][1];

                int k = 32;
                double probabilidadGanadora = 1.0 / (1 + Math.pow(10,
                        (perdedora.getPuntuacionElo() - ganadora.getPuntuacionElo()) / 400.0));

                int nuevoEloGanadora = ganadora.getPuntuacionElo() + (int) (k * (1 - probabilidadGanadora));
                int nuevoEloPerdedora = perdedora.getPuntuacionElo() + (int) (k * (0 - (1 - probabilidadGanadora)));

                ganadora.setPuntuacionElo(nuevoEloGanadora);
                ganadora.setVecesJugadas(ganadora.getVecesJugadas() + 1);
                ganadora.setVecesGanadas(ganadora.getVecesGanadas() + 1);

                perdedora.setPuntuacionElo(nuevoEloPerdedora);
                perdedora.setVecesJugadas(perdedora.getVecesJugadas() + 1);
                perdedora.setVecesPerdidas(perdedora.getVecesPerdidas() + 1);

                db.opcionDao().update(ganadora);
                db.opcionDao().update(perdedora);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(JuegoActivity.this, "¡Votado!", Toast.LENGTH_SHORT).show();
        }
    }

    private void configurarBotones() {
        btnFinalizar = findViewById(R.id.btnFinalizar);
        btnRanking = findViewById(R.id.btnRanking);

        btnFinalizar.setOnClickListener(v -> {
            new AlertDialog.Builder(JuegoActivity.this)
                    .setTitle("Finalizar Partida")
                    .setMessage("¿Seguro que quieres finalizar la partida?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        Intent intent = new Intent(JuegoActivity.this, RankingActivity.class);
                        intent.putExtra("categoria_id", categoriaId);
                        intent.putExtra("desde_finalizar", true);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        btnRanking.setOnClickListener(v -> {
            Intent intent = new Intent(JuegoActivity.this, RankingActivity.class);
            intent.putExtra("categoria_id", categoriaId);
            intent.putExtra("desde_finalizar", false);
            startActivity(intent);
        });
    }
}