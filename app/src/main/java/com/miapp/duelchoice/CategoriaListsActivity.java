package com.miapp.duelchoice;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miapp.duelchoice.database.AppDatabase;
import com.miapp.duelchoice.database.Categoria;

import java.util.ArrayList;
import java.util.List;

public class CategoriaListsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoriaAdapter adapter;
    private AppDatabase db;
    private Button btnVolver;
    private List<Categoria> categorias = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_lists);

        // Inicializar vistas
        recyclerView = findViewById(R.id.recyclerView);
        btnVolver = findViewById(R.id.btnVolver);

        if (recyclerView == null || btnVolver == null) {
            Toast.makeText(this, "Error al cargar la interfaz", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // Inicializar adaptador con lista vacía
        adapter = new CategoriaAdapter(categorias, this::onJugarClick);
        recyclerView.setAdapter(adapter);

        // Base de datos
        db = AppDatabase.getInstance(this);
        if (db == null) {
            Toast.makeText(this, "Error: Base de datos no disponible", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Botón VOLVER
        btnVolver.setOnClickListener(v -> finish());

        // Cargar categorías usando AsyncTask (como en los PDFs)
        new CargarCategoriasTask().execute();
    }

    private void onJugarClick(Categoria categoria) {
        try {
            ConfirmarJuegoDialog dialog = new ConfirmarJuegoDialog(categoria,
                    new ConfirmarJuegoDialog.OnConfirmacionListener() {
                        @Override
                        public void onConfirmar() {
                            Intent intent = new Intent(CategoriaListsActivity.this, JuegoActivity.class);
                            intent.putExtra("categoria_id", categoria.getId());
                            intent.putExtra("categoria_nombre", categoria.getNombre());
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelar() {
                            // No hacer nada
                        }
                    });

            dialog.show(getSupportFragmentManager(), "ConfirmarJuegoDialog");

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // AsyncTask para cargar categorías (como en los PDFs)
    private class CargarCategoriasTask extends AsyncTask<Void, Void, List<Categoria>> {

        @Override
        protected void onPreExecute() {
            // Opcional: mostrar algún indicador de carga
        }

        @Override
        protected List<Categoria> doInBackground(Void... params) {
            try {
                // Esto se ejecuta en segundo plano
                return db.categoriaDao().getAll();
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(List<Categoria> lista) {
            // Esto se ejecuta en el hilo principal

            if (lista == null || lista.isEmpty()) {
                Toast.makeText(CategoriaListsActivity.this,
                        "No hay categorías disponibles. Crea una nueva.", Toast.LENGTH_LONG).show();
            }

            // Actualizar la lista del adaptador
            categorias.clear();
            categorias.addAll(lista);

            // Notificar al adaptador que los datos cambiaron
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar categorías cada vez que se vuelve a esta actividad
        new CargarCategoriasTask().execute();
    }
}