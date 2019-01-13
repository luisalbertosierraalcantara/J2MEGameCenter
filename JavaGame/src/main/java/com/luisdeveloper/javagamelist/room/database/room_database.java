package com.luisdeveloper.javagamelist.room.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.luisdeveloper.javagamelist.room.dao.games_dao;
import com.luisdeveloper.javagamelist.room.entity.games_table;

//**************************************************************************************
//                              CREATED BY LUIS A. SIERRA
//**************************************************************************************
@Database(entities = {games_table.class}, version = 1)
public abstract class room_database extends RoomDatabase {

    public abstract games_dao GamesDao();
    private static room_database instance;

    public static room_database getAppDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    room_database.class,
                    "games_db")
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}
