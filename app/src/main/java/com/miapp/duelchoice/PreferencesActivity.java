/* package com.miapp.duelchoice;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

    public class PreferencesActivity extends AppCompatActivity {

        private RadioGroup radioGroupIdioma;
        private RadioButton rbEspanol, rbIngles;
        private Button btnGuardar;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_preferencias);

            radioGroupIdioma = findViewById(R.id.radioGroupIdioma);
            rbEspanol = findViewById(R.id.rbEspanol);
            rbIngles = findViewById(R.id.rbIngles);
            btnGuardar = findViewById(R.id.btnGuardarIdioma);

            // Cargar idioma actual
            String idiomaActual = getSharedPreferences("settings", MODE_PRIVATE)
                    .getString("idioma", "es");

            if (idiomaActual.equals("es")) {
                rbEspanol.setChecked(true);
            } else {
                rbIngles.setChecked(true);
            }

            btnGuardar.setOnClickListener(v -> {
                int selectedId = radioGroupIdioma.getCheckedRadioButtonId();
                String codigoIdioma = "es";

                if (selectedId == R.id.rbIngles) {
                    codigoIdioma = "en";
                }

                // Guardar preferencia
                getSharedPreferences("settings", MODE_PRIVATE)
                        .edit()
                        .putString("idioma", codigoIdioma)
                        .apply();

                cambiarIdioma(codigoIdioma);
            });
        }

        private void cambiarIdioma(String codigoIdioma) {
            Locale locale = new Locale(codigoIdioma);
            Locale.setDefault(locale);

            Configuration config = new Configuration();
            config.setLocale(locale);

            getResources().updateConfiguration(config, getResources().getDisplayMetrics());

            Toast.makeText(this, "Idioma cambiado. Reinicia la app.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

 */

