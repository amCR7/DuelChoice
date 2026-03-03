package com.miapp.duelchoice.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(
        entities = {Categoria.class, Opcion.class},
        version = 2,  // Incrementado de 1 a 2
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract CategoriaDao categoriaDao();
    public abstract OpcionDao opcionDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "duelchoice_database"
                            )
                            .fallbackToDestructiveMigration()  // 👈 IMPORTANTE
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}