package com.miapp.duelchoice.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "opciones",
        foreignKeys = @ForeignKey(
                entity = Categoria.class,
                parentColumns = "id",
                childColumns = "categoria_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("categoria_id")})
public class Opcion {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "texto")
    private String texto;

    @ColumnInfo(name = "imagen_id")  // NUEVO
    private int imagenId;              // Resource ID de la imagen

    @ColumnInfo(name = "categoria_id")
    private int categoriaId;

    @ColumnInfo(name = "puntuacion_elo")
    private int puntuacionElo;

    @ColumnInfo(name = "veces_jugadas")
    private int vecesJugadas;

    @ColumnInfo(name = "veces_ganadas")
    private int vecesGanadas;

    @ColumnInfo(name = "veces_perdidas")
    private int vecesPerdidas;

    // Constructor actualizado
    public Opcion(String texto, int imagenId, int categoriaId) {
        this.texto = texto;
        this.imagenId = imagenId;
        this.categoriaId = categoriaId;
        this.puntuacionElo = 1500;
        this.vecesJugadas = 0;
        this.vecesGanadas = 0;
        this.vecesPerdidas = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public int getImagenId() { return imagenId; }
    public void setImagenId(int imagenId) { this.imagenId = imagenId; }

    public int getCategoriaId() { return categoriaId; }
    public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }

    public int getPuntuacionElo() { return puntuacionElo; }
    public void setPuntuacionElo(int puntuacionElo) { this.puntuacionElo = puntuacionElo; }

    public int getVecesJugadas() { return vecesJugadas; }
    public void setVecesJugadas(int vecesJugadas) { this.vecesJugadas = vecesJugadas; }

    public int getVecesGanadas() { return vecesGanadas; }
    public void setVecesGanadas(int vecesGanadas) { this.vecesGanadas = vecesGanadas; }

    public int getVecesPerdidas() { return vecesPerdidas; }
    public void setVecesPerdidas(int vecesPerdidas) { this.vecesPerdidas = vecesPerdidas; }
}