package com.luisdeveloper.javagamelist.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.luisdeveloper.javagamelist.model.games_object;
import com.luisdeveloper.javagamelist.room.database.room_database;
import com.luisdeveloper.javagamelist.room.entity.games_table;

import java.util.ArrayList;
import java.util.List;

public class prosessDb extends MainActivity {

    Context mContext;

    public prosessDb(Context context) {
        this.mContext = context;
    }

    //llenar lista de canales
    protected void updateDatabase(List<games_object> data) {

        room_database database = room_database.getAppDatabase(this);;


        ArrayList<String> GameName = new ArrayList<>();
        String msg = "";


        //GameName.add(database.GamesDao().getAllGames()[0].name);

        for (games_table games: database.GamesDao().getAllGames()) {  //busca datos en la base de datos
            GameName.add(games.name); //llena lista
        }

        int old = GameName.size();
        int New = data.size();
        int resp;
        resp = old - New;


        if (GameName.size() != 0)  //actualiza
        {
            gamesDatabase.daoAccess(database).UpdateGamestoDb(data);
            msg = "actualizaron " + Integer.toString(resp) + " registros ";

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("listOptionsPref","0");
            editor.putBoolean("checkboxPref",false);
            editor.commit();
            //prefs.setString("listOptionsPref", "0");

            SharedPreferences cargaInicial = mContext.getSharedPreferences("CargaInicial", mContext.MODE_PRIVATE);
            SharedPreferences.Editor cargaInicialEditor = cargaInicial.edit();
            cargaInicialEditor.putString("cargado","1");
            cargaInicialEditor.commit();
        }
        else if (GameName.size() == 0) //inserta
        {
            gamesDatabase.daoAccess(database).InsertGamestoDb(data);
            msg = "insertaron " + Integer.toString(resp) + " registros ";
        }

        Toast.makeText(mContext.getApplicationContext(),"Se "+msg+" correctamente!!",Toast.LENGTH_SHORT).show();
        //BeginTransationFragment();
    }
}
