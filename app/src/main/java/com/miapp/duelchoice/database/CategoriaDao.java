package com.miapp.duelchoice.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategoriaDao {

    @Query("SELECT * FROM categorias")
    List<Categoria> getAll();

    @Query("SELECT * FROM categorias ORDER BY RANDOM() LIMIT 1")
    Categoria getRandom();

    @Query("SELECT * FROM categorias WHERE id = :id")
    Categoria getById(int id);

    @Query("SELECT * FROM categorias WHERE nombre = :nombre")
    Categoria getByNombre(String nombre);

    @Query("SELECT * FROM categorias ORDER BY RANDOM() LIMIT 1")
    Categoria getCategoriaAleatoria();

    @Insert
    long insert(Categoria categoria);

    @Insert
    void insertAll(Categoria... categorias);

    @Update
    void update(Categoria categoria);

    @Delete
    void delete(Categoria categoria);

    @Query("DELETE FROM categorias")
    void deleteAll();
}