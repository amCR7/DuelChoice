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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miapp.duelchoice.database.AppDatabase;
import com.miapp.duelchoice.database.Categoria;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoriaListsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoriaAdapter adapter;
    private AppDatabase db;
    private Button btnVolver;
    private List<Categoria> categorias = new ArrayList<>();

    // Botones de idioma
    private TextView btnES, btnEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_lists);
        cargarIdiomaGuardado();

        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnES = toolbar.findViewById(R.id.btnIdiomaES);
        btnEN = toolbar.findViewById(R.id.btnIdiomaEN);

        String idiomaActual = getSharedPreferences("settings", MODE_PRIVATE)
                .getString("idioma", "es");
        resaltarIdioma(idiomaActual);

        btnES.setOnClickListener(v -> cambiarIdioma("es"));
        btnEN.setOnClickListener(v -> cambiarIdioma("en"));

        // lodemas
        recyclerView = findViewById(R.id.recyclerView);
        btnVolver = findViewById(R.id.btnVolver);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new CategoriaAdapter(categorias, this::onJugarClick);
        recyclerView.setAdapter(adapter);

        db = AppDatabase.getInstance(this);
        btnVolver.setOnClickListener(v -> finish());

        new CargarCategoriasTask().execute();
    }

    private void resaltarIdioma(String idioma) {
        if (idioma.equals("es")) {
            btnES.setBackgroundResource(R.drawable.boton_resaltado);
            btnEN.setBackgroundResource(R.drawable.boton_idioma);
        } else {
            btnEN.setBackgroundResource(R.drawable.boton_resaltado);
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

    private void onJugarClick(Categoria categoria) {
        new AlertDialog.Builder(CategoriaListsActivity.this)
                .setTitle(R.string.confirmar_partida)
                .setMessage(getString(R.string.confirmar_juego,categoria.getNombre()))
                .setPositiveButton(R.string.si, (dialog, which) -> {
                    Intent intent = new Intent(CategoriaListsActivity.this, JuegoActivity.class);
                    intent.putExtra("categoria_id", categoria.getId());
                    intent.putExtra("categoria_nombre", categoria.getNombre());
                    startActivity(intent);
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private class CargarCategoriasTask extends AsyncTask<Void, Void, List<Categoria>> {

        @Override
        protected List<Categoria> doInBackground(Void... params) {
            try {
                return db.categoriaDao().getAll();
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(List<Categoria> lista) {
            categorias.clear();
            categorias.addAll(lista);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new CargarCategoriasTask().execute();
    }
}