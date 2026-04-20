package com.miapp.duelchoice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class PerfilActivity extends AppCompatActivity {

    private TextView tvUsername, tvNombre, tvEmail;
    private ImageView ivFotoPerfil;
    private Button btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        tvUsername = findViewById(R.id.tvUsername);
        tvNombre = findViewById(R.id.tvNombre);
        tvEmail = findViewById(R.id.tvEmail);
        ivFotoPerfil = findViewById(R.id.ivFotoPerfil);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // Cargar datos del SharedPreferences
        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
        String username = prefs.getString("usuario_username", "");
        String nombre = prefs.getString("usuario_nombre", "");
        String email = prefs.getString("usuario_email", "");
        String fotoPath = prefs.getString("usuario_foto", "");

        tvUsername.setText(username);
        tvNombre.setText(nombre);
        tvEmail.setText(email);

        // Cargar foto (si existe)
        if (fotoPath != null && !fotoPath.isEmpty()) {
            Glide.with(this).load("http://34.52.179.46:81/" + fotoPath).into(ivFotoPerfil);
        }

        btnCerrarSesion.setOnClickListener(v -> {
            // Borrar datos de sesión
            prefs.edit().clear().apply();
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}