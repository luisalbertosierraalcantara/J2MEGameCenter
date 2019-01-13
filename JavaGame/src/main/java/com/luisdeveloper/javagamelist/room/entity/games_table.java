package com.luisdeveloper.javagamelist.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

//**************************************************************************************
//                              CREATED BY LUIS A. SIERRA
//**************************************************************************************
//Crear tabla y sus campos
//***************************************************************************
@Entity (tableName = "games")
public class games_table {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "gameId")
    public int id;

    @ColumnInfo(name = "gameName")
    public String name;

    @ColumnInfo(name = "gameUrl")
    public String url;

    @ColumnInfo(name = "gameDesc")
    public String desc;

    @ColumnInfo(name = "gameCategoria")
    public String categoria;

    @ColumnInfo(name = "gameFavoritos")
    public int favoritos;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte[] image;

    //@Ignore
    //public Bitmap picture;
}


