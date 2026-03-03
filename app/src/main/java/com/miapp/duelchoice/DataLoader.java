package com.miapp.duelchoice;

import android.content.Context;
import android.util.Log;

import com.miapp.duelchoice.database.AppDatabase;
import com.miapp.duelchoice.database.Categoria;
import com.miapp.duelchoice.database.Opcion;

public class DataLoader {
    private static final String TAG = "DATALOADER";
    private AppDatabase db;
    private Context context;

    public DataLoader(Context context) {
        this.context = context;
        this.db = AppDatabase.getInstance(context);
    }

    public void cargarDatosSiEsNecesario() {
        Log.d(TAG, "=== INICIANDO DATALOADER ===");

        try {
            // Verificar si ya hay categorías
            int numCategorias = db.categoriaDao().getAll().size();
            Log.d(TAG, "Categorías existentes: " + numCategorias);

            if (numCategorias == 0) {
                Log.d(TAG, "No hay datos. Cargando datos iniciales...");
                cargarDatosIniciales();
            } else {
                Log.d(TAG, "Ya hay datos en la BD. No se cargan iniciales.");
            }

            // Verificar después de la carga
            int categoriasFinal = db.categoriaDao().getAll().size();
            Log.d(TAG, "=== DATALOADER FINALIZADO ===");
            Log.d(TAG, "Categorías totales: " + categoriasFinal);

        } catch (Exception e) {
            Log.e(TAG, "ERROR en DataLoader: " + e.getMessage(), e);
        }
    }

    private boolean cargarDatosIniciales() {
        Log.d(TAG, "Cargando datos iniciales...");

        try {
            // ============================================
            // CATEGORÍA 1: COMIDA
            // ============================================
            Categoria comida = new Categoria(
                    "Comida",
                    "¿Qué prefieres comer?",
                    android.R.drawable.ic_dialog_info  // ✅ ESTE SÍ FUNCIONA
            );

            long comidaId = db.categoriaDao().insert(comida);
            Log.d(TAG, "Categoría 'Comida' insertada con ID: " + comidaId);

            // Insertar opciones de comida
            Opcion[] opcionesComida = {
                    new Opcion("Pizza", R.drawable.imagen_opcion_a, (int) comidaId),
                    new Opcion("Hamburguesa", R.drawable.imagen_opcion_b, (int) comidaId),
                    new Opcion("Sushi", R.drawable.imagen_opcion_a, (int) comidaId),
                    new Opcion("Tacos", R.drawable.imagen_opcion_b, (int) comidaId),
                    new Opcion("Pasta", R.drawable.imagen_opcion_a, (int) comidaId)
            };

            for (Opcion op : opcionesComida) {
                db.opcionDao().insert(op);
            }

            // ============================================
            // CATEGORÍA 2: PELÍCULAS
            // ============================================
            Categoria peliculas = new Categoria(
                    "Películas",
                    "¿Qué película prefieres?",
                    android.R.drawable.ic_dialog_alert  // ✅ ESTE SÍ FUNCIONA
            );

            long peliculasId = db.categoriaDao().insert(peliculas);

            Opcion[] opcionesPeliculas = {
                    new Opcion("Star Wars", R.drawable.imagen_opcion_a, (int) peliculasId),
                    new Opcion("Matrix", R.drawable.imagen_opcion_b, (int) peliculasId),
                    new Opcion("Avatar", R.drawable.imagen_opcion_a, (int) peliculasId),
                    new Opcion("Titanic", R.drawable.imagen_opcion_b, (int) peliculasId),
                    new Opcion("Inception", R.drawable.imagen_opcion_a, (int) peliculasId)
            };

            for (Opcion op : opcionesPeliculas) {
                db.opcionDao().insert(op);
            }

            // ============================================
            // CATEGORÍA 3: VIDEOJUEGOS
            // ============================================
            Categoria videojuegos = new Categoria(
                    "Videojuegos",
                    "¿Qué juego prefieres?",
                    android.R.drawable.ic_menu_help  // ✅ ESTE SÍ FUNCIONA
            );

            long videojuegosId = db.categoriaDao().insert(videojuegos);

            Opcion[] opcionesVideojuegos = {
                    new Opcion("Zelda", R.drawable.imagen_opcion_a, (int) videojuegosId),
                    new Opcion("Mario", R.drawable.imagen_opcion_b, (int) videojuegosId),
                    new Opcion("Minecraft", R.drawable.imagen_opcion_a, (int) videojuegosId),
                    new Opcion("Fortnite", R.drawable.imagen_opcion_b, (int) videojuegosId),
                    new Opcion("Call of Duty", R.drawable.imagen_opcion_a, (int) videojuegosId)
            };

            for (Opcion op : opcionesVideojuegos) {
                db.opcionDao().insert(op);
            }

            Log.d(TAG, "Todas las categorías y opciones insertadas");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error cargando datos iniciales: " + e.getMessage(), e);
            return false;
        }
    }
}