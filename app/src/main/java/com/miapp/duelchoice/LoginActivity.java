package com.miapp.duelchoice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvError;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvError = findViewById(R.id.tvError);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> login());
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            tvError.setText("Completa todos los campos");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
        tvError.setText("");

        new Thread(() -> {
            String respuesta = ServidorHelper.login(username, password);
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);

                try {
                    JSONObject json = new JSONObject(respuesta);
                    if (json.has("success") && json.getBoolean("success")) {
                        JSONObject usuario = json.getJSONObject("usuario");

                        // Guardar todos los datos del usuario
                        getSharedPreferences("app", MODE_PRIVATE)
                                .edit()
                                .putInt("usuario_id", usuario.getInt("id"))
                                .putString("usuario_username", usuario.getString("username"))
                                .putString("usuario_email", usuario.getString("email"))
                                .putString("usuario_nombre", usuario.getString("nombre"))
                                .putString("usuario_foto", usuario.optString("foto_perfil", ""))
                                .apply();

                        Toast.makeText(LoginActivity.this, "Bienvenido " + usuario.getString("nombre"), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
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