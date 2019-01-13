package com.luisdeveloper.javagamelist.rest_services;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.luisdeveloper.javagamelist.model.games_object;
//**************************************************************************************
//                              CREATED BY LUIS A. SIERRA
//**************************************************************************************

public class game_json {

    public static List<games_object> parseJSON(String content) {
        try {

            JSONObject json = new JSONObject(content);

            List<games_object> gameList = new ArrayList<>();

            JSONArray obj = json.getJSONArray("Games");

            for (int x=0; x<=json.getJSONArray("Games").length() - 1; x++)
            {
                games_object games = new games_object();

                games.setId(obj.getJSONObject(x).getInt("id"));
                games.setName(obj.getJSONObject(x).getString("name"));
                games.setUrl(obj.getJSONObject(x).getString("url"));
                games.setDesc(obj.getJSONObject(x).getString("desc"));
                games.setPicture(obj.getJSONObject(x).getString("image"));
                games.setCategoria(obj.getJSONObject(x).getString("categoria"));

                gameList.add(games);
            }
            return gameList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    private static Context getBaseContext() {
        // TODO Auto-generated method stub
        return null;
    }
}
