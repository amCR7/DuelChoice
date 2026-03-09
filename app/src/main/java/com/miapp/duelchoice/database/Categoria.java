package com.miapp.duelchoice.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "categorias")
public class Categoria {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "nombre")
    private String nombre;



    @ColumnInfo(name = "icono_res_id")
    private int iconoResId;

    // Constructor
    public Categoria(String nombre, int iconoResId) {
        this.nombre = nombre;
        this.iconoResId = iconoResId;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getIconoResId() { return iconoResId; }
    public void setIconoResId(int iconoResId) { this.iconoResId = iconoResId; }
}