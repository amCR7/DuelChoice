package com.miapp.duelchoice;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.miapp.duelchoice.database.AppDatabase;
import com.miapp.duelchoice.database.Opcion;

import java.util.List;
import java.util.Locale;

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

    // Botones de idioma
    private TextView btnES, btnEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);
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
        categoriaNombre = getIntent().getStringExtra("categoria_nombre");

        if (categoriaId == -1) {
            Toast.makeText(this, R.string.error_categoria_no_valida, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = AppDatabase.getInstance(this);
        configurarFragments();
        configurarBotones();
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

    // Resto de métodos sin cambios...
    private void configurarFragments() {
        try {
            fragmentA = new OpcionFragment();
            Bundle argsA = new Bundle();
            argsA.putInt("color", 0xFFFF3B3F);
            argsA.putInt("imagen_res_id", R.drawable.imagen_opcion_a);
            fragmentA.setArguments(argsA);

            fragmentB = new OpcionFragment();
            Bundle argsB = new Bundle();
            argsB.putInt("color", 0xFF3A86FF);
            argsB.putInt("imagen_res_id", R.drawable.imagen_opcion_b);
            fragmentB.setArguments(argsB);

            preguntaFragment = new PreguntaFragment();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.containerOpcionA, fragmentA);
            ft.replace(R.id.containerOpcionB, fragmentB);
            ft.replace(R.id.containerPregunta, preguntaFragment);
            ft.commit();

            getSupportFragmentManager().executePendingTransactions();

            if (fragmentA != null) fragmentA.setClickable(false);
            if (fragmentB != null) fragmentB.setClickable(false);

            cargarNuevaPregunta();

        } catch (Exception e) {
            Toast.makeText(this, R.string.error_fragments, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void cargarNuevaPregunta() {
        if (cargandoPregunta) return;
        cargandoPregunta = true;

        if (preguntaFragment != null) {
            preguntaFragment.actualizarPregunta(getString(R.string.cargando));
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
                Toast.makeText(JuegoActivity.this, R.string.error_cargar_opciones, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            if (dosOpciones.size() < 2) {
                Toast.makeText(JuegoActivity.this, R.string.no_hay_suficientes_opciones, Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            opcionA = dosOpciones.get(0);
            opcionB = dosOpciones.get(1);

            if (opcionA == null || opcionB == null) {
                Toast.makeText(JuegoActivity.this, R.string.datos_invalidos, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            if (fragmentA != null && fragmentB != null && preguntaFragment != null) {
                fragmentA.setOpcion(opcionA, JuegoActivity.this);
                fragmentB.setOpcion(opcionB, JuegoActivity.this);
                preguntaFragment.actualizarPregunta(getString(R.string.vs));
                preguntaFragment.actualizarCategoria(categoriaNombre);
                fragmentA.setClickable(true);
                fragmentB.setClickable(true);
            }
        }
    }

    @Override
    public void onOpcionClick(Opcion opcionSeleccionada) {
        if (cargandoPregunta) return;
        if (opcionSeleccionada == null || opcionA == null || opcionB == null) return;

        if (opcionSeleccionada.getId() == opcionA.getId()) {
            registrarVoto(opcionA, opcionB);
        } else {
            registrarVoto(opcionB, opcionA);
        }

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
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(JuegoActivity.this, R.string.votado, Toast.LENGTH_SHORT).show();
        }
    }

    private void configurarBotones() {
        btnFinalizar = findViewById(R.id.btnFinalizar);
        btnRanking = findViewById(R.id.btnRanking);

        btnFinalizar.setOnClickListener(v ->
                new AlertDialog.Builder(JuegoActivity.this)
                        .setTitle(R.string.finalizar_partida)
                        .setMessage(R.string.confirmar_finalizar)
                        .setPositiveButton(R.string.si, (dialog, which) -> {
                            Intent intent = new Intent(JuegoActivity.this, RankingActivity.class);
                            intent.putExtra("categoria_id", categoriaId);
                            intent.putExtra("desde_finalizar", true);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton(R.string.no, null)
                        .show()
        );

        btnRanking.setOnClickListener(v -> {
            Intent intent = new Intent(JuegoActivity.this, RankingActivity.class);
            intent.putExtra("categoria_id", categoriaId);
            intent.putExtra("desde_finalizar", false);
            startActivity(intent);
        });
    }
}