package com.miapp.duelchoice;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.miapp.duelchoice.database.AppDatabase;
import com.miapp.duelchoice.database.Categoria;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button btnPlay, btnShop, btnHelp;
    private ImageButton btnLogin, btnMapa;
    private AppDatabase db;
    private static final int PERMISO_NOTIFICACIONES = 100;

    private TextView btnES;
    private TextView btnEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate ejecutado");

        cargarIdiomaGuardado();
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnES = toolbar.findViewById(R.id.btnIdiomaES);
        btnEN = toolbar.findViewById(R.id.btnIdiomaEN);

        String idiomaActual = getSharedPreferences("settings", MODE_PRIVATE)
                .getString("idioma", "es");
        resaltarIdioma(idiomaActual);

        btnES.setOnClickListener(v -> cambiarIdioma("es"));
        btnEN.setOnClickListener(v -> cambiarIdioma("en"));

        db = AppDatabase.getInstance(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            solicitarPermisoNotificaciones();
        }

        new CargarDatosInicialesTask().execute();
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

    private void initBotones() {
        btnPlay = findViewById(R.id.btnPlay);
        btnShop = findViewById(R.id.btnChoose);
        btnHelp = findViewById(R.id.btnCreate);
        btnLogin = findViewById(R.id.btnLogin);

        btnPlay.setOnClickListener(v -> new JugarAleatorioTask().execute());

        btnShop.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CategoriaListsActivity.class))
        );

        btnHelp.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CategoriaActivity.class);
            intent.putExtra("categoria_id", -1);
            intent.putExtra("categoria_nombre", "");
            startActivity(intent);
        });

        // Botón login: abre LoginActivity
        // Botón login: abre LoginActivity o PerfilActivity según sesión
        btnLogin.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
            int usuarioId = prefs.getInt("usuario_id", -1);

            if (usuarioId != -1) {
                // Hay sesión → abrir PerfilActivity
                Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
                startActivity(intent);
            } else {
                // No hay sesión → abrir LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        //Boton ubicacion
        btnMapa = findViewById(R.id.btnMapa);
        btnMapa.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        });
    }

    private void solicitarPermisoNotificaciones() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    PERMISO_NOTIFICACIONES);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_NOTIFICACIONES) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class CargarDatosInicialesTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                DataLoader dataLoader = new DataLoader(MainActivity.this);
                dataLoader.cargarDatosSiEsNecesario();
                return !db.categoriaDao().getAll().isEmpty();
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean hayCategorias) {
            initBotones();
        }
    }

    private class JugarAleatorioTask extends AsyncTask<Void, Void, Categoria> {
        @Override
        protected Categoria doInBackground(Void... params) {
            try {
                List<Categoria> categorias = db.categoriaDao().getAll();
                if (categorias == null || categorias.isEmpty()) return null;
                return db.categoriaDao().getRandom();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Categoria categoria) {
            if (categoria == null) {
                Toast.makeText(MainActivity.this, "No hay categorías", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(MainActivity.this, JuegoActivity.class);
            intent.putExtra("categoria_id", categoria.getId());
            intent.putExtra("categoria_nombre", categoria.getNombre());
            startActivity(intent);
        }
    }
}