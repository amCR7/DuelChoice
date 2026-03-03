package com.miapp.duelchoice.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface OpcionDao {

    @Query("SELECT * FROM opciones WHERE categoria_id = :categoriaId")
    List<Opcion> getOpcionesByCategoria(int categoriaId);

    @Query("SELECT * FROM opciones WHERE categoria_id = :categoriaId ORDER BY puntuacion_elo DESC")
    List<Opcion> getRankingByCategoria(int categoriaId);

    @Query("SELECT * FROM opciones WHERE categoria_id = :categoriaId ORDER BY RANDOM() LIMIT 2")
    List<Opcion> getDosOpcionesAleatorias(int categoriaId);

    @Insert
    long insert(Opcion opcion);

    @Update
    void update(Opcion opcion);

    @Query("DELETE FROM opciones WHERE categoria_id = :categoriaId")
    void deleteByCategoria(int categoriaId);


}