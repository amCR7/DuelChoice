package com.miapp.duelchoice;

import android.content.Context;

import com.miapp.duelchoice.database.AppDatabase;
import com.miapp.duelchoice.database.Categoria;
import com.miapp.duelchoice.database.Opcion;

public class DataLoader {
    private AppDatabase db;

    public DataLoader(Context context) {
        this.db = AppDatabase.getInstance(context);
    }

    public void cargarDatosSiEsNecesario() {
        try {
            int numCategorias = db.categoriaDao().getAll().size();

            if (numCategorias == 0) {
                cargarDatosIniciales();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarDatosIniciales() {
        try {
            // Categoría 1: Comida
            Categoria comida = new Categoria("Comida", R.drawable.ic_launcher_foreground);
            long comidaId = db.categoriaDao().insert(comida);

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

            // Categoría 2: Películas
            Categoria peliculas = new Categoria("Películas", R.drawable.ic_launcher_foreground);
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

            // Categoría 3: Videojuegos
            Categoria videojuegos = new Categoria("Videojuegos", R.drawable.ic_launcher_foreground);
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}