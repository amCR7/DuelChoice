package com.miapp.duelchoice;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ServidorHelper {

    private static final String SERVIDOR_URL = "http://34.52.179.46:81/";

    // Registrar usuario con foto
    public static String registrar(String username, String email, String password, String nombre, String fotoBase64) {
        String parametros = "username=" + URLEncoder.encode(username) +
                "&email=" + URLEncoder.encode(email) +
                "&password=" + URLEncoder.encode(password) +
                "&nombre=" + URLEncoder.encode(nombre) +
                "&foto=" + URLEncoder.encode(fotoBase64);
        return peticionPost("registro.php", parametros);
    }

    // Login con username y password
    public static String login(String username, String password) {
        String parametros = "username=" + URLEncoder.encode(username) +
                "&password=" + URLEncoder.encode(password);
        return peticionPost("login.php", parametros);
    }

    // Subir/actualizar foto de perfil
    public static String subirFoto(int usuarioId, String fotoBase64) {
        String parametros = "usuario_id=" + usuarioId +
                "&foto=" + URLEncoder.encode(fotoBase64);
        return peticionPost("subir_foto.php", parametros);
    }

    // Petición HTTP POST
    private static String peticionPost(String archivo, String parametros) {
        HttpURLConnection connection = null;
        StringBuilder resultado = new StringBuilder();

        try {
            URL url = new URL(SERVIDOR_URL + archivo);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(parametros);
            out.flush();
            out.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String linea;
                while ((linea = reader.readLine()) != null) {
                    resultado.append(linea);
                }
                reader.close();
            } else {
                return "{\"error\":\"HTTP Error: " + responseCode + "\"}";
            }
        } catch (Exception e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        } finally {
            if (connection != null) connection.disconnect();
        }

        return resultado.toString();
    }
}