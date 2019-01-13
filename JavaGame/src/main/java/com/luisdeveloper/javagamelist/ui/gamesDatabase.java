package com.luisdeveloper.javagamelist.ui;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.luisdeveloper.javagamelist.model.games_object;
import com.luisdeveloper.javagamelist.room.database.room_database;
import com.luisdeveloper.javagamelist.room.entity.games_table;

import java.util.List;

//**************************************************************************************
//                              CREATED BY LUIS A. SIERRA
//**************************************************************************************

public class gamesDatabase {

    private static gamesDatabase instance;
    private static room_database dataBase;
    private byte[] databyte = null;

    public static gamesDatabase daoAccess(room_database appDataBase) {

        if (dataBase == null)
            dataBase = appDataBase;

        if (instance == null)
            instance = new gamesDatabase();
        return instance;
    }

    public void InsertGamestoDb(List<games_object> data) {
        if (dataBase == null)
            return;

        games_table[] games = new games_table[data.size()]; //definir el tamano de lo que va a insertar


        for(int x =0; x<= data.size() -1; x++)
        {
            games_object gameupdate = data.get(x); //definier la pocicion del registro para conseguirlo de la lista en memoria

            //byte[] image = getByteToURL(gameupdate.getPicture());  //imagen to db *** DEPRECATED!!!! ***

            byte[] image = null;

            games[x] = gamesInstance(gameupdate.getId(), gameupdate.getName(), gameupdate.getUrl(),gameupdate.getDesc(),image,gameupdate.getCategoria());

        }

        dataBase.GamesDao().insertGame(games);  //Guardar lista completa!

    }

    public void UpdateGamestoDb(List<games_object> data) {
        if (dataBase == null)
            return;

        games_table[] games = new games_table[data.size()]; //definir el tamano de lo que va a insertar

        for(int x =0; x<= data.size() -1; x++)
        {
            games_object gameupdate = data.get(x); //definier la pocicion del registro para conseguirlo de la lista en memoria

            //byte[] image = getByteToURL(gameupdate.getPicture());  //imagen to db *** DEPRECATED!!!! ***

            byte[] image = null;
            games[x] = gamesInstance(gameupdate.getId(), gameupdate.getName(), gameupdate.getUrl(),gameupdate.getDesc(),image,gameupdate.getCategoria());

        }

        dataBase.GamesDao().updateGame(games);  //actualizar lista completa!

    }

    private games_table gamesInstance(int id, String name, String url, String desc, byte[] image,String categoria) {
        games_table games = new games_table();

        games.id = id;
        games.name = name;
        games.url = url;
        games.desc = desc;
        games.image = image;
        games.categoria = categoria;
        return games;
    }


    /*
    private byte[] getByteToURL(final String url)
    {

        Thread thread =  new Thread(new Runnable() {
        @Override
        public void run() {
        try {

            URL ImagenURL = new URL(url);
            URLConnection conn = ImagenURL.openConnection();
            //conn.setConnectTimeout(5000);
            //conn.setReadTimeout(5000);
            conn.connect();


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(conn.getInputStream(),baos);


            //databyte = baf.toByteArray();

            databyte = baos.toByteArray();

           }
          catch (Exception ex)
          {
            Log.d("ImageDownload","ERROR: " + ex.toString());

          }

          }


        });

        thread.start();


        return databyte;
    }
    */



}
