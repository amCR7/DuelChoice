package com.miapp.duelchoice;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etNombre;
    private Button btnRegister;
    private ImageView ivFotoPerfil;
    private TextView tvError;
    private ProgressBar progressBar;
    private static final int PERMISO_CAMARA = 100;
    private static final int CODIGO_CAMARA = 1;
    private String fotoBase64 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etNombre = findViewById(R.id.etNombre);
        btnRegister = findViewById(R.id.btnRegister);
        ivFotoPerfil = findViewById(R.id.ivFotoPerfil);
        tvError = findViewById(R.id.tvError);
        progressBar = findViewById(R.id.progressBar);

        ivFotoPerfil.setOnClickListener(v -> abrirCamara());
        btnRegister.setOnClickListener(v -> register());
    }

    private void abrirCamara() {
        // Comprobar permiso en Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, PERMISO_CAMARA);
                return;
            }
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CODIGO_CAMARA);
        } else {
            Toast.makeText(this, "No hay cámara disponible", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODIGO_CAMARA && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            Bitmap fotoBitmap = (Bitmap) bundle.get("data");
            ivFotoPerfil.setImageBitmap(fotoBitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            fotoBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            byte[] fotoBytes = stream.toByteArray();
            fotoBase64 = Base64.encodeToString(fotoBytes, Base64.DEFAULT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_CAMARA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void register() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || nombre.isEmpty()) {
            tvError.setText("Completa todos los campos");
            return;
        }

        if (password.length() < 6) {
            tvError.setText("La contraseña debe tener al menos 6 caracteres");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);
        tvError.setText("");

        new Thread(() -> {
            String respuesta = ServidorHelper.registrar(username, email, password, nombre, fotoBase64);
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);

                try {
                    JSONObject json = new JSONObject(respuesta);
                    if (json.has("success") && json.getBoolean("success")) {
                        Toast.makeText(RegisterActivity.this, "Registro exitoso. Ahora inicia sesión.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        tvError.setText(json.optString("error", "Error desconocido"));
                    }
                } catch (Exception e) {
                    tvError.setText("Error: " + e.getMessage());
                }
            });
        }).start();
    }
}