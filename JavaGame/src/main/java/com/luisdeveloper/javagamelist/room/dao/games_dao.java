package com.luisdeveloper.javagamelist.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.luisdeveloper.javagamelist.room.entity.games_table;

//**************************************************************************************
//                              CREATED BY LUIS A. SIERRA
//**************************************************************************************

//Crear consultas sql
//***************************************************************************
@Dao
public interface games_dao {

    @Insert
    void insertGame(games_table... games);

    @Update
    void updateGame(games_table... games);

    @Query("UPDATE games SET gameFavoritos = 1 WHERE gameId = :id")
    void updateGameByID(int id);

    @Query("UPDATE games SET gameFavoritos = 0 WHERE gameId = :id")
    void QuitarFavoritosGameByID(int id);

    @Query("DELETE FROM games WHERE gameId = :id")
    void deleteGame(int id);

    @Query("DELETE FROM games")
    void deleteAllGame();

    @Query("Select * FROM games")
    games_table[] getAllGames();

    @Query("Select * FROM games WHERE gameCategoria = :categoria")
    games_table[] selectCategory(String categoria);

    @Query("Select * FROM games WHERE gameFavoritos = :favoritos")
    games_table[] selectFavoritos(int favoritos);

    //@Query("Select count(*) FROM games")
    //games_table[] getCaunt();
}
