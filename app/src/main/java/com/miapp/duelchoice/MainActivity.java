package com.miapp.duelchoice;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.miapp.duelchoice.database.AppDatabase;
import com.miapp.duelchoice.database.Categoria;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnPlay, btnShop, btnHelp;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);

        // Cargar datos iniciales con AsyncTask
        new CargarDatosInicialesTask().execute();
    }

    private void initBotones() {
        btnPlay = findViewById(R.id.btnPlay);
        btnShop = findViewById(R.id.btnShop);
        btnHelp = findViewById(R.id.btnHelp);

        btnPlay.setOnClickListener(v -> new JugarAleatorioTask().execute());

        btnShop.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CategoriaListsActivity.class);
            startActivity(intent);
        });

        btnHelp.setOnClickListener(v -> {
            NuevaCategoriaDialog dialog = new NuevaCategoriaDialog(categoria -> {
                Toast.makeText(MainActivity.this,
                        "Categoría creada: " + categoria.getNombre(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, JuegoActivity.class);
                intent.putExtra("categoria_id", categoria.getId());
                intent.putExtra("categoria_nombre", categoria.getNombre());
                startActivity(intent);
            });
            dialog.show(getSupportFragmentManager(), "NuevaCategoriaDialog");
        });
    }

    // AsyncTask para cargar datos iniciales
    private class CargarDatosInicialesTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            Toast.makeText(MainActivity.this, "Cargando datos...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                DataLoader dataLoader = new DataLoader(MainActivity.this);
                dataLoader.cargarDatosSiEsNecesario();

                List<Categoria> categorias = db.categoriaDao().getAll();
                return categorias != null && !categorias.isEmpty();
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean hayCategorias) {
            initBotones();

            if (hayCategorias) {
                Toast.makeText(MainActivity.this,
                        "Datos cargados correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this,
                        "No hay categorías. Crea una nueva.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // AsyncTask para jugar con categoría aleatoria
    private class JugarAleatorioTask extends AsyncTask<Void, Void, Categoria> {

        @Override
        protected Categoria doInBackground(Void... params) {
            try {
                List<Categoria> categorias = db.categoriaDao().getAll();
                if (categorias == null || categorias.isEmpty()) {
                    return null;
                }
                return db.categoriaDao().getRandom();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Categoria categoria) {
            if (categoria == null) {
                Toast.makeText(MainActivity.this,
                        "No hay categorías disponibles", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(MainActivity.this, JuegoActivity.class);
            intent.putExtra("categoria_id", categoria.getId());
            intent.putExtra("categoria_nombre", categoria.getNombre());
            intent.putExtra("modo", "aleatorio");
            startActivity(intent);
        }
    }
}