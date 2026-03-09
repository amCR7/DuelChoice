package com.miapp.duelchoice;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.miapp.duelchoice.database.AppDatabase;
import com.miapp.duelchoice.database.Categoria;
import com.miapp.duelchoice.database.Opcion;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoriaActivity extends AppCompatActivity {

    private EditText etNombreCategoria;
    private LinearLayout layoutOpcionesContainer;
    private Button btnGuardar, btnCancelar;
    private AppDatabase db;
    private List<EditText> camposOpcion = new ArrayList<>();
    private static final int OPCIONES_INICIALES = 5;

    // Botones de idioma
    private TextView btnES, btnEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_detail);
        cargarIdiomaGuardado();


        // Configurar toolbar con idioma
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
        etNombreCategoria = findViewById(R.id.etNombreCategoria);
        layoutOpcionesContainer = findViewById(R.id.layoutOpcionesContainer);
        btnGuardar = findViewById(R.id.btnGuardarCategoria);
        btnCancelar = findViewById(R.id.btnCancelar);

        db = AppDatabase.getInstance(this);

        for (int i = 0; i < OPCIONES_INICIALES; i++) {
            agregarFilaOpcion(false);
        }

        btnGuardar.setOnClickListener(v -> guardarCategoriaYOpciones());
        btnCancelar.setOnClickListener(v -> finish());
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

    // Resto de métodos (agregarFilaOpcion, guardarCategoriaYOpciones) sin cambios
    private void agregarFilaOpcion(boolean esUltimaFila) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View filaOpcion = inflater.inflate(R.layout.item_opcion_editable, layoutOpcionesContainer, false);

        EditText etOpcion = filaOpcion.findViewById(R.id.etOpcionTexto);
        Button btnAnadir = filaOpcion.findViewById(R.id.btnAnadirFila);

        camposOpcion.add(etOpcion);

        btnAnadir.setOnClickListener(v -> {
            String texto = etOpcion.getText().toString().trim();
            if (texto.isEmpty()) {
                Toast.makeText(this, R.string.escribe_opcion, Toast.LENGTH_SHORT).show();
                return;
            }

            agregarFilaOpcion(true);
            btnAnadir.setEnabled(false);
            btnAnadir.setAlpha(0.5f);
        });

        if (esUltimaFila) {
            etOpcion.requestFocus();
        }

        layoutOpcionesContainer.addView(filaOpcion);
    }

    private void guardarCategoriaYOpciones() {
        String nombreCategoria = etNombreCategoria.getText().toString().trim();

        if (nombreCategoria.isEmpty()) {
            Toast.makeText(this, R.string.nombre_obligatorio, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> opcionesValidas = new ArrayList<>();
        for (EditText et : camposOpcion) {
            String texto = et.getText().toString().trim();
            if (!texto.isEmpty()) {
                opcionesValidas.add(texto);
            }
        }

        if (opcionesValidas.isEmpty()) {
            Toast.makeText(this, R.string.minimo_una_opcion, Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                Categoria nuevaCategoria = new Categoria(nombreCategoria, R.drawable.ic_launcher_foreground);
                long categoriaId = db.categoriaDao().insert(nuevaCategoria);

                for (String texto : opcionesValidas) {
                    Opcion opcion = new Opcion(texto, R.drawable.imagen_opcion_a, (int) categoriaId);
                    db.opcionDao().insert(opcion);
                }

                runOnUiThread(() -> {
                    NotificacionHelper.mostrarNotificacion(CategoriaActivity.this, nombreCategoria);
                    Toast.makeText(CategoriaActivity.this,
                            getString(R.string.categoria_creada) + " " + opcionesValidas.size() + " " + getString(R.string.opciones),
                            Toast.LENGTH_SHORT).show();
                    finish();
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(CategoriaActivity.this, R.string.error_guardar, Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}