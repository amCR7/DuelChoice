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

    @ColumnInfo(name = "descripcion")
    private String descripcion;

    @ColumnInfo(name = "icono_res_id")
    private int iconoResId; // recurso drawable para el icono

    // Constructor
    public Categoria(String nombre, String descripcion, int iconoResId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.iconoResId = iconoResId;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getIconoResId() { return iconoResId; }
    public void setIconoResId(int iconoResId) { this.iconoResId = iconoResId; }
}